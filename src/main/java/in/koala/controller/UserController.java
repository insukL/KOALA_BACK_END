package in.koala.controller;

import in.koala.annotation.Auth;
import in.koala.annotation.Xss;
import in.koala.domain.User;
import in.koala.enums.SnsType;
import in.koala.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(value = "/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/oauth2/authorization/{snsType}")
    public ResponseEntity snsLogin(
            @PathVariable(name="snsType") SnsType snsType,
            @RequestParam(name="code") String code,
            @RequestParam(name="error", required = false) String error) throws Exception {

        if (error != null) {
            throw new IOException(error);
        } else {
            return new ResponseEntity<>(userService.snsLogin(code, snsType), HttpStatus.OK);
        }
    }

    @Xss
    @Auth
    @GetMapping(value="/my")
    @ApiOperation(value ="유저의 현재정보" , notes = "유재의 현재 정보를 반환한다." , authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity getMyInfo(){
        return new ResponseEntity<>(userService.getLoginUserInfo(), HttpStatus.OK);
    }

    @ApiOperation(value = "sns 로그인 요청", notes = "sns 로그인을 요청하는 api 입니다. 원하는 sns 를 path 에 넣으시면 됩니다. swagger 에서는 동작하지 않으니 주소창에 직접 입력 바랍니다.")
    @GetMapping(value="/{snsType}")
    public void requestSnsLogin(@PathVariable(name = "snsType") SnsType snsType) throws Exception {
        userService.requestSnsLogin(snsType);
    }

    @PostMapping(value="/sing-up")
    public ResponseEntity signUp(@RequestBody User user){
        return new ResponseEntity(userService.signUp(user), HttpStatus.CREATED);
    }

    @PostMapping(value="/login")
    public ResponseEntity login(@RequestBody User user){
        return new ResponseEntity(userService.login(user), HttpStatus.OK);
    }


}