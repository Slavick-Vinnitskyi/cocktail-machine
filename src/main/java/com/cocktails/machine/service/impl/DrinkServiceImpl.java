package com.cocktails.machine.service.impl;

import com.cocktails.machine.model.Cocktail;
import com.cocktails.machine.service.DrinkService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * *************************************************************************
 * * Yaypay CONFIDENTIAL 2025
 * * All Rights Reserved. * *
 * NOTICE: All information contained herein is, and remains the property of Yaypay Incorporated and its suppliers, if any.
 * The intellectual and technical concepts contained  herein are proprietary to Yaypay Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material  is strictly forbidden unless prior written permission is obtained  from Yaypay Incorporated.
 * Author : Vinnytskyi Viacheslav
 * Date Created: 2025/11/25
 */

public class DrinkServiceImpl implements DrinkService {

    private Timeline dispenseTimeline;
    private static DrinkService instance;

    public static DrinkService getInstance() {
        if (instance == null) {
            instance = new DrinkServiceImpl();
        }
        return instance;
    }

    @Override
    public void dispense(Cocktail cocktail, Consumer<Float> updateCallback, Runnable finishCallback) {
        var currentProgress = new float[]{0.0f};
        dispenseTimeline = getDispenseTimeline(updateCallback, getFinishCallback(finishCallback), currentProgress);
        dispenseTimeline.play();
    }

    private static Runnable getFinishCallback(Runnable finishCallback) {
        return finishCallback;
    }

    private Timeline getDispenseTimeline(Consumer<Float> updateCallback, Runnable finishCallback, float[] progress) {
        var timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.01), e -> {
                    progress[0] += 0.246f;
                    if (progress[0] > 100) {
                        progress[0] = 100;
                        finishCallback.run();
                        dispenseTimeline.stop();
                    }
                    updateCallback.accept(progress[0]);
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        return timeline;
    }

    @Override
    public boolean isReadyToDispense(Cocktail cocktail) {
        return true;
    }
}
