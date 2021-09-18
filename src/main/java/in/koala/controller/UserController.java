package in.koala.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/*")
public class UserController {

    @PostMapping("/join")
    public ResponseEntity userJoin(){
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
