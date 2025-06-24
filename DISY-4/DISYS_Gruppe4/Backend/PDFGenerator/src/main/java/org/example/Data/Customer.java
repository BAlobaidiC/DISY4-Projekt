package org.example.Data;

/**
 * Diese Klasse repräsentiert einen Kunden im System.
 * Sie speichert grundlegende Kundeninformationen wie ID, Vor- und Nachname.
 * Alle Attribute sind als statisch deklariert, was bedeutet, dass sie von allen Instanzen geteilt werden.
 */
public class Customer {

    // Statische Attribute für Kundeninformationen
    private static int id = 0;                  // Eindeutige Kundenidentifikationsnummer
    private static String firstName = null;      // Vorname des Kunden
    private static String lastName = null;       // Nachname des Kunden

    /**
     * Konstruktor für einen neuen Kunden
     * @param id Die Kundenidentifikationsnummer
     * @param firstName Der Vorname des Kunden
     * @param lastName Der Nachname des Kunden
     */
    public Customer(int id, String firstName, String lastName) {
        setId(id);
        setFirstName(firstName);
        setLastName(lastName);
    }

    /**
     * Gibt die ID des Kunden zurück
     * @return Die Kundenidentifikationsnummer
     */
    public int getId() {
        return id;
    }

    /**
     * Setzt die ID des Kunden
     * @param id Die neue Kundenidentifikationsnummer
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gibt den Vornamen des Kunden zurück
     * @return Der Vorname des Kunden
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Setzt den Vornamen des Kunden
     * @param firstName Der neue Vorname
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gibt den Nachnamen des Kunden zurück
     * @return Der Nachname des Kunden
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Setzt den Nachnamen des Kunden
     * @param lastName Der neue Nachname
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Überschreibt die toString()-Methode, um eine lesbare Darstellung des Kundenobjekts zu erstellen
     * @return Eine String-Repräsentation des Kundenobjekts mit ID, Vor- und Nachname
     */
    @Override
    public String toString() {
        return "User{id='"+ id + "', firstName='" + firstName + "', lastName='" + lastName + "'}\n";
    }
}