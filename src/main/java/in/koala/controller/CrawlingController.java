package in.koala.controller;

import in.koala.annotation.ValidationGroups;
import in.koala.domain.CrawlingToken;
import in.koala.domain.response.CustomBody;
import in.koala.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
            return new ResponseEntity(CustomBody.of("아우누리 크롤링에 실패했습니다.", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/crawling/dorm")
    public ResponseEntity dormCrawling() throws Exception{
        Timestamp crawlingAt = new Timestamp(System.currentTimeMillis());
        if (crawlingService.dormCrawling(crawlingAt))
            return new ResponseEntity(CustomBody.of("아우미르 크롤링에 성공하였습니다.", HttpStatus.OK), HttpStatus.OK);
        else
            return new ResponseEntity(CustomBody.of("아우미르 크롤링에 실패했습니다.", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value="/crawling/youtube")
    public ResponseEntity youtubeCrawling() throws Exception{
        Timestamp crawlingAt = new Timestamp(System.currentTimeMillis());
        if (crawlingService.youtubeCrawling(crawlingAt))
            return new ResponseEntity(CustomBody.of("유튜브 크롤링에 성공하였습니다.", HttpStatus.OK), HttpStatus.OK);
        else
            return new ResponseEntity(CustomBody.of("유튜브 크롤링에 실패했습니다.", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value="/crawling/facebook")
    public ResponseEntity facebookCrawling(@RequestParam Long tokenId) throws Exception{
        Timestamp crawlingAt = new Timestamp(System.currentTimeMillis());
        if (crawlingService.facebookCrawling(tokenId, crawlingAt))
            return new ResponseEntity(CustomBody.of("페이스북 크롤링에 성공하였습니다.", HttpStatus.OK), HttpStatus.OK);
        else
            return new ResponseEntity(CustomBody.of("페이스북 크롤링에 실패했습니다.", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value="/crawling/instagram")
    public ResponseEntity instagramCrawling(@RequestParam Long tokenId) throws Exception{
        Timestamp crawlingAt = new Timestamp(System.currentTimeMillis());
        if (crawlingService.instagramCrawling(tokenId, crawlingAt))
            return new ResponseEntity(CustomBody.of("인스타그램 크롤링에 성공하였습니다.", HttpStatus.OK), HttpStatus.OK);
        else
            return new ResponseEntity(CustomBody.of("인스타그램 크롤링에 실패했습니다.", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    // 토큰 관련 API
    @PostMapping(value="/crawling/token")
    public void addToken(@Validated(ValidationGroups.createCrawlingToken.class)
                                       @RequestBody CrawlingToken token) throws Exception{
        crawlingService.addCrawlingToken(token);
    }

    @GetMapping(value="/crawling/token")
    public ResponseEntity getToken(@RequestParam("site") Long site) throws Exception {
        return new ResponseEntity (CustomBody.of(crawlingService.getCrawlingToken(site), HttpStatus.OK), HttpStatus.OK);
    }

    @PutMapping(value="/crawling/token")
    public void updateToken(@Validated(ValidationGroups.updateCrawlingToken.class)
                                @RequestBody CrawlingToken token) throws Exception {
        crawlingService.updateCrawlingToken(token);
    }

    @DeleteMapping(value="/crawling/token")
    public void deleteToken(@RequestParam("token-id") Long tokenId) throws Exception {
        crawlingService.deleteCrawlingToken(tokenId);
    }
}