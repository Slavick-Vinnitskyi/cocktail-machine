package com.cocktails.machine.util;

import javafx.scene.input.MouseEvent;

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

public class UIEventUtils {

    public static void consumeAndRun(MouseEvent e, Runnable action) {
        e.consume();
        action.run();
    }

}
