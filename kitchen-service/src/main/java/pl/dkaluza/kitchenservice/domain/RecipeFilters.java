package pl.dkaluza.kitchenservice.domain;

public class RecipeFilters {
    private final String name;
    private final CookId cookId;

    private RecipeFilters(String name, CookId cookId) {
        this.name = name;
        this.cookId = cookId;
    }

    public static RecipeFilters of(String name, CookId cookId) {
        return new RecipeFilters(name, cookId);
    }

    public String getName() {
        return name;
    }

    public CookId getCookId() {
        return cookId;
    }
}
