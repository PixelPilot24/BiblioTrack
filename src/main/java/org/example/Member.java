package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Diese Klasse stellt das {@code JPanel} für die Verwaltung von Mitgliedern dar.
 * */
public class Member extends JPanel {
    private final JTable memberTable;
    private final DataHandler dataHandler;

    /**
     * In dem Konstruktor wird eine Tabelle mit "Name", "E-Mail", "Telefon" erstellt.
     * */
    public Member(DataHandler dataHandler) {
        setLayout(new BorderLayout());
        String[] columnNames = {"Name", "E-Mail", "Telefon"};

        DefaultTableModel memberTableModel = new DefaultTableModel(columnNames, 0);
        memberTable = new JTable(memberTableModel);
        this.dataHandler = dataHandler;

        createWidgets();
    }

    /**
     * Erstellt drei Buttons und fügt diese zum Panel hinzu.
     * */
    private void createWidgets() {
        JScrollPane scrollPane = new JScrollPane(memberTable);
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Hinzufügen");
        JButton editButton = new JButton("Bearbeiten");
        JButton deleteButton = new JButton("Löschen");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
