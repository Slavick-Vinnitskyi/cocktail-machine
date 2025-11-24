package com.cocktails.machine.model;

import java.util.List;

public class CocktailData {
    private List<Cocktail> cocktails;

    public CocktailData() {
    }

    public List<Cocktail> getCocktails() {
        return cocktails;
    }

    public void setCocktails(List<Cocktail> cocktails) {
        this.cocktails = cocktails;
    }
}

