package org.example.PdfGenerator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.example.Data.Customer;

import java.util.concurrent.TimeoutException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Locale;
import java.time.LocalTime;
import java.time.format.TextStyle;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.nio.charset.StandardCharsets;
import org.example.Service.Database; // korrekter Import
import com.rabbitmq.client.ConnectionFactory;

/**
 * Diese Klasse ist verantwortlich für die Generierung von PDF-Rechnungen.
 * Sie empfängt Nachrichten über RabbitMQ, verarbeitet die Kundendaten und
 * erstellt daraus PDF-Rechnungen mit detaillierten Verbrauchsinformationen.
 */
public class PDFGenerator {

    /**
     * Hauptmethode zum Starten des PDF-Generators
     */
    public static void main(String[] args) {
        listen();
    }

    /**
     * Richtet einen RabbitMQ-Listener ein, der auf eingehende Nachrichten wartet
     * und diese zur PDF-Generierung weiterleitet
     */
    private static void listen() {
        // RabbitMQ Verbindung einrichten
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare("DCR_PG", false, false, false, null);
            System.out.println(" [*] Warte auf Nachrichten von DCR. CTRL+C zum Beenden.");

            // Callback für eingehende Nachrichten
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Empfangen '" + message + "'");

                // Nachrichtenverarbeitung
                float kwh = 0;
                int customerId = 0;

                // Parse die Nachricht in ihre Bestandteile
                String[] pairs = message.split("&");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=");
                    if (keyValue.length == 2) {
                        if (keyValue[0].equals("id")) {
                            customerId = Integer.parseInt(keyValue[1]);
                        } else if (keyValue[0].equals("totalKWH")) {
                            kwh = Float.parseFloat(keyValue[1]);
                        }
                    }
                }

                // Kundendaten aus der Datenbank abrufen
                Customer customer = Database.select(customerId);

                // PDF-Generierung starten
                generate(kwh, customer);
            };

            channel.basicConsume("DCR_PG", true, deliverCallback, consumerTag -> {});

            // Warte auf Beendigung
            while (true) {
                Thread.sleep(1000);
            }

        } catch (IOException | TimeoutException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generiert eine PDF-Rechnung mit den gegebenen Kundeninformationen
     * @param kwh Der Stromverbrauch in Kilowattstunden
     * @param customer Das Kundenobjekt mit den Kundendaten
     */
    public static void generate(float kwh, Customer customer) {
        System.out.println("Generating PDF...");

        try (PDDocument document = new PDDocument()) {
            PDPage firstPage = new PDPage();
            document.addPage(firstPage);

            int pageHeight = (int) firstPage.getTrimBox().getHeight();
            
            PDFont font = PDType1Font.HELVETICA_BOLD;

            // PDF-Inhalt erstellen
            try (PDPageContentStream contentStream = new PDPageContentStream(document, firstPage)) {
                contentStream.beginText();
                contentStream.setLeading(16.0f);
                contentStream.newLineAtOffset(50, pageHeight-50);

                // Titel
                contentStream.setFont(font, 20);
                contentStream.showText("Invoice");
                contentStream.newLine();
                contentStream.newLine();

                // Datum
                contentStream.setFont(font, 16);
                contentStream.showText("This was created on the: " + date());
                contentStream.newLine();
                contentStream.newLine();

                // Kundeninformationen
                contentStream.setFont(font, 12);
                contentStream.showText("Customer Information");
                contentStream.newLine();
                contentStream.showText("Name: " + customer.getFirstName() + " " + customer.getLastName());
                contentStream.newLine();
                contentStream.showText("Customer ID: " + customer.getId());
                contentStream.newLine();
                contentStream.newLine();
                contentStream.newLine();

                // Abrechnungsinformationen
                contentStream.setFont(font, 16);
                contentStream.showText("Charge information");
                contentStream.newLine();
                contentStream.newLine();

                contentStream.setFont(font, 12);
                contentStream.showText(String.format("Your Total charges: %.2f kwH", kwh));
                contentStream.newLine();
                contentStream.showText(String.format("Your Total price: %.2f €", kwh*0.5));

                contentStream.endText();
            }

            // PDF speichern
            document.save(".\\Backend\\FileStorage\\" + customer.getId() + ".pdf");
            System.out.println("The PDF has been created!");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Erstellt einen formatierten Datums- und Zeitstring
     * @return Formatierter String mit aktuellem Datum und Uhrzeit
     */
    private static String date() {
        return String.format(
                "%s %02d, %04d (%02d:%02d:%02d)",
                LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, Locale.US),
                LocalDate.now().getDayOfMonth(),
                LocalDate.now().getYear(),
                LocalTime.now().getHour(),
                LocalTime.now().getMinute(),
                LocalTime.now().getSecond());
    }
}