package in.koala.controller;

import in.koala.service.FcmTestService;
import in.koala.util.FcmSender;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;

@RestController
@RequestMapping("/fcm")
public class FcmTestController {
    @Resource
    FcmTestService fcmTestService;

    @Resource
    FcmSender fcmSender;

    @GetMapping("/http")
    public ResponseEntity sendTest(@RequestParam String token) throws Exception{
        return new ResponseEntity(fcmTestService.sendTokenTest(token), HttpStatus.OK);
    }

    @GetMapping("/http/{topic}")
    public ResponseEntity sendTestTopic(@PathVariable String topic) throws Exception{
        return new ResponseEntity(fcmTestService.sendTopicTest(topic), HttpStatus.OK);
    }

    @GetMapping("/sub")
    public ResponseEntity subscribe(@RequestParam String name,
                                    @RequestParam String topic) throws Exception{
        fcmSender.subscribe(Arrays.asList(name), topic);
        return new ResponseEntity("success", HttpStatus.OK);
    }

    @GetMapping("/unsub")
    public ResponseEntity unsubscribe(@RequestParam String name,
                                      @RequestParam String topic) throws Exception{
        fcmSender.unsubscribe(Arrays.asList(name), topic);
        return new ResponseEntity("success", HttpStatus.OK);
    }
}
