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

    /**
     * Returns true when there is no filtering defined within given RecipeFilters object, hence there is
     * no point of applying filtering logic using it.
     *
     * @return true when filters object is empty.
     */
    public boolean isEmpty() {
        return name == null && cookId == null;
    }
}
