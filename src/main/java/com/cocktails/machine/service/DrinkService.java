package com.cocktails.machine.service;

import com.cocktails.machine.model.Cocktail;

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

public interface DrinkService {

    void dispense(Cocktail cocktail, Consumer<Float> updateCallback, Runnable finishCallback);

    boolean isReadyToDispense(Cocktail cocktail);
}
