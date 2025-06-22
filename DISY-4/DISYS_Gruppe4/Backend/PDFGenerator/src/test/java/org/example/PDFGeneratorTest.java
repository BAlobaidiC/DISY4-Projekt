package org.example;

import org.example.Data.Customer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PDFGeneratorTest {

    @Test
    public void getIdTest() {
        Customer customer = new Customer(3, "First", "Last");
        assertEquals(3, customer.getId());
    }

    @Test
    public void getFirstNameTest() {
        Customer customer = new Customer(3, "First", "Last");
        assertEquals("First", customer.getFirstName());
    }

    @Test
    public void getLastNameTest() {
        Customer customer = new Customer(3, "First", "Last");
        assertEquals("Last", customer.getLastName());
    }

    @Test
    public void customerRealInstanceTest() {
        Customer realCustomer = new Customer(1, "Ali", "Yilmaz");
        assertEquals(1, realCustomer.getId());
        assertEquals("Ali", realCustomer.getFirstName());
        assertEquals("Yilmaz", realCustomer.getLastName());
    }
}
