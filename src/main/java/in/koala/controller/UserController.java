package in.koala.controller;

import in.koala.annotation.Auth;
import in.koala.annotation.ValidationGroups;
import in.koala.annotation.Xss;
import in.koala.domain.AuthEmail;
import in.koala.domain.User;
import in.koala.domain.response.CustomBody;
import in.koala.enums.EmailType;
import in.koala.enums.SnsType;
import in.koala.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@RestController
@RequestMapping(value = "/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/non-member")
    public ResponseEntity createNonMemberUserAndDeviceToken(@RequestParam(name = "deviceToken") String deviceToken){
        return new ResponseEntity(CustomBody.of(userService.createNonMemberUserAndDeviceToken(deviceToken), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/oauth2/authorization/{snsType}")
    public ResponseEntity snsLogin(
            @PathVariable(name="snsType") SnsType snsType,
            @RequestParam(name="code") String code,
            @RequestParam(name="error", required = false) String error) throws Exception {

        if (error != null) {
            throw new IOException(error);
        } else {
            return new ResponseEntity<>(CustomBody.of(userService.snsLogin(code, snsType), HttpStatus.OK), HttpStatus.OK);
        }
    }

    @PostMapping(value="/oauth2/{snsType}")
    @ApiOperation(value ="sns 로그인 API" , notes = "각 클라이언트에서 발급받은 sns 의 accessToken 을 이용하여 로그인을 진행합니다. \n 헤더의 Authorization 에 accessToken 을 넣고 path 에는 요청하는 sns 의 type 을 넣으면 됩니다", authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity snsSignIn(
            @PathVariable(name="snsType") SnsType snsType){

        return new ResponseEntity(CustomBody.of(userService.snsSingIn(snsType), HttpStatus.OK), HttpStatus.OK);
    }

    @Xss
    @Auth
    @GetMapping(value="/my")
    @ApiOperation(value ="유저의 현재정보" , notes = "로그인된 유저의 정보를 반환한다." , authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity getMyInfo(){
        return new ResponseEntity(CustomBody.of(userService.getLoginUserInfo(), HttpStatus.OK), HttpStatus.OK);
    }

    @ApiOperation(value = "sns 로그인 서버 개발용", notes = "자동으로 해당 sns 로그인창으로 redirect 후 로그인 완료되면 access, refresh token 반환합니다. \n swagger 에서는 동작하지 않으니 주소창에 직접 입력 바랍니다.")
    @GetMapping(value="/{snsType}")
    public void requestSnsLogin(@PathVariable(name = "snsType") SnsType snsType) throws Exception {
        userService.requestSnsLogin(snsType);
    }

    @PostMapping(value="/sing-in")
    @ApiOperation(value="회원가입", notes = "회원가입에 성공하면 가입된 유저의 정보를 반환한다")
    public ResponseEntity signIn(@RequestBody @Validated({ValidationGroups.SingIn.class}) User user){
        return new ResponseEntity(CustomBody.of(userService.signUp(user), HttpStatus.CREATED), HttpStatus.CREATED);
    }

    @PostMapping(value="/login")
    @ApiOperation(value="로그인", notes="로그인이 성공적이면 accessToken 과 refreshToken 을 반환한다")
    public ResponseEntity login(
            @RequestBody @Validated({ValidationGroups.Login.class}) User user){

        return new ResponseEntity(CustomBody.of(userService.login(user), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value="/nickname-check")
    @ApiOperation(value="닉네임 중복체크", notes="닉네임 중복체크하는 api")
    public ResponseEntity checkNickname(@RequestParam @NotNull String nickname) {
        userService.checkNickname(nickname);
        return new ResponseEntity(CustomBody.of("사용 가능한 닉네임입니다.", HttpStatus.OK), HttpStatus.OK);

    }

    @GetMapping(value="/email-check")
    @ApiOperation(value="찾기용 이메일 중복체크", notes="비밀번호나 계정을 찾기 위해 등록하는 이메일의 중복체크")
    public ResponseEntity checkFindEmail(@RequestParam @Email(message="이메일 형식이 아닙니다") String email) {
        userService.checkFindEmail(email);
        return new ResponseEntity(CustomBody.of("사용 가능한 이메일입니다", HttpStatus.OK), HttpStatus.OK);
    }

    @Auth
    @PatchMapping(value="/nickname")
    @ApiOperation(value="닉네임 변경 요청", notes="", authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity changeNickname(@RequestParam @NotNull String nickname){
        userService.updateNickname(nickname);
        return new ResponseEntity(CustomBody.of(HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping(value="/refresh")
    @ApiOperation(value="access, refresh token 재발급", notes="refresh token 이 유효하다면 access token 과 refresh token 을 재발급한다.", authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity refresh(){
        return new ResponseEntity(CustomBody.of(userService.refresh(), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping(value="/email-send/{emailType}")
    @ApiOperation(value="이메일 전송 요청", notes="이메일 전송 요청 api 입니다. \n 공통적으로 email 을 받습니다. \n account 의 경우에는 email 을 제외하고 받지 않습니다. \n password 의 경우에는 account 와 email 을 받습니다. \n university 의 경우에는 email 과 헤더에 accessToken 을 받습니다.", authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity sendEmail(@RequestBody @Validated AuthEmail authEmail, @PathVariable EmailType emailType){
        userService.sendEmail(authEmail, emailType);
        return new ResponseEntity(CustomBody.of("전송 성공", HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping(value="/email/certification/{emailType}")
    @ApiOperation(value="이메일 전송 인증", notes="전송 이메일 인증 API 입니다. \n 공통적으로 email 을 받습니다. \n account 의 경우에는 email 을 제외하고 받지 않습니다. \n password 의 경우에는 account 와 email 을 받습니다. \n university 의 경우에는 email 과 헤더에 accessToken 을 받습니다.", authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity certificateEmail(
            @RequestBody @Validated AuthEmail authEmail,
            @PathVariable(value = "emailType") EmailType emailType){

        userService.certificateEmail(authEmail, emailType);
        return new ResponseEntity(CustomBody.of("인증 성공", HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value="/account-check")
    @ApiOperation(value="계정 존재 확인 API", notes="계정이 존재하는지 확인하는 API 입니다 \n 존재하지 않는다면 예외가 발생합니다")
    public ResponseEntity checkAccount(@RequestParam @NotNull String account){
        userService.checkAccount(account);
        return new ResponseEntity(CustomBody.of("존재하는 계정입니다", HttpStatus.OK), HttpStatus.OK);
    }


    @PostMapping(value="/password-change")
    @ApiOperation(value="비밀변호 변경 API", notes="비밀번호를 변경하는 API, 이메일 인증이 선행되어야 합니다, \n password 와 account 을 body 로 입력받습니다.")
    public ResponseEntity changePassword(@RequestBody User user){
        userService.changePassword(user);
        return new ResponseEntity(CustomBody.of("비밀번호 변경 성공", HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value="/account-find")
    @ApiOperation(value="계정 찾기 API", notes="이메일을 통하여 계정을 찾아 반환해주는 API 입니다, 이메일 인증이 선행되어야 합니다. \n query 로 이메일을 받습니다")
    public ResponseEntity findAccount(@RequestParam @Email(message="이메일 형식이 아닙니다") String email){
        return new ResponseEntity(CustomBody.of(userService.findAccount(email), HttpStatus.OK), HttpStatus.OK);
    }

    @Auth
    @GetMapping(value="/auth-check")
    @ApiOperation(value="학교 인증 여부 확인", notes="해당 계정의 학교 인증 여부를 확인하는 API", authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity checkAuth(){
        userService.isUniversityCertification();
        return new ResponseEntity(CustomBody.of("인증 완료", HttpStatus.OK), HttpStatus.OK);
    }

    @Auth
    @PatchMapping(value="/delete")
    @ApiOperation(value="유저 탈퇴", notes="유저 탈퇴", authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity deleteUser(){
        userService.softDeleteUser();
        return new ResponseEntity(CustomBody.of("탈퇴 완료", HttpStatus.OK), HttpStatus.OK);
    }
}