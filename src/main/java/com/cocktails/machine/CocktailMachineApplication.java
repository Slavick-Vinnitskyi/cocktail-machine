package com.cocktails.machine;

import com.cocktails.machine.repository.CocktailRepository;
import com.cocktails.machine.ui.NavigationManager;
import com.cocktails.machine.util.ResourceUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CocktailMachineApplication extends Application {

    public static final String TITLE = "Cocktail Machine";
    public static final String MAIN_STYLE = "material-design.css";
    public static final String STATUS_BAR_FXML = "status-bar.fxml";
    public static final int DEFAULT_SCREEN_WIDTH = 1280;
    public static final int DEFAULT_SCREEN_HEIGHT = 720;
    public static final int STATUS_BAR_HEIGHT = 48;
    public static final int MAIN_SCREEN_HEIGHT = DEFAULT_SCREEN_HEIGHT - STATUS_BAR_HEIGHT;

    @Override
    public void init() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> log.error("Uncaught exception: ", e));
    }

    @Override
    public void start(Stage stage) {
        try {
            // Load cocktails from JSON at startup
            CocktailRepository.getInstance().load();

            // Create root container with fixed status bar
            VBox root = new VBox();
            root.getStyleClass().add("root");
            root.setPrefSize(DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT);

            // Load and add fixed status bar
            Parent statusBar = ResourceUtils.loadView(STATUS_BAR_FXML);

            // Create container for screen navigation (below status bar)
            StackPane screenContainer = new StackPane();
            screenContainer.setPrefSize(DEFAULT_SCREEN_WIDTH, MAIN_SCREEN_HEIGHT);

            // Add status bar and screen container to root
            root.getChildren().addAll(statusBar, screenContainer);

            Scene scene = new Scene(root, DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT);

            // Apply custom Material Design CSS
            scene.getStylesheets().add(ResourceUtils.loadStyle(MAIN_STYLE));

            // Set scene fill to use CSS variable (will be overridden by CSS)
            scene.setFill(Color.TRANSPARENT);

            // Initialize navigation manager with screen container
            NavigationManager navManager = NavigationManager.getInstance();
            navManager.setContainer(screenContainer);

            // Load initial home screen (theme will be applied automatically via NavigationManager)
            navManager.navigateToHome();

            stage.setTitle(TITLE);
            stage.setWidth(DEFAULT_SCREEN_WIDTH);
            stage.setHeight(DEFAULT_SCREEN_HEIGHT);
            stage.setResizable(false);

            stage.setScene(scene);
            stage.show();

            // Register shutdown hook to save data on exit
            stage.setOnCloseRequest(e -> {
                CocktailRepository.getInstance().save();
                Platform.exit();
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize application", e);
        }
    }

    @Override
    public void stop() {
        CocktailRepository.getInstance().save();
    }
}
