module com.cocktails.machine {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires MaterialFX;
    requires com.google.gson;
    requires static lombok;
    requires org.slf4j;
    requires javafx.base;

    opens com.cocktails.machine to javafx.fxml;
    opens com.cocktails.machine.ui.controller to javafx.fxml;
    opens com.cocktails.machine.model to com.google.gson;
    exports com.cocktails.machine;
}
