package pl.dkaluza.userservice;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.util.UriUtils;

import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static io.restassured.RestAssured.given;

public final class RestAssuredUtils {
    private RestAssuredUtils() {
    }

    public static Response signUp(String email, String password, String name) {
        return signUp(email, password, name, true);
    }

    public static Response signUp(String email, String password, String name, boolean validate) {
        var response = given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(Map.of(
                "email", email,
                "password", password,
                "name", name
            ))
            .post("/user/sign-up");

        if (validate && response.statusCode() != 201) {
            throw new IllegalStateException("Sign up failed.\n > Status code: " + response.statusCode() + "\n > Body: " + response.body().asPrettyString());
        }

        return response;
    }

    public static Response signIn(String email, String password) {
        return signIn(email, password, new Cookies(), true);
    }

    public static Response signIn(String email, String password, boolean validate) {
        return signIn(email, password, new Cookies(), validate);
    }

    public static Response signIn(String email, String password, Cookies cookies) {
        return signIn(email, password, cookies, true);
    }

    public static Response signIn(String email, String password, Cookies cookies, boolean validate) {
        var response = given()
            .filter(csrfCookieFilter("/web/sign-in", cookies))
            .accept(ContentType.JSON)
            .formParam("username", email)
            .formParam("password", password)
            .post("/sign-in");

        if (validate && response.statusCode() != 200) {
            throw new IllegalStateException("Sign in failed.\n > Status code: " + response.statusCode() + "\n > Body: " + response.body().asPrettyString());
        }

        return response;
    }

    public static String authorize(Response signInResponse) {
        return authorize(signInResponse, "");
    }

    public static String authorize(Response signInResponse, String scopes) {
        var request = given()
            .redirects().follow(false)
            .cookies(signInResponse.getDetailedCookies())
            .param("response_type", "code")
            .param("client_id", "api-gateway")
            .param("redirect_uri", "http://api-gateway/login/oauth2/code/ciy");

        if (!scopes.isBlank()) {
            request.param("scope", scopes);
        }

        var response = request.get("/oauth2/authorize");

        if (response.statusCode() != 302) {
            throw new IllegalStateException("Authorize request failed.\n > Status code: " + response.statusCode() + "\n > Body: " + response.body().asPrettyString());
        }

        var location = response.getHeader("Location");
        if (location == null) {
            throw new IllegalStateException("Authorize request failed.\n > Location is null");
        }

        try {
            return new URIBuilder(location)
                .getQueryParams().stream()
                .filter(param -> param.getName().equals("code"))
                .findFirst().orElseThrow(() -> new IllegalStateException("Authorize request failed.\n > No code param."))
                .getValue();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Authorize request failed.", e);
        }
    }

    public static Filter csrfCookieFilter(String path) {
        return new CsrfCookieFilter(path, new Cookies());
    }

    public static Filter csrfCookieFilter(String path, Cookies cookies) {
        return new CsrfCookieFilter(path, cookies);
    }


    private static class CsrfCookieFilter implements Filter {
        private final String path;
        private final Cookies cookies;

        private CsrfCookieFilter(String path, Cookies cookies) {
            this.path = path;
            this.cookies = cookies;
        }

        @Override
        public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
            var res = given().cookies(cookies).get(path);

            var cookiesList = new ArrayList<Cookie>();
            cookiesList.addAll(res.getDetailedCookies().asList());
            cookiesList.addAll(
                cookies.asList().stream().filter(
                    cookie -> !res.getCookies().containsKey(cookie.getName())
                ).toList()
            );

            var reqCookies = new Cookies(cookiesList);
            requestSpec.cookies(reqCookies);

            var token = reqCookies.getValue("XSRF-TOKEN");
            requestSpec.header("X-XSRF-TOKEN", token);
            return ctx.next(requestSpec, responseSpec);
        }
    }
}
