package org.example;

import org.example.data.DataHandler;
import org.example.lending.LendingActionListener;
import org.example.lending.LendingHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;
import java.sql.Date;

public class Lending extends JPanel {
    private final DataHandler dataHandler;
    private final LendingHelper lendingHelper = new LendingHelper();
    private final List<String> borrowedBooks = new ArrayList<>();

    private Map<String, List<Object>> lendingMap;
    private Map<String, String> booksId;
    private JTable lendingTable;
    private DefaultTableModel lendingTableModel;
    private JComboBox<String> bookComboBox;
    private JComboBox<String> memberComboBox;
    private List<String> members;
    private List<String> books;

    /**
     * Konstruktor der Klasse Lending.
     * Initialisiert die Benutzeroberfläche und lädt die aktuellen Ausleihen.
     *
     * @param dataHandler Eine Instanz von DataHandler zur Verwaltung der Daten.
     */
    public Lending(DataHandler dataHandler) {
        setLayout(new BorderLayout());
        this.lendingMap = dataHandler.getLendingMap();
        this.dataHandler = dataHandler;

        createBooksTable();
        createWidgets();
        filterBooks("");
        filterMembers("");
    }

    /**
     * Erstellt die Benutzeroberfläche für die Buchausleihe.
     */
    private void createWidgets() {
        JScrollPane scrollPane = new JScrollPane(lendingTable);

        JPanel inputPanel = createInputPanel();
        JPanel buttonPanel = createButtonPanel();

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Erstellt das Eingabepanel für die Buch- und Mitgliedsauswahl.
     *
     * @return Ein JPanel, das die Eingabe-Widgets enthält.
     */
    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        JLabel bookLabel = new JLabel("Buch:");
        JLabel memberLabel = new JLabel("Mitglied:");

        bookComboBox = new JComboBox<>();
        shapeComboBox(bookComboBox, true);

        memberComboBox = new JComboBox<>();
        shapeComboBox(memberComboBox, false);

        inputPanel.add(bookLabel);
        inputPanel.add(bookComboBox);
        inputPanel.add(memberLabel);
        inputPanel.add(memberComboBox);

        return inputPanel;
    }

    /**
     * Konfiguriert eine JComboBox zur dynamischen Filterung der Einträge basierend auf Benutzereingaben.
     *
     * @param comboBox Die zu konfigurierende JComboBox.
     * @param book Wenn true, wird die Buchliste gefiltert, sonst die Mitgliederliste.
     */
    private void shapeComboBox(JComboBox<?> comboBox, boolean book) {
        comboBox.setEditable(true);
        comboBox.setMaximumRowCount(10);
        ComboBoxEditor editor = comboBox.getEditor();
        Component editorComponent = editor.getEditorComponent();
        editorComponent.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                String inputText = ((JTextField) editorComponent).getText();

                if (book) {
                    filterBooks(inputText);
                } else {
                    filterMembers(inputText);
                }
            }
        });
    }

    /**
     * Erstellt das Buttonpanel mit den Schaltflächen zum Ausleihen und Zurückgeben von Büchern.
     *
     * @return Ein JPanel, das die Schaltflächen enthält.
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        JButton lendingButton = new JButton("Ausleihen");
        JButton returnButton = new JButton("Zurückgeben");

        lendingButton.addActionListener(_ -> lendBookActionListener());
        returnButton.addActionListener(_ -> returnBookActionListener());

        buttonPanel.add(lendingButton);
        buttonPanel.add(returnButton);

        return buttonPanel;
    }

    /**
     * Erstellt die Tabelle zur Anzeige der ausgeliehenen Bücher.
     */
    private void createBooksTable() {
        //String[] columnNames = {"Buch ID", "Buch", "Mitglied ID", "Mitglied", "Ausleihdatum", "Rückgabedatum"};
        Font inputFont = new Font("Arial", Font.PLAIN,15);

        createDefaultTableModel();

        lendingTable = new JTable(lendingTableModel);
        lendingTable.setFont(inputFont);

        // Fügt Bücher aus der Datenbank der Tabelle hinzu
        for (String keyMap : lendingMap.keySet()) {
            Object[] values = lendingMap.get(keyMap).toArray();
            String bookId = String.valueOf(values[0]);

            lendingTableModel.addRow(values);
            borrowedBooks.add(bookId);
        }

        lendingTable.addMouseListener(lendingHelper.mouseListener());
    }

    /**
     * Erstellt das DefaultTableModel für die Ausleih-Tabelle.
     */
    public void createDefaultTableModel() {
        String[] columnNames = {"Buch ID", "Buch", "Mitglied ID", "Mitglied", "Ausleihdatum", "Rückgabedatum"};

        lendingTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                int confirmMessage = JOptionPane.showConfirmDialog(
                        new JOptionPane(),
                        "Soll die Änderung gespeichert werden?",
                        columnNames[column],
                        JOptionPane.YES_NO_OPTION
                );

                if (confirmMessage == JOptionPane.YES_OPTION) {
                    List<String> resultList = saveChanges(aValue, row, column);

                    if (resultList.size() == 2) {
                        int id = Integer.parseInt(resultList.get(0));
                        String nameTitle = resultList.get(1);

                        super.setValueAt(id, row, column - 1);
                        super.setValueAt(nameTitle, row, column);
                    }  else if (resultList.size() == 1) {
                        String dateStr = resultList.getFirst();
                        Date date = Date.valueOf(dateStr);

                        super.setValueAt(date, row, column);
                        lendingHelper.updateChangeDate(lendingTableModel, row, lendingMap);
                    }
                }
            }
        };
    }

    /**
     * Speichert Änderungen in der Ausleih-Tabelle.
     *
     * @param aValue Das neue Wertobjekt.
     * @param row    Die Zeile, in der die Änderung vorgenommen wurde.
     * @param column Die Spalte, in der die Änderung vorgenommen wurde.
     * @return Eine Liste der aktualisierten Werte.
     */
    private List<String> saveChanges(Object aValue, int row, int column) {
        return switch (column) {
            case 1 -> lendingHelper.changeBook(booksId, aValue, borrowedBooks, lendingTableModel, row, lendingMap);
            case 3 -> lendingHelper.changeMember(aValue, lendingTableModel, row);
            case 4, 5 -> lendingHelper.changeDate(aValue);
            default -> new ArrayList<>();
        };
    }

    /**
     * Aktion zum Zurückgeben eines Buches.
     * Aktualisiert die Ausleihtabelle und die Liste der ausgeliehenen Bücher.
     */
    private void returnBookActionListener() {
        boolean returnBool = lendingHelper.returnBook(lendingTableModel);

        if (returnBool) {
            lendingMap = dataHandler.getLendingMap();
            String bookID = lendingHelper.removedBookId;
            borrowedBooks.remove(bookID);
            filterBooks("");
        }
    }

    /**
     * Aktion zum Ausleihen eines Buches.
     * Aktualisiert die Ausleihtabelle und die Liste der ausgeliehenen Bücher.
     */
    private void lendBookActionListener() {
        LendingActionListener actionListener;
        actionListener = new LendingActionListener(dataHandler, bookComboBox, memberComboBox);

        boolean returnBool = actionListener.lendBook(0, 0, 0, books, members, lendingTableModel);

        if (returnBool) {
            String bookTitle = (String) bookComboBox.getEditor().getItem();
            String bookID = booksId.get(bookTitle);

            lendingMap = dataHandler.getLendingMap();
            borrowedBooks.add(bookID);

            filterBooks("");
            filterMembers("");
        }
    }

    /**
     * Filtert die Bücher basierend auf dem Benutzereingabetext.
     *
     * @param query Der Benutzereingabetext zur Filterung der Bücher.
     */
    private void filterBooks(String query) {
        Map<String, List<Object>> booksMap = dataHandler.getBookMap();
        books = new ArrayList<>();
        booksId = new HashMap<>();

        for (String key : booksMap.keySet()) {
            List<Object> values = booksMap.get(key);
            String bookTitle = (String) values.getFirst();
            books.add(bookTitle);
            booksId.put(bookTitle, key);
        }

        bookComboBox.removeAllItems();

        for (String book : books) {
            if (book.toLowerCase().contains(query.toLowerCase())) {
                String bookID = booksId.get(book);

                if (borrowedBooks.contains(bookID)) {
                    book = "(ausgeliehen) " + book;
                }

                bookComboBox.addItem(book);
            }
        }

        ((JTextField) bookComboBox.getEditor().getEditorComponent()).setText(query);
    }

    /**
     * Filtert die Mitglieder basierend auf dem Benutzereingabetext.
     *
     * @param query Der Benutzereingabetext zur Filterung der Mitglieder.
     */
    private void filterMembers(String query) {
        Map<String, List<Object>> membersMap = dataHandler.getMemberMap();
        members = new ArrayList<>();

        for (Map.Entry<String, List<Object>> entry : membersMap.entrySet()) {
            List<Object> values = entry.getValue();
            members.add(values.getFirst().toString());
        }

        memberComboBox.removeAllItems();

        for (String member : members) {
            if (member.toLowerCase().contains(query.toLowerCase())) {
                memberComboBox.addItem(member);
            }
        }

        ((JTextField) memberComboBox.getEditor().getEditorComponent()).setText(query);
    }
}
