# Changelog
Alle nennenswerten Änderungen an diesem Projekt werden in dieser Datei dokumentiert.


## Änderungen vor der Basisversion
## Inhaltsverzeichnis
- [2024-05-21](#2024-05-21)
  - [Refaktorierung](#refaktorierung)
  - [Main.java](#mainjava)
  - [Books.java](#booksjava)
  - [DataHandler.java](#datahandlerjava)
  - [ISBNValidator.java](#isbnvalidator)
- [2024-05-17](#2024-05-17)
  - [Refaktorierung](#refaktorierung-1)
  - [Main.java](#mainjava-1)
  - [Member.java](#memberjava)
  - [Lending.java](#lendingjava)
  - [Books.java](#booksjava-1)

### 2024-05-21
#### Refaktorierung
- Methode zum Hinzufügen neuer Bücher
- Neue Dateien hinzugefügt: DataHandler.java, ISBNValidator

#### Main.java
- Im Konstruktor wird eine Verbindung zur Datenbank hergestellt

#### Books.java
- Im Konstruktor wird der übergebene Handler initialisiert
- Listener zum Hinzufügen-Button hinzugefügt
- Methode für das Hinzufügen eines neuen Buches erstellt
- Methode für die Überprüfung vom ISBN
- Methode für den Speichern-Button
- Methode für den Messagedialog vom Fehlertext

#### DataHandler.java
- Getter und setter Methode für die Bücher, Mitglieder und ausgeliehene Bücher
- Methode zur Herstellung einer Verbindung zur Datenbank
- Methode zum Laden der Daten
- Methode zur Erstellung einer Datenbank

#### ISBNValidator
- Methode zur Überprüfung der ISBN 
- Methode zum Validieren von ISBN-10
- Methode zum Validieren von ISBN-13


### 2024-05-17
#### Refaktorierung
- GUI Erstellung
- Neue Dateien hinzugefügt: Main.java, Member.java, Lending.java, Books.java, CHANGELOG.md

#### Main.java
- Das Hauptfenster wird erstellt
- Methode zur Erstellung der Menüleiste
- Methode zur Erstellung des Inhaltes im Hauptfenster

#### Member.java
- Erstellung der Tabelle
- Methode zur Erstellung der Widgets

#### Lending.java
- Erstellung der Tabelle
- Methode zur Erstellung der Widgets

#### Books.java
- Erstellung der Tabelle
- Methode zur Erstellung der Widgets