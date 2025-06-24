package com.example.springapp.Service;

import com.example.springapp.Queue.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.ZoneId;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceService {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);
    private static final String FILE_STORAGE_PATH = ".\\Backend\\FileStorage\\";

    @Autowired
    private Queue queue;

    public InvoiceService() {
        File directory = new File(FILE_STORAGE_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
            logger.info("FileStorage-Verzeichnis erstellt: {}", FILE_STORAGE_PATH);
        }
    }

    public boolean createInvoice(String customerID) {
        logger.info("Versuche Invoice zu erstellen für ID: {}", customerID);
        boolean result = queue.send(customerID);
        logger.info("Invoice-Erstellung Ergebnis: {}", result);
        return result;
    }

    public List<String> getInvoice(int customerID) {
        logger.info("Suche Invoice für ID: {}", customerID);
        
        String filePath = FILE_STORAGE_PATH + customerID + ".pdf";
        Path path = Paths.get(filePath);

        if (Files.exists(path) && Files.isRegularFile(path)) {
            List<String> invoiceInfo = new ArrayList<>();
            invoiceInfo.add(filePath);

            try {
                FileTime creationTime = Files.getLastModifiedTime(path);
                String formattedCreationTime = creationTime.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm"));
                invoiceInfo.add("Created: " + formattedCreationTime);
                logger.info("Invoice gefunden: {}", invoiceInfo);
                return invoiceInfo;

            } catch (IOException e) {
                logger.error("Fehler beim Lesen der Datei-Informationen: ", e);
                e.printStackTrace();
            }
        }

        logger.info("Keine Invoice gefunden für ID: {}", customerID);
        return null;
    }
}