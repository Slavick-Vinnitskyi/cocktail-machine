package com.cocktails.machine.util;

import javafx.scene.Node;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class UiDisplayUtils {

    public static void hide(Node progressContainer) {
        manageVisibility(progressContainer, false);
    }

    public static void display(Node progressContainer) {
        manageVisibility(progressContainer, true);
    }

    public static void manageVisibility(Node node, boolean isVisible) {
        if (node == null) {
            log.error("Node is null, cannot manage visibility to: {}", isVisible);
            throw new IllegalStateException("Node is null");
        }
        node.setVisible(isVisible); // Hides the node visually, but it still occupies space in layout
        node.setManaged(isVisible); // Removes it from layout entirely (no space reserved)
    }
}
