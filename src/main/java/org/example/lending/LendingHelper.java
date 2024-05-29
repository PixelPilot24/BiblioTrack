package org.example.lending;

import org.example.data.DataHandler;
import org.example.helper.HelperClass;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

/**
 * Diese Klasse stellt verschiedene Hilfsmethoden für die Buchausleihe bereit.
 */
public class LendingHelper {
    private final HelperClass helper = new HelperClass();
    private final DataHandler dataHandler = new DataHandler();

    private Map<String, List<Object>> currentLending;
    private int lendingRow;

    public String removedBookId;

    /**
     * Aktualisiert den Inhalt einer bestehenden Ausleihe.
     *
     * @param tableModel Das Tabellenmodell, das die Ausleihen darstellt.
     * @param row        Die Zeile der Ausleihe in der Tabelle.
     * @param map        Die Map, die die Ausleihen speichert.
     * @param bookTitle  Der Titel des Buches.
     * @param memberName Der Name des Mitglieds.
     */
    private void updateLendingContent(DefaultTableModel tableModel, int row, Map<String, List<Object>> map,
                                      String bookTitle, String memberName) {
        Date lendDate = (Date) tableModel.getValueAt(row, 4);
        Date returnDate = (Date) tableModel.getValueAt(row, 5);
        LocalDate lendLocalDate = LocalDate.parse(lendDate.toString());
        LocalDate returnLocalDate = LocalDate.parse(returnDate.toString());
        List<String> keyList = new ArrayList<>(map.keySet());
        int lendingIndex = Integer.parseInt(keyList.get(row));

        dataHandler.setLendingMap(bookTitle, memberName, lendLocalDate, returnLocalDate, 1, lendingIndex);
    }

    /**
     * Ändert das ausgeliehene Buch in einer bestehenden Ausleihe.
     *
     * @param booksId         Eine Map, die die IDs der Bücher speichert.
     * @param aValue          Der neue Buchtitel.
     * @param borrowedBooks   Eine Liste der ausgeliehenen Bücher.
     * @param tableModel      Das Tabellenmodell, das die Ausleihen darstellt.
     * @param row             Die Zeile der Ausleihe in der Tabelle.
     * @param lendingMap      Die Map, die die Ausleihen speichert.
     * @return Eine Liste mit der Buch-ID und dem Buchtitel, wenn die Änderung erfolgreich war, sonst eine leere Liste.
     */
    public List<String> changeBook(Map<String, String> booksId, Object aValue, List<String> borrowedBooks,
                                   DefaultTableModel tableModel, int row, Map<String, List<Object>> lendingMap) {
        String inputBookTitle = aValue.toString();
        String bookId = booksId.get(inputBookTitle);
        boolean bookExists = dataHandler.getBookMap().containsKey(bookId);
        boolean bookIsBorrowed = borrowedBooks.contains(bookId);

        if (!bookExists) {
            helper.showErrorDialog("Das eingegebene Buch existiert nicht.\n" + inputBookTitle);
        } else if (bookIsBorrowed) {
            helper.showErrorDialog("Das eingegebene Buch wurde schon ausgeliehen.\n" + inputBookTitle);
        } else {
            String memberName = (String) tableModel.getValueAt(row, 3);
            updateLendingContent(tableModel, row, lendingMap, inputBookTitle, memberName);

            int oldBookID = (Integer) tableModel.getValueAt(row, 0);
            int index = borrowedBooks.indexOf(String.valueOf(oldBookID));
            borrowedBooks.set(index, bookId);

            return Arrays.asList(bookId, inputBookTitle);
        }

        return new ArrayList<>();
    }

    /**
     * Ändert das Mitglied, das ein Buch ausgeliehen hat, in einer bestehenden Ausleihe.
     *
     * @param aValue      Der neue Mitgliedsname.
     * @param tableModel  Das Tabellenmodell, das die Ausleihen darstellt.
     * @param row         Die Zeile der Ausleihe in der Tabelle.
     * @return Eine Liste mit der Mitglieds-ID und dem Mitgliedsnamen, wenn die Änderung erfolgreich war, sonst eine leere Liste.
     */
    public List<String> changeMember(Object aValue, DefaultTableModel tableModel, int row) {
        String inputMemberName = aValue.toString();
        String newMemberId = null;
        boolean memberExists = false;
        Map<String, List<Object>> memberMap = dataHandler.getMemberMap();
        Map<String, List<Object>> lendingMap = dataHandler.getLendingMap();

        for (String key : memberMap.keySet()) {
            String memberName = memberMap.get(key).getFirst().toString();

            if (inputMemberName.equals(memberName)) {
                memberExists = true;
                newMemberId = key;
                break;
            }
        }

        if (!memberExists) {
            helper.showErrorDialog("Das eingegebene Mitglied existiert nicht.\n" + inputMemberName);
        } else {
            String bookTitle = (String) tableModel.getValueAt(row, 1);
            updateLendingContent(tableModel, row, lendingMap, bookTitle, inputMemberName);

            return Arrays.asList(newMemberId, inputMemberName);
        }

        return new ArrayList<>();
    }

    /**
     * Ändert das Ausleih- oder Rückgabedatum einer bestehenden Ausleihe.
     *
     * @param aValue Das neue Datum.
     * @return Eine Liste mit dem geänderten Datum, wenn die Änderung erfolgreich war, sonst eine leere Liste.
     */
    public List<String> changeDate(Object aValue) {
        String inputLendingDate = aValue.toString();
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate;

        try {
            localDate = LocalDate.parse(inputLendingDate, formatter1);
        } catch (DateTimeParseException e1) {
            try {
                localDate = LocalDate.parse(inputLendingDate, formatter2);
            } catch (DateTimeParseException e2) {
                System.err.println("Date Error: " + e2.getMessage());
                helper.showErrorDialog("Das Datumsformat ist ungültig:\n" +
                        inputLendingDate +
                        "\nGültige Formate: jjjj-mm-dd oder dd.mm.jjjj\n" +
                        "Fehlernachricht: " + e2.getMessage());

                return new ArrayList<>();
            }
        }

        return List.of(localDate.toString());
    }

    /**
     * Aktualisiert das geänderte Ausleih- oder Rückgabedatum in der Ausleih-Tabelle.
     *
     * @param tableModel Das Tabellenmodell, das die Ausleihen darstellt.
     * @param row        Die Zeile der Ausleihe in der Tabelle.
     * @param map        Die Map, die die Ausleihen speichert.
     */
    public void updateChangeDate(DefaultTableModel tableModel, int row, Map<String, List<Object>> map) {
        String bookTitle = (String) tableModel.getValueAt(row, 1);
        String memberName = (String) tableModel.getValueAt(row, 3);

        updateLendingContent(tableModel, row, map, bookTitle, memberName);
    }

    /**
     * Erstellt und gibt einen MouseListener zurück, der auf Mausklicks auf der Ausleih-Tabelle reagiert.
     *
     * @return Ein MouseListener, der die aktuelle Ausleihe speichert, wenn auf die Tabelle geklickt wird.
     */
    public MouseListener mouseListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                currentLending = new HashMap<>();

                if (e.getClickCount() == 1) {
                    Map<String, List<Object>> lendingMap = dataHandler.getLendingMap();
                    JTable target = (JTable) e.getSource();
                    int row = target.getSelectedRow();
                    List<String> lendingKey = new ArrayList<>(lendingMap.keySet());
                    String lendingID = lendingKey.get(row);
                    List<List<Object>> listsValues = new ArrayList<>(lendingMap.values());
                    List<Object> lendingValues = listsValues.get(row);
                    currentLending.put(lendingID, lendingValues);
                    lendingRow = row;
                }
            }
        };
    }

    /**
     * Führt die Rückgabe eines Buches durch und aktualisiert die Tabelle entsprechend.
     *
     * @param tableModel Das Tabellenmodell, das die Ausleihen darstellt.
     * @return true, wenn das Buch erfolgreich zurückgegeben wurde, false sonst.
     */
    public boolean returnBook(DefaultTableModel tableModel) {
        List<String> keySet = new ArrayList<>(currentLending.keySet());
        String lendingID = keySet.getFirst();
        List<Object> values = currentLending.get(lendingID);
        String bookTitle = values.get(1).toString();

        int messageResult = JOptionPane.showConfirmDialog(
                new JOptionPane(),
                "Wurde das Buch " + bookTitle + " zurückgegeben?",
                "Rückgabe",
                JOptionPane.YES_NO_OPTION
        );

        if (messageResult == JOptionPane.YES_OPTION) {
            int lendingIdInt = Integer.parseInt(lendingID);

            dataHandler.setLendingMap(
                    bookTitle, "", LocalDate.now(), LocalDate.now(), 2, lendingIdInt
            );
            tableModel.removeRow(lendingRow);
            removedBookId = values.getFirst().toString();

            JOptionPane.showMessageDialog(
                    new JOptionPane(),
                    bookTitle + " wurde zurückgegeben.",
                    "Rückgabe",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return true;
        }

        return false;
    }

    /**
     * Zeigt einen Dialog an, in dem das Rückgabedatum ausgewählt werden kann, und gibt das ausgewählte Datum zurück.
     *
     * @param dayInt   Der vorgewählte Tag.
     * @param monthInt Der vorgewählte Monat.
     * @param yearInt  Das vorgewählte Jahr.
     * @return Das ausgewählte Datum im Format "yyyy-MM-dd".
     */
    public CharSequence datePickerDialog(int dayInt, int monthInt, int yearInt) {
        Integer[] days = new Integer[31];
        for (int i = 0; i < 31; i++) {
            days[i] = i + 1;
        }

        String[] monthsNames = {"Januar", "Februar", "März", "April", "Mai", "Juni",
                "Juli", "August", "September", "Oktober", "November", "Dezember"};
        List<String> months = new ArrayList<>(List.of(monthsNames));

        Integer[] years = new Integer[100];
        for (int i = 0; i < 100; i++) {
            years[i] = LocalDate.now().getYear() + i;
        }

        JComboBox<Integer> dayComboBox = new JComboBox<>(days);
        dayComboBox.setSelectedIndex(dayInt);
        JComboBox<String> monthComboBox = new JComboBox<>(monthsNames);
        monthComboBox.setSelectedIndex(monthInt);
        JComboBox<Integer> yearComboBox = new JComboBox<>(years);
        yearComboBox.setSelectedIndex(yearInt);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.add(new JLabel("Tag:"));
        panel.add(dayComboBox);
        panel.add(new JLabel("Monat:"));
        panel.add(monthComboBox);
        panel.add(new JLabel("Jahr:"));
        panel.add(yearComboBox);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Rückgabedatum auswählen",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            Integer day = (Integer) dayComboBox.getSelectedItem();
            String month = (String) monthComboBox.getSelectedItem();
            Integer year = (Integer) yearComboBox.getSelectedItem();

            String formattedDay = String.format("%02d", day);
            Integer monthNumber = months.indexOf(month) + 1;
            String formattedMonth = String.format("%02d", monthNumber);

            String date = year + "-" + formattedMonth + "-" + formattedDay;

            return date.subSequence(0, date.length());
        } else {
            return "1900-01-01";
        }
    }
}