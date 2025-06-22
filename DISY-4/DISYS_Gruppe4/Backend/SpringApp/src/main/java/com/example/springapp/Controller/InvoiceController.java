package com.example.springapp.Controller;

import com.example.springapp.Service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }


    @PostMapping("/invoices/{customerID}")
    public ResponseEntity<String> gatherData(@PathVariable String customerID) {

        boolean requestSent = invoiceService.createInvoice(customerID);

        if (requestSent) return new ResponseEntity<>("The request to gather the data has been sent!", HttpStatus.OK);

        return new ResponseEntity<>("The request to gather the data could not be sent!", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @GetMapping("/invoices/{customerID}")
    public ResponseEntity<List<String>> gatherInvoice(@PathVariable String customerID) {

        List<String> invoiceInfo = invoiceService.getInvoice(Integer.parseInt(customerID));

        if (invoiceInfo != null) {
            return new ResponseEntity<>(invoiceInfo, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
