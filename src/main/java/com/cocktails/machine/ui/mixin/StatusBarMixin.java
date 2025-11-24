package com.cocktails.machine.ui.mixin;

import com.cocktails.machine.ui.service.TemperatureStreamService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Singleton mixin for status bar functionality.
 * Maintains persistent temperature streaming across screen changes.
 */
public class StatusBarMixin {
    private static StatusBarMixin instance;

    private final StringProperty temperatureText = new SimpleStringProperty("--");
    private final StringProperty wifiText = new SimpleStringProperty("Connected");
    private final TemperatureStreamService temperatureStreamService;
    private boolean isInitialized = false;

    public static final String TEMPERATURE_FORMAT = "%.1f°C";

    private StatusBarMixin() {
        temperatureStreamService = new TemperatureStreamService();
        // Subscribe to temperature updates from the streaming service
        temperatureStreamService.temperatureProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                setTemperature(newVal.doubleValue());
            }
        });
        // Display initial temperature value immediately
        setTemperature(temperatureStreamService.getTemperature());
    }

    /**
     * Gets the singleton instance of StatusBarMixin.
     * Creates the instance if it doesn't exist.
     */
    public static StatusBarMixin getInstance() {
        if (instance == null) {
            instance = new StatusBarMixin();
        }
        return instance;
    }

    public StringProperty temperatureTextProperty() {
        return temperatureText;
    }

    public String getTemperatureText() {
        return temperatureText.get();
    }

    public void setTemperatureText(String temperatureText) {
        this.temperatureText.set(temperatureText);
    }

    public void setTemperature(double temperature) {
        setTemperatureText(TEMPERATURE_FORMAT.formatted(temperature));
    }

    public StringProperty wifiTextProperty() {
        return wifiText;
    }

    public String getWifiText() {
        return wifiText.get();
    }

    public void setWifiText(String wifiText) {
        this.wifiText.set(wifiText);
    }

    public void updateStatus() {
        // Start streaming temperature data only once
        if (!isInitialized) {
            temperatureStreamService.startStreaming();
            isInitialized = true;
        }
        setWifiText("Connected");
    }

    /**
     * Stops the temperature streaming service (call when screen is closed)
     */
    public void stop() {
        temperatureStreamService.stopStreaming();
    }
}

