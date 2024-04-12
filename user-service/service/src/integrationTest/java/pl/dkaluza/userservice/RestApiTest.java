package pl.dkaluza.userservice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class RestApiTest {
    @Test
    void test() {
        Assertions.assertThat("Value")
            .isNotNull();
    }
}
