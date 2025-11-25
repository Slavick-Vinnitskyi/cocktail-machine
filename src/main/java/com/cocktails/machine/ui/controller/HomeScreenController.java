package com.cocktails.machine.ui.controller;

import com.cocktails.machine.model.Cocktail;
import com.cocktails.machine.repository.CocktailRepository;
import com.cocktails.machine.ui.NavigationManager;
import com.cocktails.machine.util.ResourceUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
public class HomeScreenController implements Initializable {

    @FXML
    private HBox cocktailContainer;

    private CocktailFilter cocktailFilter = CocktailFilter.ALL;
    private static final String COCKTAIL_CARD_FXML = "cocktail-card.fxml";
    private static final CocktailRepository cocktailRepository = CocktailRepository.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setCocktailFilter(CocktailFilter.ALL);
    }

    private void setCocktailFilter(CocktailFilter filterType) {
        cocktailFilter = filterType;
        refreshCurrentFilter();
    }

    private void refreshCurrentFilter() {
        cocktailContainer.getChildren().clear();

        List<Cocktail> filteredCocktails = cocktailRepository.getCocktails(cocktailFilter);

        filteredCocktails.forEach(cocktail -> {
            // Load card FXML directly with controller
            var result = ResourceUtils.<CocktailCardController>loadViewWithController(COCKTAIL_CARD_FXML);
            CocktailCardController controller = result.getController();

            // Set up cocktail data
            controller.setCocktail(cocktail);

            // Set up callbacks
            controller.setOnSelect(() -> onCocktailSelect(cocktail));
            controller.setOnFavoriteChanged(this::refreshCurrentFilter);

            // Add the card view to container
            cocktailContainer.getChildren().add(result.getParent());
        });
    }

    @FXML
    private void onFavourites() {
        setCocktailFilter(CocktailFilter.FAVOURITES);
    }

    @FXML
    private void onAll() {
        setCocktailFilter(CocktailFilter.ALL);
    }

    @FXML
    private void onSettings() {
    }

    @FXML
    private void onExit() {
        cocktailRepository.save();
        Platform.exit();
    }

    private void onCocktailSelect(Cocktail cocktail) {
        log.info("Selected cocktail: {}", cocktail.getName());
        NavigationManager.getInstance().navigateToCocktailDetail(cocktail);
    }

    public enum CocktailFilter {
        ALL, FAVOURITES
    }
}

