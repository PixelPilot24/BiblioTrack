package org.example.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * In dieser Klasse werden die Maps für die Bücher und Mitglieder erstellt, geändert oder gelöscht.
 * */
public class SetMaps {
    protected final String bibUrl = "jdbc:postgresql://localhost:5432/bibliotrack";
    protected final String user = "postgres"; // TODO Namen anpassen
    protected final String password = ""; // TODO Passwort anpassen

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
    protected Map<String, List<Object>> setterBookMap(String title, String author, String isbn,
                                                      int id, int command, Map<String, List<Object>> bookMap) {
        try (Connection connection = DriverManager.getConnection(bibUrl, user, password)) {
            String sql = null;
            List<Object> mapList = new ArrayList<>();

            // Fügt den Titel, Author und ISBN in eine Liste und diese dann in die Buch-Map
            mapList.add(title);
            mapList.add(author);
            mapList.add(isbn);

            if (command == 0) {
                sql = "INSERT INTO book (title, author, isbn) VALUES(?, ?, ?) RETURNING id;";
            } else if (command == 1) {
                sql = "UPDATE book SET title = ?, author = ?, isbn = ? WHERE id = ?;";
            } else if (command == 2) {
                sql = "DELETE FROM book WHERE id = ?;";
            }

            return getTableMap(connection, sql, command, id, title, author, isbn, mapList, bookMap);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return bookMap;
        }
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
    protected Map<String, List<Object>> setterMemberMap(String name, String email, String phone, int id,
                                               int command, Map<String, List<Object>> memberMap) {
        try (Connection connection = DriverManager.getConnection(bibUrl, user, password)) {
            String sql = null;
            List<Object> mapList = new ArrayList<>();

            // Fügt den Titel, Author und ISBN in eine Liste und diese dann in die Buch-Map
            mapList.add(name);
            mapList.add(email);
            mapList.add(phone);

            if (command == 0) {
                // SQL Befehl zum Einfügen eines neuen Mitglieds
                sql =   "INSERT INTO member (name, email, phone) VALUES(?, ?, ?) RETURNING id;";
            } else if (command == 1) {
                // SQL Befehl zum Aktualisieren eines Mitglieds
                sql = "UPDATE member SET name = ?, email = ?, phone = ? WHERE id = ?";
            } else if (command == 2) {
                // SQL Befehl zum Löschen eines Mitglieds
                sql = "DELETE FROM member WHERE id = ?";
            }

            return getTableMap(connection, sql, command, id, name, email, phone, mapList, memberMap);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return memberMap;
        }
    }

    /**
     * In dieser Methode wird der SQL Befehl ausgeführt und die aktualisierte Map wieder ausgegeben.
     *
     * @param connection Die Verbindung zum Server.
     * @param sql Der Befehl für die Datenbank.
     * @param command Statuscode:
     *                <ul>
     *                    <li>0: Hinzufügen</li>
     *                    <li>1: Aktualisieren</li>
     *                    <li>2: Löschen</li>
     *                </ul>
     * @param id Die ID vom Inhalt.
     * @param firstRow Inhalt der ersten Spalte.
     * @param secondRow Inhalt der zweiten Spalte.
     * @param thirdRow Inhalt der dritten Spalte.
     * @param mapList Die Liste mit dem Inhalt der Zeile.
     * @param map Die Map die aktualisiert werden soll.
     * @return
     * <ul>
     *     <li>{@code Map<String, List<Object>>} Gibt das bearbeitete Map aus.</li>
     *     <lis>{@code null} Falls es keinen Befehl gab.</lis>
     * </ul>
     * */
    private Map<String, List<Object>> getTableMap(Connection connection, String sql, int command, int id,
                                                  String firstRow, String secondRow, String thirdRow,
                                                  List<Object> mapList, Map<String, List<Object>> map) throws SQLException {
        if (sql != null) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                if (command == 0) {
                    // Fügt den Inhalt in den Befehl zum Einfügen
                    preparedStatement.setString(1, firstRow);
                    preparedStatement.setString(2, secondRow);
                    preparedStatement.setString(3, thirdRow);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    // Bestimmt die ID und fügt alles in die Map
                    if (resultSet.next()) {
                        int newID = resultSet.getInt("id");

                        map.put(String.valueOf(newID), mapList);
                    }
                } else if (command == 1) {
                    // Fügt den Inhalt in den Befehl zum Aktualisieren
                    preparedStatement.setString(1, firstRow);
                    preparedStatement.setString(2, secondRow);
                    preparedStatement.setString(3, thirdRow);
                    preparedStatement.setInt(4, id);
                    preparedStatement.executeUpdate();

                    map.put(String.valueOf(id), mapList);
                } else if (command == 2) {
                    // Fügt den Inhalt in den Befehl zum Löschen
                    preparedStatement.setInt(1, id);
                    preparedStatement.executeUpdate();
                    map.remove(String.valueOf(id));
                }

                return map;
            }
        }
        return null;
    }
}
