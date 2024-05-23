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
            Statement statement = connection.createStatement();
            List<Object> mapList = new ArrayList<>();
            int index = id; // Index für das neu erstellte Buch

            // Fügt den Titel, Author und ISBN in eine Liste und diese dann in die Buch-Map
            mapList.add(title);
            mapList.add(author);
            mapList.add(isbn);

            // SQL Befehl zum Einfügen von einem neuen Buch
            String insertCommand = "INSERT INTO book(title, author, isbn) " +
                    "VALUES('" + title + "', '" + author + "', '" + isbn + "');";

            if (command == 1) {
                // SQL Befehl zum Überarbeiten vom Buch.
                insertCommand = "UPDATE book SET " +
                        "title = '" + title + "' , " +
                        "author = '" + author + "' , " +
                        "isbn = '" + isbn + "' " +
                        "where id = " + id;
                bookMap.remove(String.valueOf(index));
                bookMap.put(String.valueOf(index), mapList);
            } else if (command == 2) {
                // SQL Befehl zum Löschen von einem Buch
                insertCommand = "DELETE from book where id = " + id;
                bookMap.remove(String.valueOf(index));
            }

            statement.executeUpdate(insertCommand);

            if (command == 0) {
                // Abfrage zum Bestimmen der ID des neuen Buches
                ResultSet resultSet = statement.executeQuery("SELECT * FROM book");

                while (resultSet.next()) {
                    index = resultSet.getInt("id");
                }

                bookMap.put(String.valueOf(index), mapList);
            }

            return bookMap;
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
     *                <li>0: Mitglied löschen</li>
     *                </ul>
     * */
    protected Map<String, List<Object>> setterMemberMap(String name, String email, String phone, int id,
                                               int command, Map<String, List<Object>> memberMap) {
        try (Connection connection = DriverManager.getConnection(bibUrl, user, password)) {
            Statement statement = connection.createStatement();
            List<Object> mapList = new ArrayList<>();
            int index = id; // Index für das neu erstellte Buch

            // Fügt den Titel, Author und ISBN in eine Liste und diese dann in die Buch-Map
            mapList.add(name);
            mapList.add(email);
            mapList.add(phone);

            // SQL Befehl zum Einfügen von einem neuen Buch
            String insertCommand = "INSERT INTO member(name, email, phone) " +
                    "VALUES('" + name + "', '" + email + "', '" + phone + "');";

            if (command == 1) {
                // SQL Befehl zum Überarbeiten vom Buch.
                insertCommand = "UPDATE member SET " +
                        "name = '" + name + "' , " +
                        "email = '" + email + "' , " +
                        "phone = '" + phone + "' " +
                        "where id = " + id;
                memberMap.remove(String.valueOf(index));
                memberMap.put(String.valueOf(index), mapList);
            } else if (command == 2) {
                // SQL Befehl zum Löschen von einem Buch
                insertCommand = "DELETE from member where id = " + id;
                memberMap.remove(String.valueOf(index));
            }

            statement.executeUpdate(insertCommand);

            if (command == 0) {
                // Abfrage zum Bestimmen der ID des neuen Buches
                ResultSet resultSet = statement.executeQuery("SELECT * FROM book");

                while (resultSet.next()) {
                    index = resultSet.getInt("id");
                }

                memberMap.put(String.valueOf(index), mapList);
            }
            
            return memberMap;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
