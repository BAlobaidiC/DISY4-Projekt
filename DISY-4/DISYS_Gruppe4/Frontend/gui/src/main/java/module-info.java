module at.technikum_wien.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires static lombok;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;


    opens at.technikum_wien.gui to javafx.fxml;
    exports at.technikum_wien.gui;
    opens at.technikum_wien.gui.model to com.fasterxml.jackson.databind;
}