package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Die DataHandler-Klasse verwaltet die Datenbankverbindung und Operationen für die Bibliothek.
 * Sie ermöglicht das Laden, Speichern und Verwalten von Büchern, Mitgliedern und ausgeliehenen Büchern.
 */
public class DataHandler {
    private final String bibUrl = "jdbc:postgresql://localhost:5432/bibliotrack";
    private final String user = "postgres"; // TODO Namen anpassen
    private final String password = ""; // TODO Passwort anpassen

    private Map<String, List<Object>> bookMap;; // Map zur Speicherung von Büchern
    private Map<String, List<Object>> memberMap;; // Map zur Speicherung von Mitgliedern
    private Map<String, List<Object>> lendingMap;; // Map zur Speicherung von ausgeliehenen Büchern

    /**
     * Gibt die Buch-Map zurück, falls die Map null ist, wird diese initialisiert.
     *
     * @return Gibt die Buch-Map zurück
     * */
    public Map<String, List<Object>> getBookMap() {
        if (bookMap == null) {
            connection();
        }

        return bookMap;
    }

    /**
     * Fügt ein Buch in die Datenbank und die Buch-Map hinzu.
     *
     * @param title  Der Titel des Buches.
     * @param author Der Autor des Buches.
     * @param isbn   Die ISBN des Buches.
     */
    public void setBookMap(String title, String author, String isbn) {
        try (Connection connection = DriverManager.getConnection(bibUrl, user, password)) {
            Statement statement = connection.createStatement();
            List<Object> mapList = new ArrayList<>();
            int index = 0; // Index für das neu erstellte Buch

            // SQL Befehl zum Einfügen von einem neuen Buch
            String insertCommand = "INSERT INTO book(title, author, isbn) " +
                    "VALUES('" + title + "', '" + author + "', '" + isbn + "');";
            statement.executeUpdate(insertCommand);

            // Abfrage zum Bestimmen der ID des neuen Buches
            ResultSet resultSet = statement.executeQuery("SELECT * FROM book");

            while (resultSet.next()) {
                index = resultSet.getInt("id");
            }

            // Fügt den Titel, Author und ISBN in eine Liste und diese dann in die Buch-Map
            mapList.add(title);
            mapList.add(author);
            mapList.add(isbn);

            this.bookMap.put(String.valueOf(index), mapList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gibt die Mitglieder-Map zurück, falls die Map null ist, wird diese initialisiert.
     *
     * @return Gibt die Mitglieder-Map zurück
     * */
    public Map<String, List<Object>> getMemberMap() {
        if (memberMap == null) {
            connection();
        }

        return memberMap;
    }

    /**
     * Gibt die Map für ausgeliehene Bücher zurück, falls die Map null ist, wird diese initialisiert.
     *
     * @return Ausgeliehene Bücher.
     * */
    public Map<String, List<Object>> getLendingMap() {
        if (lendingMap == null) {
            connection();
        }

        return lendingMap;
    }

     /**
     * Stellt die Verbindung zur Datenbank her und lädt die Daten.
     * Wenn die Datenbank nicht existiert, wird sie erstellt.
     */
    public void connection() {
        String url = "jdbc:postgresql://localhost:5432/";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT datname FROM pg_database;");

            boolean databaseExists = false;

            // Überprüft, ob die Datenbank existiert
            while (resultSet.next()) {
                String databaseName = resultSet.getString("datname");
                if (databaseName.equals("bibliotrack")) {
                    databaseExists = true;
                    break;
                }
            }

            // Wenn die Datenbank existiert, dann werden die Daten geladen. Wenn nicht,
            // dann wird eine neue Datenbank erstellt
            if (databaseExists) {
                try (Connection bibConnection = DriverManager.getConnection(bibUrl, user, password)) {
                    loadData(bibConnection);
                }
            } else {
                statement.executeUpdate("CREATE DATABASE bibliotrack");

                try (Connection bibConnection = DriverManager.getConnection(bibUrl, user, password)) {
                    createDatabase(bibConnection);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lädt die Daten aus der Datenbank und speichert sie in den entsprechenden Maps.
     *
     * @param connection Die Verbindung zur Bibliotrack-Datenbank.
     * @throws SQLException Falls ein Fehler beim Laden der Daten auftritt.
     */
    private void loadData(Connection connection) throws SQLException {
        // Initialisierung der Maps
        bookMap = new HashMap<>();
        memberMap = new HashMap<>();
        lendingMap = new HashMap<>();

        Statement statement = connection.createStatement();
        // Ruft alle Datensätze der book Tabelle auf
        ResultSet resultBook = statement.executeQuery("SELECT * FROM book");

        // Extrahiert die Daten und fügt diese in Map hinzu
        while (resultBook.next()) {
            int id = resultBook.getInt("id");
            String title = resultBook.getString("title");
            String author = resultBook.getString("author");
            String isbn = resultBook.getString("isbn");
            List<Object> mapList = new ArrayList<>();

            mapList.add(title);
            mapList.add(author);
            mapList.add(isbn);

            bookMap.put(String.valueOf(id), mapList);
        }

        resultBook.close();
        // Ruft alle Datensätze der member Tabelle auf
        ResultSet resultMember = statement.executeQuery("SELECT * FROM member");

        // Extrahiert die Daten und fügt diese in Map hinzu
        while (resultMember.next()) {
            int id = resultMember.getInt("id");
            String name = resultMember.getString("name");
            String email = resultMember.getString("email");
            int phone = resultMember.getInt("phone");
            List<Object> mapList = new ArrayList<>();

            mapList.add(name);
            mapList.add(email);
            mapList.add(phone);

            memberMap.put(String.valueOf(id), mapList);
        }

        resultMember.close();
        // Ruft alle Datensätze der lending Tabelle auf
        ResultSet resultLending = statement.executeQuery("SELECT * FROM lending");

        // Extrahiert die Daten und fügt diese in Map hinzu
        while (resultLending.next()) {
            int id = resultLending.getInt("id");
            int bookId = resultLending.getInt("book_id");
            int member_id = resultLending.getInt("member_id");
            Date lendDate = resultLending.getDate("lend_date");
            Date returnDate = resultLending.getDate("return_date");
            List<Object> mapList = new ArrayList<>();

            mapList.add(bookId);
            mapList.add(member_id);
            mapList.add(lendDate);
            mapList.add(returnDate);

            lendingMap.put(String.valueOf(id), mapList);
        }

        resultLending.close();
        statement.close();
    }

    /**
     * Erstellt in der neu erstellten Datenbank die Tabellen.
     *
     * @param connection Die Verbindung zur Bibliotrack-Datenbank.
     * @throws SQLException Falls ein Fehler beim Laden der Daten auftritt.
     * */
    private void createDatabase(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        // Befehl für die Erstellung der Tabelle mit den Spalten
        String commandBooks = "CREATE TABLE book(" +
                "id SERIAL PRIMARY KEY," +
                "title VARCHAR(255) NOT NULL," +
                "author VARCHAR(255) NOT NULL," +
                "isbn VARCHAR(13) NOT NULL" +
                ");";
        String commandMember = "CREATE TABLE member(" +
                "id SERIAL PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +
                "email VARCHAR(50) NOT NULL," +
                "phone INT NOT NULL" +
                ");";
        String commandLending = "CREATE TABLE lending(" +
                "id SERIAL PRIMARY KEY," +
                "book_id INT, " +
                "CONSTRAINT fk_lending_book " +
                "   foreign KEY (book_id) " +
                "   REFERENCES book(id)," +
                "member_id INT, " +
                "CONSTRAINT fk_lending_member " +
                "   foreign KEY(member_id) " +
                "   REFERENCES member(id), " +
                "lend_date DATE NOT NULL DEFAULT CURRENT_DATE, " +
                "return_date DATE NOT NULL" +
                ");";

        // Führt die Befehle aus
        statement.executeUpdate(commandBooks);
        statement.executeUpdate(commandMember);
        statement.executeUpdate(commandLending);
    }
}
