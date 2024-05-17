package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Member extends JPanel {
    private final JTable memberTable;

    public Member() {
        setLayout(new BorderLayout());
        String[] columnNames = {"Name", "E-Mail", "Adresse"};

        DefaultTableModel memberTableModel = new DefaultTableModel(columnNames, 0);
        memberTable = new JTable(memberTableModel);

        createWidgets();
    }

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
