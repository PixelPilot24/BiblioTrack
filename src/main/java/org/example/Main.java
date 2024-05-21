package org.example;

import javax.swing.*;
import java.awt.*;

/**
 * Diese Klasse richtet den Haupt-Frame ein, initialisiert den DataHandler
 * und erstellt die GUI-Komponenten.
 * */
public class Main extends JFrame{
    private final DataHandler dataHandler;

    /**
     * Initialisiert den DataHandler, richtet die Eigenschaften des Haupt-Frames ein
     * und erstellt die Widgets und die Menüleiste.
     * */
    public Main() {
        dataHandler = new DataHandler();
        dataHandler.connection(); // Stellt eine verbindung zur Datenbank her
        setTitle("BiblioTrack");
        setSize(800,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createWidgets();
        createMenuBar();

        setVisible(true);
    }

    /**
     * Erstellt die Menüleiste
     * */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Datei");
        JMenuItem exitItem = new JMenuItem("Beenden");
        exitItem.addActionListener(_ -> System.exit(0));

        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    /**
     * Erstellt im Hauptfenster Registerkarten mit drei Tabs.
     * */
    private void createWidgets() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Bücher", new Books(dataHandler));
        tabbedPane.addTab("Mitglieder", new Member(dataHandler));
        tabbedPane.addTab("Ausleihen", new Lending(dataHandler));

        add(tabbedPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}