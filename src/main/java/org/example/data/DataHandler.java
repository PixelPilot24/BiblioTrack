package org.example.data;

import org.example.helper.HelperClass;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Die DataHandler-Klasse verwaltet die Datenbankverbindung und Operationen für die Bibliothek.
 * Sie ermöglicht das Laden, Speichern und Verwalten von Büchern, Mitgliedern und ausgeliehenen Büchern.
 */
public class DataHandler extends HelperDataHandler {
    private Map<String, List<Object>> bookMap; // Map zur Speicherung von Büchern
    private Map<String, List<Object>> memberMap; // Map zur Speicherung von Mitgliedern
    private Map<String, List<Object>> lendingMap; // Map zur Speicherung von ausgeliehenen Büchern

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
     * Fügt, löscht oder bearbeitet ein Buch in die Datenbank und gleicht die bookMap ab.
     *
     * @param title Der Titel des Buches.
     * @param author Der Autor des Buches.
     * @param isbn Die ISBN des Buches.
     * @param id Die ID vom Buch.
     * @param command Statuscode:
     * <ul>
     *     <li>0: Neues Buch hinzufügen</li>
     *     <li>1: Buch aktualisieren</li>
     *     <li>2: Buch löschen</li>
     * </ul>
     */
    public void setBookMap(String title, String author, String isbn, int id, int command) {
        bookMap = setterBookMap(title, author, isbn, id, command, bookMap);
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
     * Fügt, löscht oder bearbeitet ein Mitglied in der Datenbank und gleicht die memberMap ab.
     *
     * @param name Der Name vom Mitglied.
     * @param email Die E-Mail vom Mitglied.
     * @param phone Die Telefonnummer vom Mitglied.
     * @param id Der Index vom Mitglied. Falls nicht bekannt dann 0.
     * @param command Statuscode:
     *                <ul>
     *                <li>0: Neues Mitglied hinzufügen</li>
     *                <li>1: Mitglied aktualisieren</li>
     *                <li>2: Mitglied löschen</li>
     *                </ul>
     * */
    public void setMemberMap(String name, String email, String phone, int id, int command) {
        memberMap = setterMemberMap(name, email, phone, id, command, memberMap);
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
     * Methode für die Speicherung, Aktualisierung oder Löschens der ausgeliehenen Bücher.
     *
     * @param bookTitle Der Titel des Buches.
     * @param memberName Der Name des Mitglieds.
     * @param lendDate Das Datum vom Ausleihen.
     * @param returnDate Das Datum der Rückgabe.
     * @param command Statuscode:
     *                <ul>
     *                <li>0: Neues ausgeliehenes Buch hinzufügen</li>
     *                <li>1: Ausgeliehenes Buch, Mitglied oder Datum aktualisieren</li>
     *                <li>2: Daten Löschen</li>
     *                </ul>
     * @param id Die ID der Daten. Falls unbekannt, dann 0.
     * */
    public void setLendingMap(String bookTitle, String memberName, LocalDate lendDate, LocalDate returnDate,
                        int command, int id) {
        this.lendingMap = manageLendingBook(bookTitle, memberName, lendDate, returnDate, lendingMap, command, id);
    }
     /**
     * Stellt die Verbindung zur Datenbank her und lädt die Daten.
     * Wenn die Datenbank nicht existiert, wird sie erstellt.
     */
    public void connection() {
        String url = "jdbc:postgresql://localhost:5432/";

        try {
            Class.forName("org.postgresql.Driver");

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
                    } catch (SQLException e) {
                        System.out.println("Connection error (02): " + e.getMessage());
                        new HelperClass().showErrorDialog("Fehler beim erstellen einer Verbindung (02): " + e.getMessage());
                    }
                } else {
                    statement.executeUpdate("CREATE DATABASE bibliotrack");

                    try (Connection bibConnection = DriverManager.getConnection(bibUrl, user, password)) {
                        createDatabase(bibConnection);
                    } catch (SQLException e) {
                        System.out.println("Connection error (03): " + e.getMessage());
                        new HelperClass().showErrorDialog("Fehler beim erstellen einer Verbindung (03): " + e.getMessage());
                    }
                }
            } catch (SQLException e) {
                System.out.println("Connection error (00): " + e.getMessage());
                new HelperClass().showErrorDialog("Fehler beim erstellen einer Verbindung: (00)" + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC Driver not found.", e);
        }
    }

    /**
     * Lädt die Daten aus der Datenbank und speichert sie in den entsprechenden Maps.
     *
     * @param connection Die Verbindung zur Bibliotrack-Datenbank.
     */
    private void loadData(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            bookMap = getBookData(statement);
            memberMap = getMemberData(statement);
            lendingMap = getLendingData(statement);
        } catch (SQLException e) {
            System.out.println("Connection error (01): " + e.getMessage());
            new HelperClass().showErrorDialog("Fehler beim erstellen einer Verbindung: (01)" + e.getMessage());
        }
    }

    /**
     * Erstellt in der neu erstellten Datenbank die Tabellen.
     *
     * @param connection Die Verbindung zur Bibliotrack-Datenbank.
     * */
    private void createDatabase(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            // Befehl für die Erstellung der Tabelle mit den Spalten
            String createBookCommand = """
                CREATE TABLE book(
                id SERIAL PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                author VARCHAR(255) NOT NULL,
                isbn VARCHAR(13) NOT NULL
                );""";
            String createMemberCommand = """
                CREATE TABLE member(
                id SERIAL PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                email VARCHAR(50) NOT NULL,
                phone VARCHAR(15) NOT NULL
                );""";
            String createLendingCommand = """
                CREATE TABLE lending(
                id SERIAL PRIMARY KEY,
                book_id INT,
                CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES book(id),
                member_id INT,
                CONSTRAINT fk_member FOREIGN KEY (member_id) REFERENCES member(id),
                book_title VARCHAR(255) NOT NULL,
                member_name VARCHAR(100) NOT NULL,
                lendDate DATE NOT NULL,
                returnDate DATE NOT NULL
                );""";

            // Führt die Befehle aus
            statement.executeUpdate(createBookCommand);
            statement.executeUpdate(createMemberCommand);
            statement.executeUpdate(createLendingCommand);
        } catch (SQLException e) {
            System.err.println("Error creating database: " + e.getMessage());
            new HelperClass().showErrorDialog("Fehler beim erstellen der Datenbank: " + e.getMessage());
        }
    }
}
