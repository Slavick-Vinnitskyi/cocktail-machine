package com.cocktails.machine.ui.controller;

import com.cocktails.machine.model.Cocktail;
import com.cocktails.machine.repository.CocktailRepository;
import com.cocktails.machine.ui.widgets.CocktailCard;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HomeScreenController implements Initializable {
    @FXML private MFXButton btnFavourites;
    @FXML private MFXButton btnAll;
    @FXML private MFXButton btnSettings;
    @FXML private MFXButton btnExit;
    @FXML private HBox cocktailContainer;

    private final CocktailRepository cocktailRepository = CocktailRepository.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up button handlers
        btnFavourites.setOnAction(e -> onFavourites());
        btnAll.setOnAction(e -> onAll());
        btnSettings.setOnAction(e -> onSettings());
        btnExit.setOnAction(e -> onExit());

        // Load initial cocktails
        setCocktailFilter("All");
    }

    private String currentFilter = "All";

    private void setCocktailFilter(String filterType) {
        currentFilter = filterType;
        refreshCurrentFilter();
    }

    private void refreshCurrentFilter() {
        cocktailContainer.getChildren().clear();

        List<Cocktail> filteredCocktails;
        if ("Favourites".equals(currentFilter)) {
            filteredCocktails = cocktailRepository.getFavoriteCocktails();
        } else {
            filteredCocktails = cocktailRepository.getAllCocktails();
        }

        if (filteredCocktails.isEmpty()) {
            // Container will be empty but maintain layout
            return;
        }

        for (Cocktail cocktail : filteredCocktails) {
            // Create card with callbacks
            // onSelect: navigate to detail screen
            // onFavoriteChanged: refresh UI when favorite status changes
            CocktailCard card = new CocktailCard(
                    cocktail,
                    () -> onCocktailSelect(cocktail),
                    () -> {
                        // Refresh current view (e.g., if filtering by favorites)
                        refreshCurrentFilter();
                    }
            );
            cocktailContainer.getChildren().add(card);
        }
    }

    @FXML
    private void onFavourites() {
        setCocktailFilter("Favourites");
    }

    @FXML
    private void onAll() {
        setCocktailFilter("All");
    }

    @FXML
    private void onSettings() {
        // Settings button - no action needed for now
    }

    @FXML
    private void onExit() {
        // Save cocktails before exiting
        cocktailRepository.save();
        javafx.application.Platform.exit();
    }

    private void onCocktailSelect(Cocktail cocktail) {
        System.out.println("Selected: " + cocktail.getName());
        com.cocktails.machine.ui.NavigationManager.getInstance().navigateToCocktailDetail(cocktail);
    }
}

