package com.cocktails.machine.service;

import com.cocktails.machine.model.Cocktail;

import java.util.function.Consumer;


public interface DispenseService {

    boolean isReadyToDispense(Cocktail cocktail);

    void dispense(Cocktail cocktail, Consumer<Float> updateCallback, Runnable finishCallback);
}
