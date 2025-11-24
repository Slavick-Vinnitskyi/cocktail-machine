package com.cocktails.machine.ui.widgets;

import com.cocktails.machine.model.Cocktail;
import com.cocktails.machine.util.ResourceUtils;
import javafx.animation.PauseTransition;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class CocktailCard extends VBox {
    public static final String COCKTAIL_CARD_FXML = "cocktail-card.fxml";
    private static final Duration LONG_PRESS_DURATION = Duration.millis(500); // 500ms for long-press

    private final CocktailCardController controller;
    private final Cocktail cocktail;
    private PauseTransition longPressTimer;
    private boolean isLongPress = false;
    private Runnable onSelect; // Callback for normal click
    private Runnable onFavoriteChanged; // Callback when favorite status changes (for UI refresh)

    public CocktailCard(Cocktail cocktail, Runnable onSelect) {
        this(cocktail, onSelect, null);
    }

    public CocktailCard(Cocktail cocktail, Runnable onSelect, Runnable onFavoriteChanged) {
        this.cocktail = cocktail;
        this.onSelect = onSelect;
        this.onFavoriteChanged = onFavoriteChanged;
        var viewWithController = ResourceUtils.<CocktailCardController>loadViewWithController(COCKTAIL_CARD_FXML);
        controller = viewWithController.getController();
        controller.setCocktail(cocktail);
        // Set up favorite changed callback on controller
        controller.setOnFavoriteChanged(() -> {
            if (onFavoriteChanged != null) {
                onFavoriteChanged.run();
            }
        });

        VBox loadedRoot = (VBox) viewWithController.getParent();
        // Copy all properties and children from loaded root to this
        getChildren().setAll(loadedRoot.getChildren());
        getStyleClass().setAll(loadedRoot.getStyleClass());
        setPrefSize(loadedRoot.getPrefWidth(), loadedRoot.getPrefHeight());
        setMaxSize(loadedRoot.getMaxWidth(), loadedRoot.getMaxHeight());
        setMinSize(loadedRoot.getMinWidth(), loadedRoot.getMinHeight());
        setSpacing(loadedRoot.getSpacing());
        setAlignment(loadedRoot.getAlignment());

        // Set up long-press detection
        setupLongPressDetection();

        // Set click handler on this (not on the loaded root)
        setOnMouseClicked(e -> {
            // Only handle click if it wasn't a long-press
            if (!isLongPress && onSelect != null) {
                e.consume();
                onSelect.run();
            }
            isLongPress = false; // Reset for next interaction
        });
    }

    public String getCocktailName() {
        return cocktail.getName();
    }

    public Cocktail getCocktail() {
        return cocktail;
    }

    private void setupLongPressDetection() {
        setOnMousePressed(e -> {
            isLongPress = false;
            // Start timer for long-press detection
            longPressTimer = new PauseTransition(LONG_PRESS_DURATION);
            longPressTimer.setOnFinished(event -> {
                isLongPress = true;
                System.out.println("Long-press detected on cocktail card: " + cocktail.getName());
                // Toggle favorite directly
                controller.toggleFavorite();
                // Notify parent to refresh UI if needed
                if (onFavoriteChanged != null) {
                    onFavoriteChanged.run();
                }
            });
            longPressTimer.play();
        });

        setOnMouseReleased(e -> {
            // Cancel long-press timer if mouse is released before threshold
            if (longPressTimer != null) {
                longPressTimer.stop();
            }
        });

        setOnMouseExited(e -> {
            // Cancel long-press timer if mouse leaves the card
            if (longPressTimer != null) {
                longPressTimer.stop();
            }
        });
    }
}

