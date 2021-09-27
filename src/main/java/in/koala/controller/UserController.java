package in.koala.controller;

import in.koala.domain.User;
import in.koala.domain.kakaoLogin.KakaoCallBack;
import in.koala.domain.kakaoLogin.KakaoProfile;
import in.koala.domain.naverLogin.NaverCallBack;
import in.koala.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/test")
    public String test() {
        return userService.test();
    }

    @GetMapping(value = "/oauth2/authorization/naver")
    public ResponseEntity naverLogin(NaverCallBack callBack) throws Exception {
        if (callBack.getError() != null) {
            throw new IOException(callBack.getError());
        } else {
            return new ResponseEntity<>(userService.snsLogin(callBack.getCode(), "Naver"), HttpStatus.OK);
        }
    }

    @GetMapping(value="/oauth2/authorization/kakao")
    public ResponseEntity<Map<String, String>> kakaoLogin(KakaoCallBack callBack) throws Exception{
        if(callBack.getError() != null){
            throw new IOException(callBack.getError());
        } else{
            return new ResponseEntity<>(userService.snsLogin(callBack.getCode(), "Kakao"), HttpStatus.OK);
        }
    }

    @PostMapping(value="/sing-up")
    public ResponseEntity signUp(@RequestBody User user){
        return new ResponseEntity(userService.signUp(user), HttpStatus.CREATED);
    }

    @PostMapping(value="/login")
    public ResponseEntity login(@RequestBody User user){
        return null;
    }
}