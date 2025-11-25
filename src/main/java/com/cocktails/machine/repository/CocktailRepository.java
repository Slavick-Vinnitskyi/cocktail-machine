package com.cocktails.machine.repository;

import com.cocktails.machine.model.Cocktail;
import com.cocktails.machine.model.CocktailData;
import com.cocktails.machine.ui.controller.HomeScreenController.CocktailFilter;
import com.cocktails.machine.util.ResourceUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Singleton repository for managing cocktail data.
 * Loads cocktails from JSON at startup and saves them on exit.
 */
public class CocktailRepository {
    private static CocktailRepository instance;
    private List<Cocktail> cocktails;
    private final Gson gson;
    private final String jsonResourcePath = "/com/cocktails/machine/cocktails.json";
    private final Path jsonFilePath; // Save to user home directory for better cross-platform support

    private CocktailRepository() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.cocktails = new ArrayList<>();

        String userHome = System.getProperty("user.home");
        if (userHome != null && !userHome.isEmpty()) {
            this.jsonFilePath = Paths.get(userHome, ".cocktail-machine", "cocktails.json");
        } else {
            this.jsonFilePath = Paths.get("cocktails.json");
        }
    }

    public static CocktailRepository getInstance() {
        if (instance == null) {
            instance = new CocktailRepository();
        }
        return instance;
    }

    /**
     * Loads cocktails from JSON file.
     * First tries to load from saved file (working directory), then falls back to resource file.
     * Should be called at application startup.
     */
    public void load() {
        try {
            // First, try to load from saved file (preserves user preferences like favorites)
            if (Files.exists(jsonFilePath)) {
                try {
                    String json = Files.readString(jsonFilePath);
                    var data = gson.fromJson(json, CocktailData.class);
                    if (data != null && data.getCocktails() != null) {
                        this.cocktails = new ArrayList<>(data.getCocktails());
                        System.out.println("Loaded " + this.cocktails.size() + " cocktails from saved file");
                        return;
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load from saved file, falling back to resource: " + e.getMessage());
                }
            }

            // Fall back to resource file if saved file doesn't exist or failed to load
            InputStream is = ResourceUtils.loadResource(jsonResourcePath);
            if (is != null) {
                var data = gson.fromJson(
                        new InputStreamReader(is, StandardCharsets.UTF_8),
                        CocktailData.class
                );
                if (data != null && data.getCocktails() != null) {
                    this.cocktails = new ArrayList<>(data.getCocktails());
                    System.out.println("Loaded " + this.cocktails.size() + " cocktails from resource file");
                } else {
                    this.cocktails = new ArrayList<>();
                }
            } else {
                System.err.println("Warning: Could not find cocktails.json resource");
                this.cocktails = new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("Failed to load cocktails: " + e.getMessage());
            e.printStackTrace();
            this.cocktails = new ArrayList<>();
        }
    }

    /**
     * Saves cocktails to JSON file.
     * Should be called on application exit.
     */
    public void save() {
        try {
            CocktailData data = new CocktailData();
            data.setCocktails(new ArrayList<>(cocktails));

            String json = gson.toJson(data);

            // Ensure parent directory exists (important for Raspberry Pi and cross-platform)
            Path parentDir = jsonFilePath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            Files.writeString(jsonFilePath, json, StandardCharsets.UTF_8);

            System.out.println("Cocktails saved to " + jsonFilePath.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to save cocktails: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Cocktail> getCocktails(CocktailFilter filterType) {
        var predicate = getPredicate(filterType);
        return cocktails.stream().filter(predicate).toList();
    }

    private static Predicate<Cocktail> getPredicate(CocktailFilter filterType) {
        return filterType == CocktailFilter.FAVOURITES ? Cocktail::isFavorite : cocktail -> true;
    }

    /**
     * Finds a cocktail by name.
     */
    public Optional<Cocktail> findByName(String name) {
        return cocktails.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst();
    }

    /**
     * Toggles the favorite status of a cocktail.
     */
    public void toggleFavorite(Cocktail cocktail) {
        if (cocktail != null) {
            cocktail.toggleFavorite();
        }
    }

    /**
     * Sets the favorite status of a cocktail.
     */
    public void setFavorite(Cocktail cocktail, boolean isFavorite) {
        if (cocktail != null) {
            cocktail.setFavorite(isFavorite);
        }
    }
}

