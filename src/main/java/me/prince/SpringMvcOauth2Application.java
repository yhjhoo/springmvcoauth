package me.prince;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class SpringMvcOauth2Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringMvcOauth2Application.class, args);
    }
}


@Controller
class WelcomeController {
    private String oauthURL = "http://localhost:8080/uaa/oauth/authorize";
    private String oauthtoken = "http://localhost:8080/uaa/oauth/token";
    private String clientId = "identity";
    private String clientSecret = "identitysecret";

    private String redirectURL = "http://localhost:8080/uaa/oauth/authorize?client_id=identity&response_type=code&redirect_uri=http://localhost:8082/callback";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/auth")
    public String auth() {
        return "redirect:" + redirectURL;
    }

    HttpHeaders createHeaders(String username, String password) {
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII")));
            String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
        }};
    }

    @GetMapping("/callback")
    public String callback(HttpServletRequest request, String code) {
        RestTemplate template = new RestTemplate();


        Map<String, String> map = new HashMap<>();
        map.put("client_id", clientId);
        map.put("client_secret", clientSecret + 1);
//        map.put("code", code);
//        map.put("grant_type", "authorization_code");
//        map.put("response_type", "token");
//        map.put("redirect_uri", "http://localhost:8082/auth");

//        String result = template.postForObject(oauthtoken +"?"+ "grant_type=authorization_code&code="+code
//                , map, String.class);


        HttpEntity<String> header = new HttpEntity<String>(createHeaders(clientId, clientSecret));

        ResponseEntity<String> result = template.exchange(oauthtoken + "?" + "grant_type=authorization_code&code=" + code,
                HttpMethod.POST, header, String.class, map);
        logger.info(result.getBody() );
        return "hello";
    }
}