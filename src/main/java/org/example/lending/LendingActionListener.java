package org.example.lending;

import org.example.data.DataHandler;
import org.example.helper.HelperClass;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Diese Klasse implementiert den ActionListener für die Buchausleihe.
 */
public class LendingActionListener {
    private final LendingHelper lendingHelper = new LendingHelper();
    private final DataHandler dataHandler;
    private final JComboBox<String> bookComboBox;
    private final JComboBox<String> memberComboBox;

    /**
     * Konstruktor für LendingActionListener.
     *
     * @param dataHandler   Ein Objekt, das Datenmanipulationsmethoden bereitstellt.
     * @param bookComboBox  Das JComboBox-Objekt zur Auswahl des Buches.
     * @param memberComboBox Das JComboBox-Objekt zur Auswahl des Mitglieds.
     */
    public LendingActionListener(DataHandler dataHandler, JComboBox<String> bookComboBox,
                                 JComboBox<String> memberComboBox) {
        this.dataHandler = dataHandler;
        this.bookComboBox = bookComboBox;
        this.memberComboBox = memberComboBox;
    }

    /**
     * Diese Methode führt den Ausleihvorgang durch.
     *
     * @param dayInt Der Wert für den Tag des Rückgabedatums.
     * @param monthInt Der Wert für den Monat des Rückgabedatums.
     * @param yearInt Der Wert für das Jahr des Rückgabedatums.
     * @param books Die Liste der Buchtitel.
     * @param members Die Liste der Mitgliedernamen.
     * @param lendingTableModel {@code DefaultTableModel} von der Tabelle der Ausleihe.
     * @return Wenn die Auswahl gültig ist, wird der Ausleihvorgang verarbeitet.
     * */
    public boolean lendBook(int dayInt, int monthInt, int yearInt, List<String> books, List<String> members,
                            DefaultTableModel lendingTableModel) {

        String bookTitle = (String) bookComboBox.getEditor().getItem();
        String memberName = (String) memberComboBox.getEditor().getItem();

        if (bookTitle.contains("(ausgeliehen)")) {
            showErrorDialog("Das eingegebene Buch wurde schon ausgeliehen.");
            return false;
        }

        if (!isValidSelection(bookTitle, memberName, books, members)) {
            return false;
        }

        return processLending(dayInt, monthInt, yearInt, bookTitle, memberName, lendingTableModel, books, members);
    }

    /**
     * Überprüft die Auswahl vom Buch und Mitglied. Wenn es ungültig ist, dann wird eine entsprechende
     * Fehlermeldung angezeigt.
     *
     * @param bookTitle Der Titel vom Buch.
     * @param memberName Der Name bom Mitglied.
     * @param books Die Liste der Buchtitel.
     * @param members Die Liste der Mitglieder.
     * @return Wenn die Eingaben richtig sind, ist die Methode gültig.
     * */
    private boolean isValidSelection(@NotNull String bookTitle, String memberName, List<String> books,
                                     List<String> members) {
        if (bookTitle.isEmpty()) {
            showErrorDialog("Die Auswahl für ein Buch darf nicht leer sein.");
        } else if (!books.contains(bookTitle)) {
            showErrorDialog("Das eingegebene Buch gibt es nicht.");
        } else if (memberName.isEmpty()) {
            showErrorDialog("Die Auswahl für ein Mitglied darf nicht leer sein.");
        } else if (!members.contains(memberName)) {
            showErrorDialog("Das eingegebene Mitglied existiert nicht.");
        } else {
            return true;
        }

        return false;
    }

    /**
     * Diese Methode überprüft das Ausleihdatum und das Rückgabedatum des Ausleihvorgangs.
     *
     * @param dayInt Der Wert für den Tag des Rückgabedatums.
     * @param monthInt Der Wert für den Monat des Rückgabedatums.
     * @param yearInt Der Wert für das Jahr des Rückgabedatums.
     * @param bookTitle Der Titel für das Buch.
     * @param memberName Der Name des Mitglieds.
     * @param lendingTableModel {@code DefaultTableModel} von der Tabelle der Ausleihe.
     * @param books Die Liste der Buchtitel.
     * @param members Die Liste der Mitgliedernamen.
     * @return Wenn der Ausleihvorgang korrekt ist, werden die Daten gespeichert.
     * */
    private boolean processLending(int dayInt, int monthInt, int yearInt, String bookTitle, String memberName,
                               DefaultTableModel lendingTableModel, List<String> books, List<String> members) {

        CharSequence returnDataChar = lendingHelper.datePickerDialog(dayInt, monthInt, yearInt);
        LocalDate lendDate = LocalDate.now();
        LocalDate returnDate = LocalDate.parse(returnDataChar);

        if (returnDate.isAfter(lendDate)) {
            completeLending(bookTitle, memberName, lendDate, returnDate, lendingTableModel);
            return true;
        } else if (returnDate.equals(LocalDate.of(1900, 1, 1))) {
            System.out.println("Abgebrochen");
        }  else if (lendDate.isAfter(returnDate)) {
            showErrorDialog("Das Rückgabedatum darf nicht in der Vergangenheit liegen.");
            List<String> returnDateList = new ArrayList<>(List.of(returnDate.toString().split("-")));
            int day = Integer.parseInt(returnDateList.get(2)) - 1;
            int month = Integer.parseInt(returnDateList.get(1)) - 1;
            int year = Integer.parseInt(returnDateList.get(0)) - LocalDate.now().getYear();

            return lendBook(day, month, year, books, members, lendingTableModel);
        }

        return false;
    }

    /**
     * Diese Methode speichert die Daten des Ausleihvorgangs und fügt die neuen Daten in die Tabelle.
     *
     * @param bookTitle Der Titel für das Buch.
     * @param memberName Der Name des Mitglieds.
     * @param lendDate Das Ausleihdatum.
     * @param returnDate Das Rückgabedatum.
     * @param lendingTableModel {@code DefaultTableModel} von der Tabelle der Ausleihe.
     */
    private void completeLending(String bookTitle, String memberName, LocalDate lendDate, LocalDate returnDate,
                                 @NotNull DefaultTableModel lendingTableModel) {
        dataHandler.setLendingMap(bookTitle, memberName, lendDate, returnDate, 0, 0);
        Object[] values = getLatestLendingValues();
        lendingTableModel.addRow(values);
    }

    /**
     * Diese Methode liefert die Werte der letzten hinzugefügten Ausleihe.
     *
     * @return Gibt das {@code Object[]} zurück mit den entsprechenden Werten.
     * */
    private Object @NotNull [] getLatestLendingValues() {
        Map<String, List<Object>> lendingMap = dataHandler.getLendingMap();
        List<String> keyList = new ArrayList<>(lendingMap.keySet());
        String newKey = "0";

        for (String key : keyList) {
            int id = Integer.parseInt(key);
            int newKeyId = Integer.parseInt(newKey);

            if (id > newKeyId) {
                newKey = key;
            }
        }

        return lendingMap.get(newKey).toArray();
    }

    /**
     * Diese Methode ruft den Fehlerdialog aus.
     *
     * @param message Die Nachricht die bei der Fehlermeldung ausgegeben wird.
     * */
    private void showErrorDialog(String message) {
        new HelperClass().showErrorDialog(message);
    }
}
