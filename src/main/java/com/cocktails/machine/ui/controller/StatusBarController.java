package com.cocktails.machine.ui.controller;

import com.cocktails.machine.ui.service.StatusBarService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class StatusBarController implements Initializable {
    @FXML
    private Label temperatureLabel;
    @FXML
    private Label wifiLabel;

    private final StatusBarService statusBarService = StatusBarService.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Bind status bar
        temperatureLabel.textProperty().bind(statusBarService.temperatureTextProperty());
        wifiLabel.textProperty().bind(statusBarService.wifiTextProperty());
        statusBarService.updateStatus();
    }
}

