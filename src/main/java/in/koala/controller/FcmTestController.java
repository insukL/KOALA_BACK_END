package in.koala.controller;

import in.koala.service.FcmTestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class FcmTestController {
    @Resource
    FcmTestService fcmTestService;

    @GetMapping("/fcm")
    public ResponseEntity sendTest(@RequestParam String token) {
        try {
            return new ResponseEntity(fcmTestService.sendTest(token), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity("false", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
