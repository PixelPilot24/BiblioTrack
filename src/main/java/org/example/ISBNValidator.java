package org.example;

/**
 * In dieser Klasse wird die übergebene ISBN überprüft.
 * */
public class ISBNValidator {
    /**
     * Validiert die eingegebene ISBN und gibt einen Statuscode zurück.
     *
     * @param input Die zu validierende ISBN.
     * @return Statuscode:
     * <ul>
     *     <li>0: wenn die ISBN korrekt ist</li>
     *     <li>1: wenn sie nicht numerisch ist</li>
     *     <li>2: wenn sie die falsche Länge hat</li>
     *     <li>3: wenn die Prüfziffer falsch ist</li>
     * </ul>
     */
    public int isValid(String input) {
        // Überprüft die Länge vom ISBN
        if (input.length() < 10) {
            return 2; // Der Wert hat nicht die richtige Länge
        } else {
            try {
                // Versucht die ersten 9 Zeichen in Integer umzuwandeln, weil die zehnte ein X sein kann
                Integer.parseInt(input.substring(0, 9));

                // Basierend auf der Länge vom ISBN, wird diese überprüft oder ein Fehler ausgegeben
                if (input.length() == 10) {
                    return isbn10validator(input);
                } else if (input.length() == 13) {
                    return isbn13validator(input);
                } else {
                    return 2; // Der Wert hat nicht die richtige Länge
                }
            } catch (Exception _) {
                return 1; // Wenn die Ausgabe nicht nummerisch ist
            }
        }
    }

    /**
     * Validiert eine ISBN-13.
     *
     * @param input Die zu validierende ISBN-13.
     * @return Statuscode:
     * <ul>
     *     <li>0: wenn die ISBN korrekt ist</li>
     *     <li>3: wenn die Prüfziffer falsch ist</li>
     * </ul>
     */
    private int isbn13validator(String input) {
        int sum = 0;
        int moduloNum;

        // Berechnung der Prüfziffer für die ersten 12 Ziffern
        for (int i = 0; i < 12; i++) {
            String num = input.substring(i, i + 1);
            int oneNum = Integer.parseInt(num);

            // Wenn die Position gerade ist, wird die Ziffer addiert,
            // andernfalls mit 3 multipliziert
            if (i % 2 == 0) {
                sum += oneNum;
            } else {
                sum = sum + (oneNum * 3);
            }
        }

        // Berechnung der letzten Ziffer (Prüfziffer)
        moduloNum = 10 - (sum % 10);
        String lastChar = input.substring(12,13);
        int lastNum = Integer.parseInt(lastChar);

        // Überprüft, ob die berechnete Prüfziffer mit der eingegebenen Prüfziffer übereinstimmt
        if (lastNum == moduloNum) {
            return 0; // Alles korrekt eingegeben
        } else {
            return 3; // Die letzte Zahl stimmt nicht überein
        }
    }

    /**
     * Validiert eine ISBN-10.
     *
     * @param input Die zu validierende ISBN-10.
     * @return Statuscode:
     * <ul>
     *     <li>0: wenn die ISBN korrekt ist</li>
     *     <li>3: wenn die Prüfziffer falsch ist</li>
     * </ul>
     */
    private int isbn10validator(String input) {
        int sum = 0;
        int moduloNum;

        // Berechnung der Prüfziffer für die ersten 9 Ziffern
        for (int i = 1; i < 10; i++) {
            String num = input.substring(i - 1, i);
            int oneNum = Integer.parseInt(num);
            sum = sum + (oneNum * i);
        }

        // Berechnung der Prüfziffer
        moduloNum = sum % 11;
        String lastChar = input.substring(9,10);

        // Überprüft, ob die berechnete Prüfziffer mit der letzten Ziffer übereinstimmt
        if (moduloNum == 10) {
            if (lastChar.equals("X")) {
                return 0; // Alles korrekt eingegeben
            } else {
                return 3; // Die letzte Zahl stimmt nicht überein
            }
        } else {
            int lastNum = Integer.parseInt(lastChar);

            if (lastNum == moduloNum) {
                return 0; // Alles korrekt eingegeben
            } else {
                return 3; // Die letzte Zahl stimmt nicht überein
            }
        }
    }
}
