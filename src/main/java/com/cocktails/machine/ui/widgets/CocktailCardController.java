package com.cocktails.machine.ui.widgets;

import com.cocktails.machine.model.Cocktail;
import com.cocktails.machine.util.ResourceUtils;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CocktailCardController {
    @FXML private Label nameLabel;
    @FXML private Label descriptionLabel;
    @FXML private ImageView cocktailImage;
    @FXML private MFXFontIcon favoriteIcon;

    private static final String DEFAULT_COCKTAIL_IMAGE_PATH = "/com/cocktails/machine/images/cocktail.png";
    private static final String IMAGES_FOLDER = "/com/cocktails/machine/images/";

    private Cocktail cocktail;
    private Runnable onFavoriteChanged;

    @FXML
    private void initialize() {
        setupFavoriteIconHandler();
    }
    
    public void setOnFavoriteChanged(Runnable onFavoriteChanged) {
        this.onFavoriteChanged = onFavoriteChanged;
    }
    
    private void setupFavoriteIconHandler() {
        if (favoriteIcon != null) {
            favoriteIcon.setOnMouseClicked(e -> {
                e.consume(); // Prevent event from propagating to card
                toggleFavorite();
            });
        }
    }
    
    public void toggleFavorite() {
        if (cocktail != null) {
            cocktail.toggleFavorite();
            updateFavoriteIndicator(cocktail.isFavorite());
            // Notify parent that favorite status changed
            if (onFavoriteChanged != null) {
                onFavoriteChanged.run();
            }
        }
    }

    public void setCocktail(Cocktail cocktail) {
        this.cocktail = cocktail;
        if (cocktail != null) {
            nameLabel.setText(cocktail.getName());
            descriptionLabel.setText(cocktail.getDescription());
            // Load cocktail image (specific image if available, otherwise default)
            loadCocktailImage();
            // Initialize favorite indicator on startup
            updateFavoriteIndicator(cocktail.isFavorite());
        }
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

    public Cocktail getCocktail() {
        return cocktail;
    }

    public void updateFavoriteIndicator(boolean isFavorite) {
        if (favoriteIcon != null) {
            // Always show the icon - empty star when not favorite, filled star when favorite
            favoriteIcon.setVisible(true);
            favoriteIcon.setManaged(true);
            favoriteIcon.getStyleClass().removeAll("favorite-icon-filled", "favorite-icon-outline");
            if (isFavorite) {
                favoriteIcon.setDescription("fas-star");
                favoriteIcon.getStyleClass().add("favorite-icon-filled");
            } else {
                favoriteIcon.setDescription("fas-star");
                favoriteIcon.getStyleClass().add("favorite-icon-outline");
            }
        }
    }
}

