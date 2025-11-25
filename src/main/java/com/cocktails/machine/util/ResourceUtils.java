package com.cocktails.machine.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.net.URL;

/**
 * Static utility class for loading views (FXML) and resources (JSON, CSS, etc.).
 */
public class ResourceUtils {

    private static final String VIEWS_FOLDER = "/com/cocktails/machine/view/";
    private static final String STYLES_FOLDER = "/com/cocktails/machine/styles/";
    private static final String IMAGES_FOLDER = "/com/cocktails/machine/images/";
    private static final String DEFAULT_COCKTAIL_IMAGE_PATH = "/com/cocktails/machine/images/cocktail.png";

    private ResourceUtils() {
    }

    /**
     * Loads an FXML view file and returns the loaded Parent.
     *
     * @param viewName home-screen.fxml
     * @return The loaded Parent node
     * @throws RuntimeException if the FXML file cannot be loaded
     */
    public static Parent loadView(String viewName) {
        var fxmlPath = VIEWS_FOLDER + viewName;
        try {
            var resource = ResourceUtils.class.getResource(fxmlPath);
            if (resource == null) {
                throw new RuntimeException("FXML resource not found: " + fxmlPath);
            }
            var loader = new FXMLLoader(resource);
            return loader.load();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load FXML view: " + fxmlPath, e);
        }
    }

    /**
     * Loads an FXML view file and returns both the loaded Parent and the controller.
     *
     * @param viewName Name of the view file (e.g., "home-screen.fxml")
     * @return A ViewResult containing both the Parent and the controller
     * @throws RuntimeException if the FXML file cannot be loaded
     */
    public static <T> ViewResult<T> loadViewWithController(String viewName) {
        var fxmlPath = VIEWS_FOLDER + viewName;
        try {
            var resource = ResourceUtils.class.getResource(fxmlPath);
            if (resource == null) {
                throw new RuntimeException("FXML resource not found: " + fxmlPath);
            }
            var loader = new FXMLLoader(resource);
            Parent view = loader.load();
            T controller = loader.getController();
            return new ViewResult<>(view, controller);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load FXML view: " + fxmlPath, e);
        }
    }

    /**
     * Loads a resource file (JSON, CSS, etc.) as an InputStream.
     *
     * @param resourcePath Path to the resource file (e.g., "/com/cocktails/.../cocktails.json")
     * @return InputStream for the resource, or null if not found
     */
    public static InputStream loadResource(String resourcePath) {
        return ResourceUtils.class.getResourceAsStream(resourcePath);
    }

    public static String loadStyle(String styleName) {
        URL resourceURL = ResourceUtils.getResourceURL(STYLES_FOLDER + styleName);
        if (resourceURL == null) {
            throw new RuntimeException("Style resource not found: " + STYLES_FOLDER + styleName);
        }
        return resourceURL.toExternalForm();
    }

    /**
     * Gets the URL for a resource file.
     *
     * @param resourcePath Path to the resource file
     * @return URL for the resource, or null if not found
     */
    public static URL getResourceURL(String resourcePath) {
        return ResourceUtils.class.getResource(resourcePath);
    }

    /**
     * Loads a cocktail image. Returns the appropriate image based on the cocktail's image property.
     * If the cocktail has a specific image, it will be loaded; otherwise, the default image is used.
     *
     * @param imageName The image name from cocktail.getImage() (can be null or empty)
     * @return Image object for the cocktail
     */
    public static Image loadCocktailImage(String imageName) {
        try {
            String imagePath;
            
            // Determine image path
            if (imageName != null && !imageName.isEmpty()) {
                // If image path doesn't start with /, assume it's in images folder
                if (imageName.startsWith("/")) {
                    imagePath = imageName;
                } else {
                    imagePath = IMAGES_FOLDER + imageName;
                }
            } else {
                imagePath = DEFAULT_COCKTAIL_IMAGE_PATH;
            }

            // Try to load the specified image
            URL imageURL = getResourceURL(imagePath);
            if (imageURL != null) {
                return new Image(imageURL.toExternalForm());
            } else {
                System.err.println("Warning: Cocktail image not found at " + imagePath + ", using default");
                // Fallback to default image
                URL defaultURL = getResourceURL(DEFAULT_COCKTAIL_IMAGE_PATH);
                if (defaultURL != null) {
                    return new Image(defaultURL.toExternalForm());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load cocktail image: " + e.getMessage());
        }
        
        // Return null if all attempts fail
        return null;
    }

    /**
     * Result class for loading views with controllers.
     */
    public static class ViewResult<T> {
        private final Parent parent;
        private final T controller;

        public ViewResult(Parent parent, T controller) {
            this.parent = parent;
            this.controller = controller;
        }

        public Parent getParent() {
            return parent;
        }

        public T getController() {
            return controller;
        }
    }
}
