package in.koala.controller;

import in.koala.annotation.Auth;
import in.koala.annotation.Xss;
import in.koala.domain.AuthEmail;
import in.koala.domain.User;
import in.koala.enums.SnsType;
import in.koala.service.UserService;
import io.swagger.annotations.Api;
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
    @ApiOperation(value ="유저의 현재정보" , notes = "로그인된 유저의 정보를 반환한다." , authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity getMyInfo(){
        return new ResponseEntity<>(userService.getLoginUserInfo(), HttpStatus.OK);
    }

    @ApiOperation(value = "sns 로그인 요청", notes = "sns 로그인을 요청하는 api 입니다. 원하는 sns 를 path 에 넣으시면 됩니다. swagger 에서는 동작하지 않으니 주소창에 직접 입력 바랍니다.")
    @GetMapping(value="/{snsType}")
    public void requestSnsLogin(@PathVariable(name = "snsType") SnsType snsType) throws Exception {
        userService.requestSnsLogin(snsType);
    }

    @PostMapping(value="/sing-up")
    @ApiOperation(value="회원가입", notes = "회원가입에 성공하면 가입된 유저의 정보를 반환한다")
    public ResponseEntity signUp(@RequestBody User user){
        return new ResponseEntity(userService.signUp(user), HttpStatus.CREATED);
    }

    @PostMapping(value="/login")
    @ApiOperation(value="로그인", notes="로그인이 성공적이면 accessToken 과 refreshToken 을 반환한다")
    public ResponseEntity login(@RequestBody User user){
        return new ResponseEntity(userService.login(user), HttpStatus.OK);
    }

    @GetMapping(value="/nickname-check")
    @ApiOperation(value="닉네임 중복체크", notes="닉네임 중복체크하는 api")
    public ResponseEntity checkNickname(@RequestParam String nickname){
        if ( userService.checkNickname(nickname) )
            return new ResponseEntity("사용 가능한 닉네임입니다." ,HttpStatus.OK);
        else
            return new ResponseEntity("중복된 닉네임입니다." ,HttpStatus.BAD_REQUEST);
    }

    @Auth
    @PatchMapping(value="/nickname")
    @ApiOperation(value="닉네임 변경 요청", notes="", authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity changeNickname(@RequestParam String nickname){
        userService.updateNickname(nickname);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value="/refresh")
    @ApiOperation(value="access token 재발급", notes="refresh token 이 유효하다면 access token 과 refresh token 을 재발급한다.", authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity refresh(){
        return new ResponseEntity(userService.refresh(), HttpStatus.OK);
    }

    @Auth
    @PostMapping(value="/email")
    @ApiOperation(value="이메일 전송 요청", notes="전송할 email 과 전송 type 를 받는다 type 이 0이면 비밀번호 재발급, 1 이면 채팅 인증이다.", authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity sendEmail(@RequestBody AuthEmail authEmail){
        userService.sendEmail(authEmail);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Auth
    @PostMapping(value="/email/certification")
    @ApiOperation(value="이메일 전송 인증")
    public ResponseEntity certificateEmail(@RequestBody AuthEmail authEmail){
        return null;
    }
}