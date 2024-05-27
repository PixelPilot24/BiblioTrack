package org.example.member;

import org.example.data.DataHandler;
import org.example.helper.HelperClass;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diese Klasse stellt das {@code JPanel} für die Verwaltung von Mitgliedern dar.
 * */
public class Member extends JPanel {
    private final DataHandler dataHandler;
    private final JPanel mainPanel;
    
    private JTable memberTable;
    private DefaultTableModel memberTableModel;
    private Map<String, java.util.List<Object>> currentMember;
    private Map<String, List<Object>> memberMap;

    /**
     * In dem Konstruktor wird eine Tabelle mit "Name", "E-Mail", "Telefon" erstellt.
     * */
    public Member(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
        this.memberMap = dataHandler.getMemberMap();
        this.mainPanel = new JPanel(new BorderLayout(10,10));
        setLayout(new BorderLayout());
        
        createMemberTable();
        createWidgets();
    }

    /**
     * Erstellt und bearbeitet die Tabelle der Mitglieder.
     * */
    private void createMemberTable() {
        String[] columnNames = {"Name", "E-Mail", "Telefon"};
        Font inputFont = new Font("Arial", Font.PLAIN,15);
        
        memberTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
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
        
        memberTable = new JTable(memberTableModel);
        memberTable.setFont(inputFont);
        
        // Fügt die Mitglieder aus der Datenbank der Tabelle hinzu
        for (String keyMap : memberMap.keySet()) {
            Object[] values = memberMap.get(keyMap).toArray();
            memberTableModel.addRow(values);
        }
        
        // Fügt einen MouseListener hinzu, um das aktuelle Mitglied bearbeiten oder löschen zu können
        memberTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    memberMap = dataHandler.getMemberMap();
                    currentMember = new HashMap<>();
                    JTable target = (JTable) e.getSource();
                    int row = target.getSelectedRow();
                    List<String> memberKey = new ArrayList<>(memberMap.keySet());
                    String memberID = memberKey.get(row);
                    List<List<Object>> listsValues = new ArrayList<>(memberMap.values());
                    List<Object> memberValues = listsValues.get(row);
                    currentMember.put(memberID, memberValues);

                    deleteButton(memberID, memberValues, row);
                }
            }
        });
    }

    /**
     * Initialisierung des Löschen-Buttons.
     *
     * @param memberID Die ID vom Mitglied.
     * @param values Eine {@code List<Object>} mit dem Namen, E-Mail und der Telefonnummer.
     * @param row Die aktuelle Reihe des ausgewählten Mitgliedes.
     * */
    private void deleteButton(String memberID, List<Object> values, int row) {
        JPanel buttonPanel = (JPanel) getComponent(1);

        if (buttonPanel.getComponentCount() == 2) {
            buttonPanel.remove(1);
        }

        JButton deleteButton = addDeleteButton(memberID, values, row);
        buttonPanel.add(deleteButton);

        repaint();
        revalidate();
    }

    /**
     * Erstellung und gestaltung des Löschen-Buttons.
     *
     * @param memberID Die ID vom Mitglied.
     * @param values Eine {@code List<Object>} mit dem Namen, E-Mail und der Telefonnummer.
     * @param row Die aktuelle Reihe des ausgewählten Mitgliedes.
     *
     * @return Gibt den Löschen-Button aus.
     * */
    private JButton addDeleteButton(String memberID, List<Object> values, int row) {
        JButton deleteButton = new JButton("Löschen");
        deleteButton.addActionListener(_ -> {
            String name = (String) values.getFirst();
            String email = (String) values.get(1);
            String phone = (String) values.get(2);
            String message = String.format("""
                            Soll das Mitglied wirklich gelöscht werden?

                            Name: %s
                            E-Mail: %s
                            Telefonnummer: %s""", name, email, phone);
            int confirmMessage = JOptionPane.showConfirmDialog(
                    new JOptionPane(),
                    message,
                    "Löschen",
                    JOptionPane.YES_NO_OPTION
            );

            // Wenn der Dialog akzeptiert wird, dann wird das Mitglied gelöscht
            if (confirmMessage == JOptionPane.YES_OPTION) {
                // Bestimmt die ID, löscht das Mitglied aus der Datenbank und aktualisiert die Map
                int id = Integer.parseInt(memberID);
                dataHandler.setMemberMap(name, email, phone, id, 2);
                memberMap = dataHandler.getMemberMap();

                // Löscht das Mitglied aus der Tabelle und aktualisiert diese
                JPanel buttonPanel = (JPanel) getComponent(1);
                buttonPanel.remove(1);
                memberTableModel.removeRow(row);
                repaint();
                revalidate();
            }
        });

        return deleteButton;
    }

    /**
     * In dieser Methode wird das Mitglied gespeichert, falls die Anforderungen erfüllt wurden.
     *
     * @param aValue Der veränderte Wert aus der Tabelle.
     * @param column Die Spalte in der geändert wurde.
     * @return <ul>
     *     <li>true: Änderung wurde gespeichert</li>
     *     <li>false: Änderung nicht gespeichert</li>
     * </ul>
     * */
    private boolean saveChanges(Object aValue, int column) {
        HelperClass helper = new HelperClass();

        AddMemberButton addMemberButton = new AddMemberButton(mainPanel, dataHandler, memberTableModel);
        // Bestimmt die ID und die Liste mit dem Titel, der E-Mail und der Telefonnummer
        List<String> keyList = new ArrayList<>(currentMember.keySet());
        String currentKey = keyList.getFirst();
        List<Object> memberValues = new ArrayList<>(currentMember.get(currentKey));

        int id = Integer.parseInt(currentKey);
        String name = (String) memberValues.getFirst();
        String email = (String) memberValues.get(1);
        String phone = (String) memberValues.get(2);

        // Überprüft welche Spalte ausgewählt ist, und diese Variable wird dann verändert
        if (column == 0) {
            name = (String) aValue;
        } else if (column == 1) {
            email = (String) aValue;
        } else {
            phone = (String) aValue;
        }
        
        if (name.isEmpty()) {
            helper.showMessageDialog("Der Name darf nicht leer sein");
        } else if (email.isEmpty()) {
            helper.showMessageDialog("Die E-Mail darf nicht leer sein");
        } else if (phone.isEmpty()) {
            helper.showMessageDialog("Die Telefonnummer darf nicht leer sein");
        } else if (addMemberButton.checkPhone(phone)) {
            helper.showMessageDialog("Die Telefonnummer darf nur Zahlen beinhalten");
        } else {
            dataHandler.setMemberMap(name, email, phone, id, 1);
            return true;
        }

        return false;
    }

    /**
     * Erstellt den Button und fügt diese zum Panel hinzu.
     * */
    private void createWidgets() {
        JScrollPane scrollPane = new JScrollPane(memberTable);
        JPanel buttonPanel = new JPanel();
        JButton addButton = new AddMemberButton(mainPanel, dataHandler, memberTableModel);

        buttonPanel.add(addButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
