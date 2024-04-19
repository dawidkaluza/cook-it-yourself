package pl.dkaluza.userservice;

import org.junit.jupiter.api.Test;

/*
oauth2 auth code tests:
- given unauthorized user, when user requests for auth code, then redirect user to sign in page (RA)
- given authorized user, when user requests for auth code with invalid params, then redirect to web app with error code (RA)
- given authorized user, when user requests for auth code, then redirect to web app with auth code (RA)

sign in tests:
given user on sign in page, when user sends invalid sign in request, show errors
given user on sign in page, when user sends valid sign in request, return success and redirect to last visited page

sign in tests (RA):
given user on sign in page with CSRF token in a cookie, when user sends invalid sign in request, return errors
given user on sign in page with CSRF token in a cookie, when user sends valid sign in request, return success and redirect to last visiteg page

sign up tests:
- given user on sign up page, when user sends invalid sign up request, show errors
- given user on sign up page, when user sends valid sign up request, return success and redirect to sign in page

sign up tests (RA):
given user on sign up page with CSRF token in a cookie, when user sends invalid sign up request, return errors
given user on sign up page with CSRF token in a cookie, when user sends valid sign up request, return success and redirect to last visiteg page

requirements:
- go thru redirections
- submit forms
-

solutions:
rest assured:
form authentication - submit authentication via form before performing a request (https://github.com/rest-assured/rest-assured/wiki/Usage#form-authentication)
csrf token - send get request, get csrf token, send POST request with CSRF token (https://github.com/rest-assured/rest-assured/wiki/Usage#csrf-form-token P.S. does not support csrf token being stored in cookies, but same can be accomplished via Filters: https://github.com/rest-assured/rest-assured/wiki/Usage#filters)
issue - CORS policy, can be overriden

Summary:
Rest Assured can try to simulate opening a page and sending a request, but it does not really do that so what is being tested is only service endpoints, not web UI - which is not bad since UI automated tests are not that straightforward, hence it might be better to have UI tests in e2e tests

 */
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


