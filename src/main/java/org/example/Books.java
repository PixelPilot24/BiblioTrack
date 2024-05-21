package org.example;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Diese Klasse stellt das {@code JPanel} für die Verwaltung von Büchern dar.
 * */
public class Books extends JPanel {
    private final JTable booksTable;
    private final DataHandler dataHandler;

    private final String[] columnNames = {"Titel", "Autor", "ISBN"};
    private final Font inputFont = new Font("Arial", Font.PLAIN,18);

    private JPanel mainPanel;

    /**
     * Konstruktor für das Books-Panel.
     *
     * @param dataHandler Handler für Datenoperationen.
     */
    public Books(DataHandler dataHandler) {
        setLayout(new BorderLayout());
        DefaultTableModel booksTableModel = new DefaultTableModel(columnNames, 0);
        booksTable = new JTable(booksTableModel);
        this.dataHandler = dataHandler;

        createWidgets();
    }

    /**
     * Erstellt die Widgets und fügt sie dem Panel hinzu.
     */
    private void createWidgets() {
        JScrollPane scrollPane = new JScrollPane(booksTable);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Hinzufügen");
        addButton.addActionListener(_ -> addButtonListener());
        JButton editButton = new JButton("Bearbeiten");
        JButton deleteButton = new JButton("Löschen");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Listener für den Hinzufügen-Button.
     * Öffnet einen Dialog zur Eingabe eines neun Buches.
     * */
    private void addButtonListener() {
        // Erstellung und gestaltung vom Dialogfenster
        JDialog dialog = new JDialog();
        dialog.setLayout(new GridLayout(0,1));
        dialog.setTitle("Neues Buch hinzufügen");
        dialog.setSize(400,400);
        dialog.setLocationRelativeTo(null);

        // Panel für die Widgets
        JPanel panel = new JPanel(new FlowLayout());
        mainPanel = new JPanel(new BorderLayout(10,10));
        JPanel titlePanel = new JPanel(new GridLayout(0, 1, 10, 10));

        Font borderFont = new Font("Arial", Font.PLAIN,20);

        // Erstellt für die drei Kategorien ein Textfeld welches umrandet ist
        // und fügt dieses dem passenden Panel hinzu
        for (String name : columnNames) {
            TitledBorder titledBorder = new TitledBorder(name);
            titledBorder.setTitleFont(borderFont);

            JPanel textPanel = new JPanel();
            textPanel.setBorder(titledBorder);
            textPanel.setPreferredSize(new Dimension(250,70));

            JTextField textField = new JTextField();
            textField.setFont(inputFont);
            textField.setPreferredSize(new Dimension(230, 30));

            textPanel.add(textField);
            titlePanel.add(textPanel);
        }

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        // Der KeyListener muss außerhalb der Schleife hinzugefügt werden, weil in der Schleife
        // wurde dem mainPanel noch nichts hinzugefügt. Also ist dieser dann leer und gibt einen
        // Fehler aus
        JPanel textPanel = (JPanel) titlePanel.getComponent(2);
        JTextField textField = (JTextField) textPanel.getComponent(0);
        textField.addKeyListener(getKeyAdapter());

        mainPanel.add(saveButton(), BorderLayout.SOUTH);
        panel.add(mainPanel);
        dialog.add(panel);

        dialog.setVisible(true);
    }

    /**
     * Erstellt und gibt einen KeyListener zurück, der sicherstellt, dass nur Zahlen,
     * die Löschtaste und 'X' im ISBN-Eingabefeld akzeptiert werden.
     *
     * @return {@code KeyListener} für das ISBN-Eingabefeld
     */
    private KeyListener getKeyAdapter() {
        // Pattern um sicherzustellen das nur Zahlen, Punkt und wenn gelöscht wird,
        // akzeptiert wird
        Pattern pattern = Pattern.compile("^[0-9\bX]+$");
        JPanel titlePanel = (JPanel) mainPanel.getComponent(0);
        JPanel textPanel = (JPanel) titlePanel.getComponent(2);
        JTextField textField = (JTextField) textPanel.getComponent(0);

        return new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                // Bestimmt das letzte eingegebene Zeichen und gleicht das mit dem Pattern ab
                char c = keyEvent.getKeyChar();
                Matcher matcher = pattern.matcher(String.valueOf(c));

                // Wenn die Eingabe nicht im Pattern ist oder es 13 Zeichen sind,
                // dann wird nichts eingegeben
                if (!matcher.matches() || textField.getText().length() == 13) {
                    keyEvent.consume();
                }
            }
        };
    }

    /**
     * Erstellt und gibt den Speichern-Button zurück, die die Eingaben validiert und speichert.
     *
     * @return Gibt den Speichern-Button zurück.
     */
    private JButton saveButton() {
        // Erstellt und gestaltet den Speichern-Button
        JButton saveButton = new JButton("Speichern");
        saveButton.setFont(inputFont);
        saveButton.addActionListener(_ -> {
            String titel;
            String author;
            String isbn;
            String[] values = new String[3]; // Liste für die Eingaben
            JPanel titlePanel = (JPanel) mainPanel.getComponent(0);

            // Extrahiert die eingegebenen Texte aus den Feldern und fügt diese in die Liste hinzu
            for (int i = 0; i < 3; i++) {
                JPanel textPanel = (JPanel) titlePanel.getComponent(i);
                JTextField textField = (JTextField) textPanel.getComponent(0);
                String value = textField.getText();

                values[i] = value;
            }

            titel = values[0];
            author = values[1];
            isbn = values[2];

            // Überprüft, ob die ISBN richtig ist
            int isbnCorrect = new ISBNValidator().isValid(isbn);

            // Überprüft, ob alles stimmt und wenn ja, dann wird der neue Eintrag in der Datenbank
            // gespeichert und die Felder geleert
            if (titel.isEmpty()) {
                showMessageDialog("Der Titel darf nicht leer sein");
            } else if (author.isEmpty()) {
                showMessageDialog("Der Autor darf nicht leer sein");
            } else if (isbn.isEmpty()) {
                showMessageDialog("Der ISBN darf nicht leer sein");
            } else if (isbnCorrect == 1) {
                showMessageDialog("Der ISBN muss aus Zahlen bestehen");
            } else if (isbnCorrect == 2) {
                showMessageDialog("Der ISBN hat nicht die richtige Länge." +
                        "\nEntweder sind es 10 oder 13 Zeichen");
            } else if (isbnCorrect == 3) {
                showMessageDialog("Die letzte Zahl stimmt nicht überein");
            } else {
                dataHandler.setBookMap(titel, author, isbn);
                JOptionPane.showMessageDialog(
                        new JOptionPane(),
                        "Neues Buch erfolgreich gespeichert",
                        "Speichern",
                        JOptionPane.INFORMATION_MESSAGE
                );

                for (int i = 0; i < 3; i++) {
                    JPanel textPanel = (JPanel) titlePanel.getComponent(i);
                    JTextField textField = (JTextField) textPanel.getComponent(0);

                    textField.setText("");
                }
            }
        });

        return saveButton;
    }

    /**
     * Zeigt einen Fehlerdialog mit der angegebenen Nachricht an.
     *
     * @param message Die anzuzeigende Fehlermeldung
     */
    private void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(
                new JOptionPane(), message, "Fehler", JOptionPane.ERROR_MESSAGE
        );
    }
}
