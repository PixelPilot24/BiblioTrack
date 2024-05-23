package org.example.books;

import org.example.data.DataHandler;
import org.example.ISBNValidator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddButton extends JButton {
    private final String[] columnNames = {"Titel", "Autor", "ISBN"};
    private final Font inputFont = new Font("Arial", Font.PLAIN,18);
    private final JPanel mainPanel;
    private final DataHandler dataHandler;
    private final DefaultTableModel defaultTableModel;

    /**
     * Konstruktor zum Initialisieren der Daten und gestaltung des Buttons.
     *
     * @param mainPanel {@code JPanel} vom Haupt-Panel.
     * @param dataHandler Handler für Datenoperationen.
     * @param tableModel Zum Hinzufügen eines neuen Buches.
     * */
    public AddButton(JPanel mainPanel, DataHandler dataHandler, DefaultTableModel tableModel) {
        this.mainPanel = mainPanel;
        this.dataHandler = dataHandler;
        this.defaultTableModel = tableModel;
        setText("Hinzufügen");
        addActionListener(_ -> createDialog(
                saveButton(), "Neues Buch hinzufügen", columnNames, mainPanel, true
        ));
    }

    /**
     * Listener für den Hinzufügen-Button.
     * Öffnet einen Dialog zur Eingabe eines neun Buches.
     * */
    public void createDialog(JButton button, String title, String[] columnNames, JPanel mainPanel,
                             boolean book) {
        // Erstellung und gestaltung vom Dialogfenster
        JDialog dialog = new JDialog();
        dialog.setLayout(new GridLayout(0,1));
        dialog.setTitle(title);
        dialog.setSize(400,400);
        dialog.setLocationRelativeTo(null);

        // Panel für die Widgets
        JPanel panel = new JPanel(new FlowLayout());
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
        textField.addKeyListener(getKeyAdapter(book));

        mainPanel.add(button, BorderLayout.SOUTH);
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
    private KeyListener getKeyAdapter(boolean book) {
        Pattern pattern;

        if (!book) {
            // Pattern für die Eingabe der Telefonnummer (Nur Zahlen)
            pattern = Pattern.compile("^[0-9]+$");
        } else {
            // Pattern für die Eingabe der ISBN (Zahlen, Löschen, X)
            pattern = Pattern.compile("^[0-9\bX]+$");
        }

        JPanel titlePanel = (JPanel) mainPanel.getComponent(0);
        JPanel textPanel = (JPanel) titlePanel.getComponent(2);
        JTextField textField = (JTextField) textPanel.getComponent(0);

        return new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                // Bestimmt das letzte eingegebene Zeichen und gleicht das mit dem Pattern ab
                char c = keyEvent.getKeyChar();
                Matcher matcher = pattern.matcher(String.valueOf(c));

                // Überprüft welche Länge benötigt wird. Für ISBN 13, für Telefonnummer 15
                if (book) {
                    if (!matcher.matches() || textField.getText().length() == 13) {
                        keyEvent.consume();
                    }
                } else {
                    if (!matcher.matches() || textField.getText().length() == 15) {
                        keyEvent.consume();
                    }
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
                dataHandler.setBookMap(titel, author, isbn, 0, 0);
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

                defaultTableModel.addRow(new Object[]{titel, author, isbn});
            }
        });

        return saveButton;
    }

    /**
     * Zeigt einen Fehlerdialog mit der angegebenen Nachricht an.
     *
     * @param message Die anzuzeigende Fehlermeldung
     */
    public void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(
                new JOptionPane(), message, "Fehler", JOptionPane.ERROR_MESSAGE
        );
    }
}
