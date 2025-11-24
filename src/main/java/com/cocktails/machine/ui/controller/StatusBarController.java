package com.cocktails.machine.ui.controller;

import com.cocktails.machine.ui.mixin.StatusBarMixin;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class StatusBarController implements Initializable {
    @FXML private Label temperatureLabel;
    @FXML private Label wifiLabel;
    @FXML private HBox temperatureContainer;
    @FXML private HBox wifiContainer;

    private StatusBarMixin statusBarMixin = StatusBarMixin.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Bind status bar
        temperatureLabel.textProperty().bind(statusBarMixin.temperatureTextProperty());
        wifiLabel.textProperty().bind(statusBarMixin.wifiTextProperty());
        statusBarMixin.updateStatus();
    }
}

