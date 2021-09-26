package in.koala.controller.chat;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//채팅방을 보여주기 위한 컨트롤러
//임시 생성
//뷰를 반환하지 않으면 뷰(프론트, 안드 등등..)쪽이랑 이야기가 필요할지도?

@Controller
@RequestMapping(value = "/chat")
public class ChatRoomController {
    @GetMapping(value = "/room")
    public String room(){
        return "chat";
    }
}
