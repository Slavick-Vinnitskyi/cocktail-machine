package com.cocktails.machine.ui.service;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.util.Duration;

import java.util.Random;

/**
 * Service that streams temperature data from a simulated source.
 * Uses Timeline for efficient periodic updates on the JavaFX thread.
 */
public class TemperatureStreamService {
    private static final Duration REGULARITY = Duration.seconds(1);
    private final DoubleProperty temperature = new SimpleDoubleProperty(22.5);
    private static final Random RANDOM = new Random();
    private Timeline timeline;

    public ReadOnlyDoubleProperty temperatureProperty() {
        return temperature;
    }

    public double getTemperature() {
        return temperature.get();
    }

    /**
     * Starts the temperature streaming service.
     * Fires the first update immediately, then continues with periodic updates.
     */
    public void startStreaming() {
        // Stop existing timeline if any
        if (timeline != null) {
            timeline.stop();
        }

        // Fire first update immediately so UI has a value right away
        updateTemperature();

        // Then start periodic updates
        timeline = new Timeline(new KeyFrame(REGULARITY, e -> updateTemperature()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTemperature() {
        double newValue = RANDOM.nextDouble(15, 30);
        temperature.set(newValue);
    }

    /**
     * Stops the temperature streaming service
     */
    public void stopStreaming() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }
}

