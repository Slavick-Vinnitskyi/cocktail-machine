package com.cocktails.machine.ui.controller;

import com.cocktails.machine.model.Cocktail;
import com.cocktails.machine.repository.CocktailRepository;
import com.cocktails.machine.service.DispenseService;
import com.cocktails.machine.service.impl.DispenseServiceImpl;
import com.cocktails.machine.ui.NavigationManager;
import com.cocktails.machine.util.ResourceUtils;
import com.cocktails.machine.util.UiDisplayUtils;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class CocktailDetailScreenController implements Initializable {

    public static final String FAVORITE_ICON_FILLED = "favorite-icon-filled";
    public static final String FAVORITE_ICON_OUTLINE = "favorite-icon-outline";
    public static final String FAS_STAR = "fas-star";
    public static final String INGREDIENT_TEXT = "ingredient-text";

    @FXML
    private MFXFontIcon favoriteIcon;
    @FXML
    private Label cocktailName;
    @FXML
    private ImageView cocktailImage;
    @FXML
    private StackPane playButtonContainer;
    @FXML
    private StackPane progressContainer;
    @FXML
    private Circle progressCircle;
    @FXML
    private Text progressLabel;
    @FXML
    private VBox ingredientsContainer;
    @FXML
    private Text progressText;

    private Cocktail cocktail;

    private final AtomicInteger logCount = new AtomicInteger(0);
    private final DispenseService drinkService = DispenseServiceImpl.getInstance();
    private final CocktailRepository cocktailRepository = CocktailRepository.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }


    public void setCocktail(Cocktail cocktail) {
        this.cocktail = cocktail;
        if (cocktail != null) {
            updateUI();
        }
    }

    private void updateUI() {
        if (cocktail == null) {
            return;
        }

        cocktailName.setText(cocktail.getName());

        var image = ResourceUtils.loadCocktailImage(cocktail.getImage());
        if (image != null) {
            cocktailImage.setImage(image);
        }

        updateIngredientsText();
        updateFavoriteIcon();
    }

    private void updateIngredientsText() {
        if (ingredientsContainer == null || cocktail == null) {
            return;
        }

        ingredientsContainer.getChildren().clear();

        if (cocktail.getIngredients() != null && !cocktail.getIngredients().isEmpty()) {
            for (String ingredient : cocktail.getIngredients()) {
                var text = new Text("• " + ingredient);
                text.getStyleClass().add(INGREDIENT_TEXT);
                text.setTextAlignment(TextAlignment.LEFT);
                ingredientsContainer.getChildren().add(text);
            }
        } else {
            // Fallback to description if no ingredients
            var text = new Text(cocktail.getDescription());
            text.getStyleClass().add(INGREDIENT_TEXT);
            text.setTextAlignment(TextAlignment.LEFT);
            ingredientsContainer.getChildren().add(text);
        }
    }

    @FXML
    private void toggleFavorite() {
        if (cocktail != null) {
            cocktailRepository.toggleFavorite(cocktail);
            log.info("Toggled favorite for {}: {}", cocktail.getName(), cocktail.isFavorite());
            updateFavoriteIcon();
        }
    }

    private void updateFavoriteIcon() {
        if (favoriteIcon == null || cocktail == null) {
            return;
        }

        log.info("Updating favorite icon for {}: isFavorite: {}", cocktail.getName(), cocktail.isFavorite());
        favoriteIcon.getStyleClass().removeAll(FAVORITE_ICON_FILLED, FAVORITE_ICON_OUTLINE);
        favoriteIcon.setDescription(FAS_STAR);
        var style = cocktail.isFavorite() ? FAVORITE_ICON_FILLED : FAVORITE_ICON_OUTLINE;
        favoriteIcon.getStyleClass().add(style);
    }

    @FXML
    private void onBack() {
        NavigationManager.getInstance().navigateToHome();
    }

    @FXML
    private void onReload() {
        stopProgress();
        UiDisplayUtils.hide(null);
        UiDisplayUtils.hide(progressText);

        UiDisplayUtils.display(playButtonContainer);
        UiDisplayUtils.display(ingredientsContainer);
        updateIngredientsText();
    }

    @FXML
    private void onExit() {
        cocktailRepository.save();
        Platform.exit();
    }

    @FXML
    private void onPlay() {
        UiDisplayUtils.hide(playButtonContainer);

        UiDisplayUtils.display(progressText);
        UiDisplayUtils.display(progressContainer);
        startProgress();
    }

    private void startProgress() {
        if (!drinkService.isReadyToDispense(cocktail)) {
            log.error("Not ready to dispense: {}", cocktail.getName());
            progressText.setText("Not ready to dispense");
            return;
        }
        drinkService.dispense(cocktail, this::updateProgress, this::stopProgress);
        progressText.setText("Drink is being prepared...");
    }

    private void stopProgress() {
        logCount.set(0);
        progressText.setText("Drink is ready!");
    }

    private void updateProgress(double percentage) {
        progressLabel.setText(String.format("%.0f%%", percentage));
        double circumference = 2 * Math.PI * 100;
        double progress = percentage / 100.0;

        progressCircle.getStrokeDashArray().setAll(circumference * progress, circumference);
        progressCircle.setRotate(-90);

        logCount.incrementAndGet();
        if (logCount.get() % 10 == 0 || percentage == 100.0f) {
            log.info("Updated progress to: %.1f%%".formatted(percentage));
        }
    }
}
