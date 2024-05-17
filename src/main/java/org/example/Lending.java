package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Lending extends JPanel {
    private final JTable lendingTable;

    public Lending() {
        setLayout(new BorderLayout());
        String[] columnNames = {"Buch", "Mitglied", "Ausleihdatum", "Rückgabedatum"};
        DefaultTableModel lendingTableModel = new DefaultTableModel(columnNames, 0);
        lendingTable = new JTable(lendingTableModel);

        createWidgets();
    }

    private void createWidgets() {
        JScrollPane scrollPane = new JScrollPane(lendingTable);

        JPanel buttonPanel = new JPanel();
        JButton lendingButton = new JButton("Ausleihen");
        JButton returnButton = new JButton("Zurückgeben");

        buttonPanel.add(lendingButton);
        buttonPanel.add(returnButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
