package pl.dkaluza.domaincore;

import static pl.dkaluza.domaincore.Validator.validator;

public class PageRequest {
    private final int pageNumber;
    private final int pageSize;

    private PageRequest(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public static Factory<PageRequest> of(int pageNumber, int pageSize) {
        return new PageRequestFactory(pageNumber, pageSize);
    }

    private static class PageRequestFactory extends DefaultFactory<PageRequest> {
        private PageRequestFactory(int pageNumber, int pageSize) {
            super(
                ValidationExecutor.builder()
                    .withValidator(validator(pageNumber > 0, "pageNumber", "Page number must be a positive number"))
                    .withValidator(validator(pageSize > 0, "pageSize", "Page size must be a positive number"))
                    .build(),
                () -> new PageRequest(pageNumber, pageSize)
            );
        }
    }
}