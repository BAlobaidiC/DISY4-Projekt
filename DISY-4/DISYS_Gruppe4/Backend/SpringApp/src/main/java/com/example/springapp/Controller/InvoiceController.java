package com.example.springapp.Controller;

import com.example.springapp.Service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/invoices/{customerID}")
    public ResponseEntity<String> gatherData(@PathVariable String customerID) {
        try {
            String cleanCustomerId = customerID.replaceAll("[^0-9]", "");
            if (cleanCustomerId.isEmpty()) {
                return new ResponseEntity<>("Ungültige Kunden-ID!", HttpStatus.BAD_REQUEST);
            }

            boolean requestSent = invoiceService.createInvoice(cleanCustomerId);

            if (requestSent) {
                return new ResponseEntity<>("Die Anfrage wurde erfolgreich gesendet!", HttpStatus.OK);
            }

            return new ResponseEntity<>("Die Anfrage konnte nicht gesendet werden!", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>("Fehler bei der Verarbeitung der Anfrage: " + e.getMessage(), 
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/invoices/{customerID}")
    public ResponseEntity<List<String>> gatherInvoice(@PathVariable String customerID) {
        try {
            String cleanCustomerId = customerID.replaceAll("[^0-9]", "");
            if (cleanCustomerId.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            List<String> invoiceInfo = invoiceService.getInvoice(Integer.parseInt(cleanCustomerId));

            if (invoiceInfo != null) {
                return new ResponseEntity<>(invoiceInfo, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}