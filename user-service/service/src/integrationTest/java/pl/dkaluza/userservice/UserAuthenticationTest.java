package pl.dkaluza.userservice;

import org.junit.jupiter.api.Test;

class UserAuthenticationTest {
    @Test
    void signIn_invalidCsrfToken_returnForbidden() {

    }

    @Test
    void signIn_invalidEmailOrPassword_returnUnauthorized() {

    }

    @Test
    void signIn_unauthenticated_authenticate() {

    }

    @Test
    void signIn_redirectedToSignIn_authenticateAndRedirectBack() {

    }

    @Test
    void signIn_alreadyAuthenticated_reauthenticate() {

    }

    @Test
    void signOut_invalidCsrfToken_returnForbidden() {

    }

    @Test
    void signOut_unauthenticated_accept() {

    }

    @Test
    void signOut_authenticated_invalidateAuthentication() {

    }
}


