package org.example.data;

import java.sql.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HelperLendingData {
    public final String bibUrl = "jdbc:postgresql://localhost:5432/bibliotrack";
    public final String user = "postgres"; // TODO Namen anpassen
    public final String password = ""; // TODO Passwort anpassen

    /**
     * Diese Methode verwaltet die Ausleihvorgänge.
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
     * @return Gibt die überarbeitete {@code Map} aus.
     * */
    protected Map<String, List<Object>> manageLendingBook(String bookTitle, String memberName, LocalDate lendDate,
                                                 LocalDate returnDate, Map<String, List<Object>> lendingMap,
                                                 int command, int id) {
        try (Connection connection = DriverManager.getConnection(bibUrl, user, password)) {
            return switch (command) {
                case 0 -> addLending(connection, bookTitle, memberName, lendDate, returnDate, lendingMap);
                case 1 -> updateLending(connection, bookTitle, memberName, lendDate, returnDate, lendingMap, id);
                case 2 -> deleteLending(connection, lendingMap, id);
                default -> throw new IllegalArgumentException("Invalid command: " + command);
            };
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        }

        return lendingMap;
    }

    /**
     * Gibt die gesuchte ID aus.
     *
     * @param statement Das Statement zum Ausführen des SQL Befehls.
     * @param parameter Der gesuchte Parameter.
     * @param errorMessage Die Fehlermeldung, falls etwas schiefgeht.
     * @return Gibt die gesuchte ID aus.
     * */
    private int getEntityId(PreparedStatement statement, String parameter, String errorMessage)
            throws SQLException {
        statement.setString(1, parameter);

        try (ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                throw new SQLException(errorMessage);
            }

            return resultSet.getInt("id");
        }
    }

    /**
     * Fügt einen neuen Ausleihdatensatz in die Datenbank ein.
     *
     * @param connection Die Verbindung zur Datenbank.
     * @param bookTitle Der Titel des auszuleihenden Buches.
     * @param memberName Der Name des Mitglieds, das das Buch ausleiht.
     * @param lendDate Das Datum, an dem das Buch ausgeliehen wird.
     * @param returnDate Das Datum, an dem das Buch zurückgegeben werden soll.
     * @param map Die Map, die die aktuellen Ausleihdatensätze speichert.
     * @return Die aktualisierte Map der Ausleihdatensätze.
     * @throws SQLException Wenn ein Datenbankzugriffsfehler auftritt.
     */
    private Map<String, List<Object>> addLending(
            Connection connection, String bookTitle, String memberName, LocalDate lendDate,
            LocalDate returnDate, Map<String, List<Object>> map) throws SQLException {
        String sql = """
            INSERT INTO lending (
            book_id, member_id, book_title, member_name, lenddate, returndate
            ) VALUES(?, ?, ?, ?, ?, ?) RETURNING id;
            """;

        try (PreparedStatement bookStatement = connection.prepareStatement(
                "SELECT * FROM book WHERE title = ?");
             PreparedStatement memberStatement = connection.prepareStatement(
                "SELECT * FROM member WHERE name = ?");
             PreparedStatement insertLendingStatement = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false);

            int bookID = getEntityId(bookStatement, bookTitle, "Book does not exist");
            int memberID = getEntityId(memberStatement, memberName, "Member does not exist");

            insertLendingStatement.setInt(1, bookID);
            insertLendingStatement.setInt(2, memberID);
            insertLendingStatement.setString(3, bookTitle);
            insertLendingStatement.setString(4, memberName);
            insertLendingStatement.setDate(5, Date.valueOf(lendDate));
            insertLendingStatement.setDate(6, Date.valueOf(returnDate));

            try (ResultSet resultSet = insertLendingStatement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");

                    map.put(String.valueOf(id),
                            Arrays.asList(bookID, bookTitle, memberID, memberName, lendDate, returnDate)
                    );
                }
            }

            connection.commit();
        }

        return map;
    }

    /**
     * Aktualisiert einen bestehenden Ausleihdatensatz in der Datenbank.
     *
     * @param connection Die Verbindung zur Datenbank.
     * @param bookTitle Der aktualisierte Titel des Buches.
     * @param memberName Der aktualisierte Name des Mitglieds.
     * @param lendDate Das aktualisierte Ausleihdatum.
     * @param returnDate Das aktualisierte Rückgabedatum.
     * @param map Die Map, die die aktuellen Ausleihdatensätze speichert.
     * @param id Die ID des zu aktualisierenden Ausleihdatensatzes.
     * @return Die aktualisierte Map der Ausleihdatensätze.
     * @throws SQLException Wenn ein Datenbankzugriffsfehler auftritt.
     */
    private Map<String, List<Object>> updateLending(
            Connection connection, String bookTitle, String memberName, LocalDate lendDate,
            LocalDate returnDate, Map<String, List<Object>> map, int id) throws SQLException {
        String sql = """
                       UPDATE lending SET book_id = ?, member_id = ?, book_title = ?, member_name = ?,
                       lenddate = ?, returndate = ? WHERE id = ?;""";
        try (PreparedStatement bookStatement = connection.prepareStatement("SELECT id FROM book WHERE title = ?");
             PreparedStatement memberStatement = connection.prepareStatement("SELECT id FROM member WHERE name = ?");
             PreparedStatement updateLendingStatement = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false);

            int bookID = getEntityId(bookStatement, bookTitle, "Book does not exist");
            int memberID = getEntityId(memberStatement, memberName, "Member does not exist");

            updateLendingStatement.setInt(1, bookID);
            updateLendingStatement.setInt(2, memberID);
            updateLendingStatement.setString(3, bookTitle);
            updateLendingStatement.setString(4, memberName);
            updateLendingStatement.setDate(5, Date.valueOf(lendDate));
            updateLendingStatement.setDate(6, Date.valueOf(returnDate));
            updateLendingStatement.setInt(7, id);

            updateLendingStatement.executeUpdate();

            map.put(String.valueOf(id), Arrays.asList(bookID, bookTitle, memberID, memberName, lendDate, returnDate));

            connection.commit();
        }
        return map;
    }

    /**
     * Löscht einen Ausleihdatensatz aus der Datenbank.
     *
     * @param connection Die Verbindung zur Datenbank.
     * @param map Die Map, die die aktuellen Ausleihdatensätze speichert.
     * @param id Die ID des zu löschenden Ausleihdatensatzes.
     * @return Die aktualisierte Map der Ausleihdatensätze.
     * @throws SQLException Wenn ein Datenbankzugriffsfehler auftritt.
     */
    private Map<String, List<Object>> deleteLending(
            Connection connection, Map<String, List<Object>> map, int id) throws SQLException {
        String sql = "DELETE FROM lending WHERE id = ?;";
        try (PreparedStatement deleteLendingStatement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);

            deleteLendingStatement.setInt(1, id);
            deleteLendingStatement.executeUpdate();

            connection.commit();

            map.remove(String.valueOf(id));
        }
        return map;
    }
}
