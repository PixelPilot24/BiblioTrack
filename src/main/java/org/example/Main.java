package org.example;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame{
    public Main() {
        setTitle("BiblioTrack");
        setSize(800,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createWidgets();
        createMenuBar();

        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Datei");
        JMenuItem exitItem = new JMenuItem("Beenden");
        exitItem.addActionListener(_ -> System.exit(0));

        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private void createWidgets() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Bücher", new Books());
        tabbedPane.addTab("Mitglieder", new Member());
        tabbedPane.addTab("Ausleihen", new Lending());

        add(tabbedPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}