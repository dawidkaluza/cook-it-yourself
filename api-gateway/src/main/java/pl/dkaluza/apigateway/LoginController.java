package pl.dkaluza.apigateway;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class LoginController {
    private final WebSettings webSettings;

    public LoginController(WebSettings webSettings) {
        this.webSettings = webSettings;
    }

    @GetMapping("/login")
    String login() {
        return "redirect:" + webSettings.getApiGatewayUrl() + "/oauth2/authorization/ciy";
    }

    @GetMapping(value = "/login", params = { "logout" })
    String logout() {
        return "redirect:" + webSettings.getWebAppSignOutUrl();
    }
}
