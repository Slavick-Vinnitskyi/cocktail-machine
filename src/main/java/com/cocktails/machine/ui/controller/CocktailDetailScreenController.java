package com.cocktails.machine.ui.controller;

import com.cocktails.machine.model.Cocktail;
import com.cocktails.machine.repository.CocktailRepository;
import com.cocktails.machine.ui.NavigationManager;
import com.cocktails.machine.util.ResourceUtils;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class CocktailDetailScreenController implements Initializable {
    // Sidebar
    @FXML private MFXButton backButton;
    @FXML private MFXButton favoriteButton;
    @FXML private MFXFontIcon favoriteIcon;
    @FXML private MFXButton reloadButton;
    @FXML private MFXButton exitButton;

    // Main Content
    @FXML private Label cocktailName;
    @FXML private ImageView cocktailImage;
    @FXML private MFXButton playButton;
    @FXML private StackPane playButtonContainer;
    @FXML private StackPane progressContainer;
    @FXML private Circle progressCircle;
    @FXML private Text progressLabel;
    @FXML private VBox ingredientsContainer;
    @FXML private Text progressText;

    private static final String DEFAULT_COCKTAIL_IMAGE_PATH = "/com/cocktails/machine/images/cocktail.png";
    private static final String IMAGES_FOLDER = "/com/cocktails/machine/images/";

    private Cocktail cocktail;
    private final CocktailRepository cocktailRepository = CocktailRepository.getInstance();
    private Timeline progressTimeline;
    private double currentProgress = 0.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up button handlers
        if (backButton != null) {
            backButton.setOnAction(e -> onBack());
        }
        if (favoriteButton != null) {
            favoriteButton.setOnAction(e -> toggleFavorite());
        }
        // Set up favorite icon click handler (clicking icon directly also toggles)
        if (favoriteIcon != null) {
            favoriteIcon.setOnMouseClicked(e -> {
                e.consume(); // Prevent event from propagating
                toggleFavorite();
            });
        }
        if (reloadButton != null) {
            reloadButton.setOnAction(e -> onReload());
        }
        if (exitButton != null) {
            exitButton.setOnAction(e -> onExit());
        }
        if (playButton != null) {
            playButton.setOnAction(e -> onPlay());
        }

        // Ensure progress label is perfectly centered to match play button alignment
        if (progressLabel != null && progressContainer != null) {
            StackPane.setAlignment(progressLabel, Pos.CENTER);
            // Center the text node by adjusting its bounds
            progressLabel.setTextOrigin(javafx.geometry.VPos.CENTER);
            progressLabel.setBoundsType(javafx.scene.text.TextBoundsType.VISUAL);
        }

    }

    public void setCocktail(Cocktail cocktail) {
        this.cocktail = cocktail;
        if (cocktail != null) {
            updateUI();
        }
    }

    private void updateUI() {
        if (cocktail == null) return;

        // Update name
        if (cocktailName != null) {
            cocktailName.setText(cocktail.getName());
        }

        // Update cocktail image
        loadCocktailImage();

        // Update ingredients text
        updateIngredientsText();

        // Update favorite icon
        updateFavoriteIcon();
    }

    private void loadCocktailImage() {
        if (cocktailImage != null && cocktail != null) {
            try {
                String imagePath;
                // Use cocktail-specific image if available, otherwise use default
                if (cocktail.getImage() != null && !cocktail.getImage().isEmpty()) {
                    // If image path doesn't start with /, assume it's in images folder
                    if (cocktail.getImage().startsWith("/")) {
                        imagePath = cocktail.getImage();
                    } else {
                        imagePath = IMAGES_FOLDER + cocktail.getImage();
                    }
                } else {
                    imagePath = DEFAULT_COCKTAIL_IMAGE_PATH;
                }

                var imageURL = ResourceUtils.getResourceURL(imagePath);
                if (imageURL != null) {
                    Image image = new Image(imageURL.toExternalForm());
                    cocktailImage.setImage(image);
                } else {
                    System.err.println("Warning: Cocktail image not found at " + imagePath + ", using default");
                    // Fallback to default image
                    var defaultURL = ResourceUtils.getResourceURL(DEFAULT_COCKTAIL_IMAGE_PATH);
                    if (defaultURL != null) {
                        cocktailImage.setImage(new Image(defaultURL.toExternalForm()));
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to load cocktail image: " + e.getMessage());
            }
        }
    }

    private void updateIngredientsText() {
        if (ingredientsContainer == null || cocktail == null) return;

        ingredientsContainer.getChildren().clear();

        if (cocktail.getIngredients() != null && !cocktail.getIngredients().isEmpty()) {
            // Create a Text node for each ingredient with bullet point (left-aligned)
            for (String ingredient : cocktail.getIngredients()) {
                Text text = new Text("• " + ingredient);
                text.getStyleClass().add("ingredient-text");
                text.setTextAlignment(javafx.scene.text.TextAlignment.LEFT);
                ingredientsContainer.getChildren().add(text);
            }
        } else {
            // Fallback to description if no ingredients
            Text text = new Text(cocktail.getDescription());
            text.getStyleClass().add("ingredient-text");
            text.setTextAlignment(javafx.scene.text.TextAlignment.LEFT);
            ingredientsContainer.getChildren().add(text);
        }
    }

    private void toggleFavorite() {
        if (cocktail != null) {
            cocktailRepository.toggleFavorite(cocktail);
            updateFavoriteIcon();
        }
    }

    private void updateFavoriteIcon() {
        if (favoriteIcon == null || cocktail == null) return;

        favoriteIcon.getStyleClass().removeAll("favorite-icon-filled", "favorite-icon-outline");
        if (cocktail.isFavorite()) {
            // Gold color when favorite - use filled star
            favoriteIcon.setDescription("fas-star");
            favoriteIcon.getStyleClass().add("favorite-icon-filled");
        } else {
            // White outline star when not favorite (matches sidebar)
            favoriteIcon.setDescription("fas-star");
            favoriteIcon.getStyleClass().add("favorite-icon-outline");
        }
    }

    private void onBack() {
        NavigationManager.getInstance().navigateToHome();
    }

    private void onReload() {
        // Reset progress if needed
        stopProgress();
        currentProgress = 0.0;

        // Show play button and ingredients, hide progress
        if (playButtonContainer != null) {
            playButtonContainer.setVisible(true);
            playButtonContainer.setManaged(true);
        }
        if (ingredientsContainer != null) {
            ingredientsContainer.setVisible(true);
            ingredientsContainer.setManaged(true);
            updateIngredientsText(); // Restore ingredients text
        }
        if (progressContainer != null) {
            progressContainer.setVisible(false);
            progressContainer.setManaged(false);
        }
        if (progressText != null) {
            progressText.setVisible(false);
            progressText.setManaged(false);
        }
    }

    private void onExit() {
        // Save cocktails before exiting
        cocktailRepository.save();
        javafx.application.Platform.exit();
    }

    private void onPlay() {
        // Hide play button and ingredients, show progress
        if (playButtonContainer != null) {
            playButtonContainer.setVisible(false);
            playButtonContainer.setManaged(false);
        }
        if (ingredientsContainer != null) {
            ingredientsContainer.setVisible(false);
        }
        if (progressText != null) {
            progressText.setVisible(true);
            progressText.setManaged(true);
        }
        if (progressContainer != null) {
            progressContainer.setVisible(true);
            progressContainer.setManaged(true);
        }

        // Start dispensing process
        startProgress();
    }

    private void startProgress() {
        if (progressTimeline != null) {
            progressTimeline.stop();
        }

        currentProgress = 0.0;
        progressTimeline = new Timeline(
            new KeyFrame(Duration.seconds(0.1), e -> {
                currentProgress += 2.46; // Increment by 0.46% per 100ms
                if (currentProgress > 100) {
                    currentProgress = 100;
                    stopProgress();
                }
                updateProgress(currentProgress);
                updateText(currentProgress);
            })
        );
        progressTimeline.setCycleCount(Timeline.INDEFINITE);
        progressTimeline.play();
    }

    private void updateText(double currentProgress) {

    }

    private void stopProgress() {
        if (progressTimeline != null) {
            progressTimeline.stop();
        }
    }

    private void updateProgress(double percentage) {
        if (progressLabel != null) {
            progressLabel.setText(String.format("%.0f%%", percentage));
        }

        // Update circle progress (circumference = 2 * PI * radius = 2 * PI * 100 ≈ 628.32)
        if (progressCircle != null) {
            double circumference = 2 * Math.PI * 100;
            double progress = percentage / 100.0;

            progressCircle.getStrokeDashArray().setAll(
                circumference * progress,
                circumference
            );
            progressCircle.setRotate(-90); // Start from top
        }
    }

    public void startDispensing() {
        // This method can be called externally if needed
        onPlay();
    }
}
