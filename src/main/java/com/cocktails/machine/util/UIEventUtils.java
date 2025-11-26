package com.cocktails.machine.util;

import javafx.scene.input.MouseEvent;

public class UIEventUtils {

    public static void consumeAndRun(MouseEvent e, Runnable action) {
        e.consume();
        action.run();
    }

}
