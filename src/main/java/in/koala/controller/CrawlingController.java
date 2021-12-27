package in.koala.controller;

import in.koala.service.CrawlingService;
import lombok.RequiredArgsConstructor;
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
    public void portalCrawling() throws Exception{
        Timestamp crawlingAt = new Timestamp(System.currentTimeMillis());
        crawlingService.portalCrawling(crawlingAt);
    }

    @GetMapping(value = "/crawling/dorm")
    public void dormCrawling() throws Exception{
        Timestamp crawlingAt = new Timestamp(System.currentTimeMillis());
        crawlingService.dormCrawling(crawlingAt);
    }

    @GetMapping(value="/crawling/youtube")
    public void youtubeCrawling() throws Exception{
        Timestamp crawlingAt = new Timestamp(System.currentTimeMillis());
        crawlingService.youtubeCrawling(crawlingAt);
    }

}