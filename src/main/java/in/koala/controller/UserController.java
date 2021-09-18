package in.koala.controller;

<<<<<<< HEAD
import in.koala.domain.naverLogin.NaverCallBack;
import in.koala.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Resource(name = "userServiceImpl")
    private UserService userService;

    @GetMapping(value = "/test")
    public String test() {
        return userService.test();
    }

    @GetMapping(value = "/login/naver")
    public ResponseEntity<Map<String, String>> naverLogin(NaverCallBack callBack) throws IOException {
        if (callBack.getError() != null) {
            throw new IOException(callBack.getError());
        } else {
            return new ResponseEntity<>(userService.naverLogin(callBack), HttpStatus.OK);
        }
    }
}