package pl.dkaluza.userservice;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.util.ArrayList;
import java.util.Map;

import static io.restassured.RestAssured.given;

public final class RestAssuredUtils {
    private RestAssuredUtils() {
    }

    public static void signUp(String email, String password, String name) {
        signUp(email, password, name, true);
    }

    public static void signUp(String email, String password, String name, boolean validate) {
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
