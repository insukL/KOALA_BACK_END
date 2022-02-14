package in.koala.controller;

import in.koala.controller.response.BaseResponse;
import in.koala.service.DeviceTokenService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DeviceTokenController {

    private final DeviceTokenService deviceTokenService;

    @PatchMapping(value = "/token")
    @ApiOperation(value = "토큰 변경 API", notes = "회원의 토큰을 변경하는 API 입니다. \n 만료된 토큰과 새로운 토큰을 같이 파라미터로 받습니다.", authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity updateTokenByUser(
            @RequestParam(name = "expired_token") String expiredToken,
            @RequestParam(name="new_token") String newToken){

        return new ResponseEntity(BaseResponse.of(deviceTokenService.updateToken(expiredToken, newToken), HttpStatus.OK), HttpStatus.OK);
    }
}
