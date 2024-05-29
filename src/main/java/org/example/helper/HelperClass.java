package org.example.helper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Diese Klasse enthält eine Sammlung von Methoden.
 * */
public class HelperClass {
    /**
     * Bestimmt den Inhalt der Textfelder und gibt eine Liste mit dem Inhalt aus.
     *
     * @param titlePanel Das Panel in dem sich die Textfelder befinden.
     * @return Gibt die Liste mit dem Inhalt der Textfelder aus.
     * */
    public String [] getValues(JPanel titlePanel) {
        String[] values = new String[3];

        // Bestimmt den Inhalt der Eingaben und fügt diese in eine Liste hinzu
        for (int i = 0; i < 3; i++) {
            JPanel textPanel = (JPanel) titlePanel.getComponent(i);
            JTextField textField = (JTextField) textPanel.getComponent(0);

            values[i] = textField.getText();
        }

        return values;
    }

    /**
     * Fügt in der Tabelle den Inhalt ein und löscht die eingegebenen Texte in den Textfeldern.
     *
     * @param titlePanel Das Panel in dem sich die Textfelder befinden.
     * @param values Eine {@code String[]} mit den Werten die gespeichert wurden.
     * @param tableModel Die Tabelle in die der Inhalt eingefügt wird.
     * */
    public void addRowInTable(JPanel titlePanel, String[] values, DefaultTableModel tableModel) {
        // Leert die Textfelder
        for (int i = 0; i < 3; i++) {
            JPanel textPanel = (JPanel) titlePanel.getComponent(i);
            JTextField textField = (JTextField) textPanel.getComponent(0);

            textField.setText("");
        }

        tableModel.addRow(values);
    }

    /**
     * Zeigt einen Fehlerdialog mit der angegebenen Nachricht an.
     *
     * @param message Die anzuzeigende Fehlermeldung
     */
    public void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
                new JOptionPane(), message, "Fehler", JOptionPane.ERROR_MESSAGE
        );
    }
}
