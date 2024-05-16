package pl.dkaluza.domaincore;

import java.util.List;

import static pl.dkaluza.domaincore.Validator.validator;

public class Page<T> {
    private final List<T> items;
    private final int pageNumber;
    private final int totalPages;

    private Page(List<T> items, int pageNumber, int totalPages) {
        this.items = items;
        this.pageNumber = pageNumber;
        this.totalPages = totalPages;
    }

    public static <T> Factory<Page<T>> of(List<T> items) {
        return new PageFactory<>(items, 1, 1);
    }

    public static <T> Factory<Page<T>> of(List<T> items, int pageNumber, int totalPages) {
        return new PageFactory<>(items, pageNumber, totalPages);
    }

    public List<T> getItems() {
        return items;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getTotalPages() {
        return totalPages;
    }

    private static class PageFactory<T> extends DefaultFactory<Page<T>> {
        private PageFactory(List<T> items, int pageNumber, int totalPages) {
            super(
                ValidationExecutor.builder()
                    .withValidator(validator(items != null, "items", "Items must not be null"))
                    .withValidator(validator(pageNumber > 0, "pageNumber", "Page number must be a positive number"))
                    .withValidator(validator(totalPages > 0, "totalPages", "Total pages must be a positive number"))
                    .build(),
                () -> new Page<>(items, pageNumber, totalPages)
            );
        }
    }
}
