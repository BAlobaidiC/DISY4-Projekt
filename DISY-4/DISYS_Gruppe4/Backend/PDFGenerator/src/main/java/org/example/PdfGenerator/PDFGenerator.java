package org.example.PdfGenerator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.example.Service.Queue;
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

public class PDFGenerator {

    public static void main(String[] args) {
        Queue queue = new Queue();
        listen(queue);
    }

    private static void listen(Queue queue) {
        try {
            // Erstellen Sie eine neue ConnectionFactory für RabbitMQ
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");
            
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            
            channel.queueDeclare("DCR_PG", false, false, false, null);
            System.out.println(" [*] Warte auf Nachrichten von DCR. CTRL+C zum Beenden.");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Empfangen '" + message + "'");
                
                // Parse die Nachricht
                float kwh = 0;
                int customerId = 0;
                
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

                // Hole Customer-Daten aus der Datenbank
                Customer customer = Database.select(customerId); // Verwende die korrekte Methode
                
                // Generiere PDF
                generate(kwh, customer);
            };

            channel.basicConsume("DCR_PG", true, deliverCallback, consumerTag -> {});

        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public static void generate(float kwh, Customer customer) {

        System.out.println("Generating PDF...");

        PDDocument document = new PDDocument();
        PDPage firstPage = new PDPage();
        document.addPage(firstPage);

        int pageHeight = (int) firstPage.getTrimBox().getHeight();
        int pageWidth = (int) firstPage.getTrimBox().getWidth();

        PDFont font = PDType1Font.HELVETICA_BOLD;
        PDPageContentStream contentStream;

        try {
            contentStream = new PDPageContentStream(document, firstPage);
            contentStream.beginText();
            contentStream.setLeading(16.0f);
            contentStream.newLineAtOffset(50, pageHeight-50);


            contentStream.setFont(font, 20 );
            contentStream.showText("Invoice");
            contentStream.newLine();
            contentStream.newLine();


            contentStream.setFont(font, 16 );
            contentStream.showText("This was created on the: " + date());
            contentStream.newLine();
            contentStream.newLine();


            contentStream.setFont(font, 12 );
            contentStream.showText("Customer Information");
            contentStream.newLine();
            contentStream.showText("Name: " + customer.getFirstName() + " " + customer.getLastName());
            contentStream.newLine();
            contentStream.showText("Customer ID: " + customer.getId());
            contentStream.newLine();
            contentStream.newLine();
            contentStream.newLine();


            contentStream.setFont(font, 16 );
            contentStream.showText("Charge information");
            contentStream.newLine();
            contentStream.newLine();


            contentStream.setFont(font, 12 );
            contentStream.showText(String.format("Your Total charges: %.2f kwH", kwh));
            contentStream.newLine();
            contentStream.showText(String.format("Your Total price: %.2f €", kwh*0.5));


            contentStream.endText();
            contentStream.close();


            document.save(".\\Backend\\FileStorage\\" + customer.getId() + ".pdf");
            document.close();
            System.out.println("The PDF has been created!");


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


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