# Changelog
Alle nennenswerten Änderungen an diesem Projekt werden in dieser Datei dokumentiert.


## Änderungen vor der Basisversion
## Inhaltsverzeichnis
- [2024-05-29](#2024-05-29)
- [2024-05-27](#2024-05-27)
- [2024-05-23](#2024-05-23)
- [2024-05-22](#2024-05-22)
- [2024-05-22](#2024-05-22-1)
- [2024-05-21](#2024-05-21)
- [2024-05-17](#2024-05-17)


## 2024-05-29
Refaktorierung
- Neue Dateien erstellt: HelperLendingData.java, LendingActionListener.java, LendingHelper.java
- Neue Methoden hinzugefügt: DataHandler.java, HelperDataHandler.java, Lending.java

DataHandler.java
- Methode zur Speicherung, Aktualisierung oder Löschens der ausgeliehenen Bücher hinzugefügt
- try catch in die connection Methode eingefügt
- loadDate Methode überarbeitet:
  - das Erstellen der Maps ausgelagert in HelperDataHandler.java
  - try catch hinzugefügt
- try catch in die createDatabase Methode eingefügt 

HelperDataHandler.java
- HelperDataHandler.java erbt von HelperLendingData.java
- Methode zum Abrufen der Bücher hinzugefügt
- Methode zum Abrufen der Mitglieder hinzugefügt
- Methode zum Abrufen der ausgeliehenen Daten hinzugefügt

HelperLendingData.java
- Methode zur Verwaltung der Ausleihvorgänge hinzugefügt
- Methode für die Ausgabe der ID hinzugefügt
- Methode zum Einfügen eines Datensatzes hinzugefügt
- Methode zum Aktualisieren eines bestehenden Datensatzes hinzugefügt
- Methode zum Löschen eines bestehenden Datensatzes hinzugefügt

LendingActionListener.java
- Methode zur Ausführung des Ausleihvorgangs hinzugefügt
- Methode zur Überprüfung der Eingabe vom Buch und Mitglied hinzugefügt
- Methode zur Überprüfung des Ausleihdatums und des Rückgabedatums hinzugefügt
- Methode zur Speicherung der Ausleihdaten hinzugefügt

LendingHelper.java
- Methode zur Aktualisierung der bestehenden Ausleihdaten hinzugefügt
- Methode zur Änderung eines Buches hinzugefügt
- Methode zur Änderung eines Mitglieds hinzugefügt
- Methode zur Änderung des Ausleih- und Rückgabedatums hinzugefügt
- Methode für den MouseListener
- Methode zur Rückgabe des ausgeliehenen Buches hinzugefügt
- Methode zur Erstellung von einem DatePicker hinzugefügt

Lending.java
- Methode zur Erstellung der Widgets überarbeitet
- Methode zur Filterung in der ComboBox hinzugefügt
- Methode zur Speicherung der Änderungen in der Ausleih-Tabelle hinzugefügt
- Methode für das Zurückgeben eines Buches hinzugefügt
- Methode für das Ausleihen eines Buches hinzugefügt
- Methode für die Filterung der Bücher
- Methode für die Filterung der Mitglieder


## 2024-05-27
Refaktorierung
- Neue Datei erstellt: HelperClass.java
- Methoden überarbeitet: AddButton.java, Member.java, Books.java, AddMemberButton.java, DataHandler.java, HelperDataHandler.java

HelperClass.java
- Methode zur bestimmung des Inhalts der Textfelder
- Methode zum Hinzufügen einer neuen Zeile
- Methode zum Anzeigen einer Fehlermeldung

AddButton.java und AddMemberButton.java
- Methoden von der HelperClass in saveButton Methode eingefügt

Member.java und Books.java
- Methoden von der HelperClass in saveChanges Methode eingefügt

DataHandler.java
- Try catch Statement in die connection Methode hinzugefügt

HelperDataHandler.java
- Methode zum Ausführen von dem SQL Befehl hinzugefügt
- setterMemberMap Methode überarbeitet:
  - Befehle anders gestaltet
  - Methode zum Ausführen des SQL Befehls eingefügt
- setterBookMap Methode überarbeitet:
  - Befehle anders gestaltet
  - Methode zum Ausführen des SQL Befehls eingefügt


## 2024-05-23
Refaktorierung
- Neue Datei erstellt: HelperDataHandler.java, AddMember.java
- Neues Verzeichnis erstellt: member
- Methden erstellt: Member.java
- Methoden überarbeitet: AddButton.java

DataHandler.java
- Funktion von setBookMap und setMemberMap Methoden nach HelperDataHandler.java ausgelagert

HelperDataHandler.java
- Methode zum Hinzufügen, Löschen, überarbeiten der Bücher
- Methode zum Hinzufügen, Löschen, überarbeiten der Mitglieder

Member.java
- In das Verzeichnis member verschoben
- Methode zur Erstellung der Tabelle hinzugefügt
- Methode zu Erstellung eines Buttons zum Löschen eines Mitgliedes hinzugefügt
- Methode zur Speicherung der Veränderung hinzugefügt

AddMemberButton.java
- Methode zur Erstellung des Buttons zum Speichern der Mitglieder
- Methode zur Überprüfung der Telefonnummer

AddButton.java
- Parameter für createDialog Methode hinzugefügt
  - String[] columnNames, JPanel mainPanel, boolean book
- getKeyAdapter Methode überarbeitet
  - Kondition für Pattern hinzugefügt
  - Kondition für die Länge der Eingabe hinzugefügt


## 2024-05-22
Refaktorierung
- Neue Methoden hinzugefügt: Books.java
- Methoden überarbeitet: DataHandler.java

Books.java
- Methode zur Erstellung eines Buttons zum Löschen
- Methode zur Löschung eines Buches

DataHandler.java
- setBookMap Methode überarbeitet:
  - Boolean "update" Parameter durch Integer "command" ersetzt
  - Kondition für den "DELETE" Befehl hinzugefügt


## 2024-05-22
Refaktorierung
- Neue Datei erstellt: AddButton.java
- Dateien verschoben:
  - AddButton.java, Books.java in books Ordner verschoben

DataHandler.java
- setBookMap Methode überarbeitet
  - Integer id und Boolean update Parameter
  - Kondition für das "UPDATE" Befehl hinzugefügt

Books.java
- Methode zur Erstellung der Tabelle
- Methode zur Speicherung der Änderung in der Tabelle

AddButton.java
- Methoden für das Hinzufügen eines neuen Buches in eine Klasse verschoben


## 2024-05-21
Refaktorierung
- Methode zum Hinzufügen neuer Bücher
- Neue Dateien hinzugefügt: DataHandler.java, ISBNValidator

Main.java
- Im Konstruktor wird eine Verbindung zur Datenbank hergestellt

Books.java
- Im Konstruktor wird der übergebene Handler initialisiert
- Listener zum Hinzufügen-Button hinzugefügt
- Methode für das Hinzufügen eines neuen Buches erstellt
- Methode für die Überprüfung vom ISBN
- Methode für den Speichern-Button
- Methode für den Messagedialog vom Fehlertext

DataHandler.java
- Getter und setter Methode für die Bücher, Mitglieder und ausgeliehene Bücher
- Methode zur Herstellung einer Verbindung zur Datenbank
- Methode zum Laden der Daten
- Methode zur Erstellung einer Datenbank

ISBNValidator
- Methode zur Überprüfung der ISBN 
- Methode zum Validieren von ISBN-10
- Methode zum Validieren von ISBN-13


## 2024-05-17
Refaktorierung
- GUI Erstellung
- Neue Dateien hinzugefügt: Main.java, Member.java, Lending.java, Books.java, CHANGELOG.md

Main.java
- Das Hauptfenster wird erstellt
- Methode zur Erstellung der Menüleiste
- Methode zur Erstellung des Inhaltes im Hauptfenster

Member.java
- Erstellung der Tabelle
- Methode zur Erstellung der Widgets

Lending.java
- Erstellung der Tabelle
- Methode zur Erstellung der Widgets

Books.java
- Erstellung der Tabelle
- Methode zur Erstellung der Widgets