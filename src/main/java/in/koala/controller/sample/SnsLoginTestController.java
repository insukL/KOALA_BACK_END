package in.koala.controller.sample;

import in.koala.domain.sns.AppleLogin.AppleResponse;
import in.koala.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class SnsLoginTestController {

    private final String AUD = "com.koala.services";
    private final String REDIRECT_URI = "https://api.stage.koala.im/applecallback";

    @GetMapping(value = "/applelogin")
    public String appleLoginPage(ModelMap model) {

        model.addAttribute("client_id", AUD);
        model.addAttribute("redirect_uri", REDIRECT_URI);
        model.addAttribute("nonce", "asd");

        return "test/appleLogin";
    }

    @GetMapping(value="/naverlogin")
    public String appleLoginPage(){
        return "test/naverlogin";
    }

    @GetMapping(value = "/callback")
    public String naverCallBack(){
        return "test/callback";
    }

    @PostMapping(value = "/applecallback")
    @ResponseBody
    public AppleResponse appleCallBack(AppleResponse appleResponse){
        return appleResponse;
    }
}
