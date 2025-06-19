import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Pattern;

public class MainController extends Application {       
    
    
    @FXML
    private Button gatherDataBtn;
    
    @FXML
    private Button downloadBtn;
    
    @FXML
    private TextField CIDField1;
    
    @FXML
    private TextField CIDField2;
    
    @FXML
    private Label InvoiceInformation;
    
    @FXML
    private TextArea InvoiceCreationresponse;


    @FXML
    public void initialize() {
        gatherDataBtn.setOnAction(event -> gatherData(CIDField1.getText()));
        downloadBtn.setOnAction(event -> InvoiceDownload(CIDField2.getText()));
    }

    @FXML
    private void gatherData(String id) {
        if (!id.isEmpty() && Pattern.matches("^[0-9]+$", id)) {

            
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("http://localhost:8080/invoices/%s", id)))
                    .POST(HttpRequest.BodyPublishers.ofString(id))
                    .build();

            
            try {
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body)
                        .thenAccept(body -> Platform.runLater(() -> InvoiceInformation.setText(body)))
                        .join();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        } else if (id.isEmpty()) {
            InvoiceInformation.setText("Please enter a Customer ID!");
            
        } else {
            InvoiceInformation.setText("Only numbers are valid!");
        }
    }

    
    private void InvoiceDownload(String id) {
        if (!id.isEmpty() && Pattern.matches("^[0-9]+$", id)) {
            

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("http://localhost:8080/invoices/%s", id)))
                    .GET()
                    .build();

            
            try {
                HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

                
                int statusCode = response.statusCode();
                
                if (statusCode >= 200 && statusCode < 300) {
                    String responseBody = response.body();
                    Platform.runLater(() -> InvoiceCreationresponse.setText(responseBody));
                    InvoiceCreationresponse.setVisible(true);
                    
                    try {
                        File file = new File(".\\Backend\\FileStorage\\"+id+".pdf");
                        HostServices hostServices = getHostServices();
                        hostServices.showDocument(file.getAbsolutePath());
                        
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    
                } else {
                    System.err.println();
                    InvoiceCreationresponse.setText("HTTP Error: " + statusCode);
                    InvoiceCreationresponse.setVisible(true);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            
        } else if (id.isEmpty()) {
            InvoiceCreationresponse.setText("Please enter a Customer ID!");
            InvoiceCreationresponse.setVisible(true);

            
        } else {
            InvoiceCreationresponse.setText("Only numbers are valid!");
            InvoiceCreationresponse.setVisible(true);

        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        // muss wegen Application eingefügt sein
    }
}