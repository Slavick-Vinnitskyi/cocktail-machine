package com.cocktails.machine.service.impl;

import com.cocktails.machine.model.Cocktail;
import com.cocktails.machine.service.DispenseService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.function.Consumer;

public class DispenseServiceImpl implements DispenseService {

    public static final int FINISH_DISPENSE = 100;
    public static final float DISPENSE_INCREMENT = 2.46f;
    public static final Duration DURATION = Duration.seconds(0.1);

    private Timeline dispenseTimeline;
    private static DispenseService instance;

    public static DispenseService getInstance() {
        if (instance == null) {
            instance = new DispenseServiceImpl();
        }
        return instance;
    }

    @Override
    public boolean isReadyToDispense(Cocktail cocktail) {
        return true;
    }

    @Override
    public void dispense(Cocktail cocktail, Consumer<Float> updateCallback, Runnable finishCallback) {
        var currentProgress = new float[]{0.0f};
        dispenseTimeline = getDispenseTimeline(updateCallback, finishCallback, currentProgress);
        dispenseTimeline.play();
    }

    private Timeline getDispenseTimeline(Consumer<Float> updateCallback, Runnable finishCallback, float[] progress) {
        var timeline = new Timeline(
                new KeyFrame(DURATION, e -> {
                    progress[0] += DISPENSE_INCREMENT;
                    if (progress[0] >= FINISH_DISPENSE) {
                        progress[0] = FINISH_DISPENSE;
                        finishCallback.run();
                        dispenseTimeline.stop();
                    }
                    updateCallback.accept(progress[0]);
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        return timeline;
    }
}
