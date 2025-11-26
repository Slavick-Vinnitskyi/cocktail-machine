package com.cocktails.machine.ui;

import com.cocktails.machine.model.Cocktail;
import com.cocktails.machine.ui.controller.CocktailDetailScreenController;
import com.cocktails.machine.util.ResourceUtils;
import javafx.animation.TranslateTransition;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class NavigationManager {

    private static NavigationManager instance;

    public static final int SCREEN_DEFAULT_WIDTH = 1280;
    public static final double TRANSITION_DURATION = 0.3;
    public static final String HOME_SCREEN_FXML = "home-screen.fxml";
    public static final String COCKTAIL_DETAIL_SCREEN_FXML = "cocktail-detail-screen.fxml";

    private StackPane container;
    private Parent currentScreen;

    private NavigationManager() {
    }

    public static NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }

    public void setContainer(StackPane container) {
        this.container = container;
    }

    public void navigateToHome() {
        Parent homeScreen = ResourceUtils.loadView(HOME_SCREEN_FXML);
        navigateToScreen(homeScreen, Direction.RIGHT);
    }

    public void navigateToCocktailDetail(Cocktail cocktail) {
        ResourceUtils.ViewResult<CocktailDetailScreenController> result = ResourceUtils.loadViewWithController(COCKTAIL_DETAIL_SCREEN_FXML);
        result.controller().setCocktail(cocktail);
        navigateToScreen(result.parent(), Direction.LEFT);
    }

    private void navigateToScreen(Parent screen, Direction direction) {
        if (container == null) {
            throw new IllegalStateException("NavigationManager container not set");
        }

        Parent previousScreen = currentScreen;
        currentScreen = screen;

        if (previousScreen == null) {
            container.getChildren().add(screen);
            return;
        }

        var containerWidth = container.getWidth();
        var screenWidth = containerWidth == 0 ? SCREEN_DEFAULT_WIDTH : containerWidth;

        if (direction == Direction.LEFT) {
            screen.setTranslateX(screenWidth);
        } else {
            screen.setTranslateX(-screenWidth);
        }

        container.getChildren().add(screen);

        // Animate transition
        TranslateTransition currentOut = new TranslateTransition(Duration.seconds(TRANSITION_DURATION), previousScreen);
        TranslateTransition newIn = new TranslateTransition(Duration.seconds(TRANSITION_DURATION), screen);

        if (direction == Direction.LEFT) {
            currentOut.setToX(-screenWidth);
            newIn.setToX(0);
        } else {
            currentOut.setToX(screenWidth);
            newIn.setToX(0);
        }

        newIn.setOnFinished(e -> {
            container.getChildren().remove(previousScreen);
            previousScreen.setTranslateX(0);
        });

        currentOut.play();
        newIn.play();
    }

    public enum Direction {
        LEFT, RIGHT
    }
}

