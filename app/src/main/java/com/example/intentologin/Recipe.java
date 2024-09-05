package com.example.intentologin;

public class Recipe {
    private String title;
    private String ingredients;
    private String steps;
    private String imagePath;
    private String author;

    public Recipe(String title, String ingredients, String steps, String imagePath, String author) {
        this.title = title;
        this.ingredients = ingredients;
        this.steps = steps;
        this.imagePath = imagePath;
        this.author = author;
    }



    // Getters y setters para los atributos de la receta


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
