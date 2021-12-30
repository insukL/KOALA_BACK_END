package in.koala.controller;

import in.koala.domain.response.CustomBody;
import in.koala.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@RestController
@RequiredArgsConstructor
public class CrawlingController {

    private final CrawlingService crawlingService;

    @GetMapping(value = "/crawling/test")
    public String test() {
        return crawlingService.test();
    }

    @GetMapping(value="/crawling/portal")
    public ResponseEntity portalCrawling() throws Exception{
        Timestamp crawlingAt = new Timestamp(System.currentTimeMillis());
        if (crawlingService.portalCrawling(crawlingAt))
            return new ResponseEntity(CustomBody.of("아우누리 크롤링에 성공하였습니다.", HttpStatus.OK), HttpStatus.OK);
        else
            return new ResponseEntity("아우누리 크롤링에 실패했습니다.", HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/crawling/dorm")
    public ResponseEntity dormCrawling() throws Exception{
        Timestamp crawlingAt = new Timestamp(System.currentTimeMillis());
        if (crawlingService.dormCrawling(crawlingAt))
            return new ResponseEntity(CustomBody.of("아우미르 크롤링에 성공하였습니다.", HttpStatus.OK), HttpStatus.OK);
        else
            return new ResponseEntity("아우미르 크롤링에 실패했습니다.", HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value="/crawling/youtube")
    public ResponseEntity youtubeCrawling() throws Exception{
        Timestamp crawlingAt = new Timestamp(System.currentTimeMillis());
        if (crawlingService.youtubeCrawling(crawlingAt))
            return new ResponseEntity(CustomBody.of("유튜브 크롤링에 성공하였습니다.", HttpStatus.OK), HttpStatus.OK);
        else
            return new ResponseEntity("유튜브 크롤링에 실패했습니다.", HttpStatus.BAD_REQUEST);
    }

}