package org.example.member;

import org.example.books.AddButton;
import org.example.data.DataHandler;
import org.example.helper.HelperClass;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Diese Klasse erstellt und gibt den Button zum Hinzufügen von Mitgliedern aus.
 * */
public class AddMemberButton extends JButton {
    private final String[] columnNames = {"Name", "E-Mail", "Telefon"};
    private final Font inputFont = new Font("Arial", Font.PLAIN,18);
    private final JPanel mainPanel;
    private final DataHandler dataHandler;
    private final DefaultTableModel defaultTableModel;

    /**
     * In diesem Konstruktor werden die Daten initialisiert und dem Button eine Funktion zugewiesen.
     *
     * @param mainPanel Das Panel vom Hauptfenster.
     * @param dataHandler Der Handler für Datenoperationen.
     * @param tableModel Die Tabelle mit den Mitgliedern.
     * */
    public AddMemberButton(JPanel mainPanel, DataHandler dataHandler, DefaultTableModel tableModel) {
        this.mainPanel = mainPanel;
        this.dataHandler = dataHandler;
        this.defaultTableModel = tableModel;
        AddButton addButton = new AddButton(mainPanel, dataHandler, tableModel);
        setText("Hinzufügen");
        addActionListener(_ -> addButton.createDialog(
                saveButton(), "Neues Mitglied Hinzufügen", columnNames, mainPanel, false
        ));
    }

    /**
     * Der Button zum Speichern von einem neuen Mitglied.
     *
     * @return Gibt den Button aus.
     * */
    private JButton saveButton() {
        // Erstellt und gestaltet den Button
        JButton saveButton = new JButton("Speichern");
        saveButton.setFont(inputFont);
        saveButton.addActionListener(_ -> {
            HelperClass helper = new HelperClass();

            JPanel titlePanel = (JPanel) mainPanel.getComponent(0);
            String[] values = helper.getValues(titlePanel);
            // Bestimmt die einzelnen Variablen
            String name = values[0];
            String email = values[1];
            String phone = values[2];

            if (name.isEmpty()) {
                helper.showMessageDialog("Der Name darf nicht leer sein");
            } else if (email.isEmpty()) {
                helper.showMessageDialog("Die E-Mail darf nicht leer sein");
            } else if (phone.isEmpty()) {
                helper.showMessageDialog("Die Telefonnummer darf nicht leer sein");
            } else if (checkPhone(phone)) {
                helper.showMessageDialog("Die Telefonnummer darf nur Zahlen beinhalten");
            } else {
                // Speichert die Mitglieder in der Datenbank
                dataHandler.setMemberMap(name, email, phone, 0, 0);
                JOptionPane.showMessageDialog(
                        new JOptionPane(),
                        "Neues Mitglied erfolgreich gespeichert",
                        "Speichern",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Fügt der Tabelle eine weitere Reihe mit dem neuen Mitglied hinzu
                helper.addRowInTable(titlePanel, values, defaultTableModel);
            }
        });

        return saveButton;
    }

    /**
     * Überprüft, ob die eingegebene Nummer nur aus Zahlen besteht.
     *
     * @param phone Die zu überprüfende Nummer.
     * @return <ul>
     *     <li>false: Der eingegebene Wert besteht aus Zahlen</li>
     *     <li>true: Der eingegebene Wert besteht nicht aus Zahlen</li>
     * </ul>
     * */
    protected boolean checkPhone(String phone) {
        Pattern pattern = Pattern.compile("^[0-9]+$");
        Matcher matcher = pattern.matcher(phone);

        return !matcher.matches();
    }
}
