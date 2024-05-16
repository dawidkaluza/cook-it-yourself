package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.DefaultFactory;
import pl.dkaluza.domaincore.ValidationExecutor;

import static pl.dkaluza.domaincore.Validator.validator;

public class PageRequest {
    private final int pageNumber;
    private final int pageSize;

    private PageRequest(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public static PageRequest of(int pageNumber, int pageSize) {
        return new PageRequest(pageNumber, pageSize);
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
