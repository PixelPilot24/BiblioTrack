package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Diese Klasse stellt das {@code JPanel} für die Verwaltung von geliehenen Büchern dar.
 * */
public class Lending extends JPanel {
    private final JTable lendingTable;
    private final DataHandler dataHandler;

    /**
     * In dem Konstruktor wird eine Tabelle mit "Buch", "Mitglied", "Ausleihdatum"
     * und "Rückgabedatum" erstellt.
     * */
    public Lending(DataHandler dataHandler) {
        setLayout(new BorderLayout());
        String[] columnNames = {"Buch", "Mitglied", "Ausleihdatum", "Rückgabedatum"};
        DefaultTableModel lendingTableModel = new DefaultTableModel(columnNames, 0);
        lendingTable = new JTable(lendingTableModel);
        this.dataHandler = dataHandler;

        createWidgets();
    }

    /**
     * Erstellt zwei Buttons und fügt diese zum Panel hinzu.
     * */
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
