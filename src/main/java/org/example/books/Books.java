package org.example.books;

import org.example.DataHandler;
import org.example.ISBNValidator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * Diese Klasse stellt das {@code JPanel} für die Verwaltung von Büchern dar.
 * */
public class Books extends JPanel {
    private final DataHandler dataHandler;
    private final Map<String, List<Object>> booksMap;
    private final JPanel mainPanel;

    private DefaultTableModel booksTableModel;
    private JTable booksTable;
    private Map<String, List<Object>> currentBook;

    /**
     * Konstruktor für das Books-Panel.
     *
     * @param dataHandler Handler für Datenoperationen.
     */
    public Books(DataHandler dataHandler) {
        setLayout(new BorderLayout());
        this.dataHandler = dataHandler;
        this.booksMap = dataHandler.getBookMap();
        mainPanel = new JPanel(new BorderLayout(10,10));

        createBooksTable();
        createWidgets();
    }

    /**
     * Erstellt die Tabelle für Bücher und setzt das Table Model.
     * */
    private void createBooksTable() {
        String[] columnNames = {"Titel", "Autor", "ISBN"};
        Font inputFont = new Font("Arial", Font.PLAIN,15);

        booksTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            // Beim Bearbeiten eines Datensatzes wird nur geändert,
            // wenn die Änderung bestätigt wird
            public void setValueAt(Object aValue, int row, int column) {
                int confirmMessage = JOptionPane.showConfirmDialog(
                        new JOptionPane(),
                        "Soll die Änderung gespeichert werden?",
                        columnNames[column],
                        JOptionPane.YES_NO_OPTION
                );

                if (confirmMessage == JOptionPane.YES_OPTION) {
                    if (saveChanges(aValue, column)) {
                        super.setValueAt(aValue, row, column);
                    }
                }
            }
        };
        booksTable = new JTable(booksTableModel);
        booksTable.setFont(inputFont);

        // Fügt Bücher aus der Datenbank der Tabelle hinzu
        for (String keyMap : booksMap.keySet()) {
            Object[] values = booksMap.get(keyMap).toArray();
            booksTableModel.addRow(values);
        }

        // Fügt einen MouseListener hinzu, um das aktuelle Buch bearbeiten oder löschen zu können
        booksTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    currentBook = new HashMap<>();
                    JTable target = (JTable) e.getSource();
                    int row = target.getSelectedRow();
                    List<String> bookKey = new ArrayList<>(booksMap.keySet());
                    String bookID = bookKey.get(row);
                    List<List<Object>> listsValues = new ArrayList<>(booksMap.values());
                    List<Object> booksValues = listsValues.get(row);
                    currentBook.put(bookID, booksValues);
                }
            }
        });
    }

    /**
     * Validiert und speichert die Änderung an einem Buch.
     *
     * @param aValue Der neue Wert der gespeichert wird.
     * @param column Die Spalte die geändert wird.
     * @return <ul>
     *     <li>true, wenn die Änderungen erfolgreich gespeichert wurden</li>
     *     <li>false, wenn die Änderungen nicht erfolgreich gespeichert wurden</li>
     * </ul>
     * */
    private boolean saveChanges(Object aValue, int column) {
        AddButton addButton = new AddButton(mainPanel, dataHandler, booksTableModel);
        List<String> keyList = new ArrayList<>(currentBook.keySet());
        List<Object> bookValues = new ArrayList<>(currentBook.get(keyList.getFirst()));
        int id = Integer.parseInt(keyList.getFirst());
        int isbnCorrect = 0;
        String title = (String) bookValues.getFirst();
        String author = (String) bookValues.get(1);
        String isbn = (String) bookValues.getLast();

        if (column == 0) {
            title = (String) aValue;
        } else if (column == 1) {
            author = (String) aValue;
        } else {
            isbn = (String) aValue;
            isbnCorrect = new ISBNValidator().isValid(isbn);
        }

        if (title.isEmpty()) {
            addButton.showMessageDialog("Der Titel darf nicht leer sein");
            return false;
        } else if (author.isEmpty()) {
            addButton.showMessageDialog("Der Autor darf nicht leer sein");
            return false;
        } else if (isbn.isEmpty()) {
            addButton.showMessageDialog("Der ISBN darf nicht leer sein");
            return false;
        } else if (isbnCorrect == 1) {
            addButton.showMessageDialog("Der ISBN muss aus Zahlen bestehen");
            return false;
        } else if (isbnCorrect == 2) {
            addButton.showMessageDialog("Der ISBN hat nicht die richtige Länge." +
                    "\nEntweder sind es 10 oder 13 Zeichen");
            return false;
        } else if (isbnCorrect == 3) {
            addButton.showMessageDialog("Die letzte Zahl stimmt nicht überein");
            return false;
        } else {
            dataHandler.setBookMap(title, author, isbn, id, true);
            return true;
        }
    }

    /**
     * Erstellt die Widgets und fügt sie dem Panel hinzu.
     */
    private void createWidgets() {
        JScrollPane scrollPane = new JScrollPane(booksTable);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new AddButton(mainPanel, dataHandler, booksTableModel);
        JButton deleteButton = new JButton("Löschen");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

}