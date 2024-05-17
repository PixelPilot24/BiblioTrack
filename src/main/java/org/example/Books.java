package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Books extends JPanel {
    private final JTable booksTable;

    public Books() {
        setLayout(new BorderLayout());
        String[] columnNames = {"Titel", "Autor", "ISBN"};
        DefaultTableModel booksTableModel = new DefaultTableModel(columnNames, 0);
        booksTable = new JTable(booksTableModel);

        createWidgets();
    }

    private void createWidgets() {
        JScrollPane scrollPane = new JScrollPane(booksTable);

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
