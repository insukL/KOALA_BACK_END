package in.koala.controller;

import in.koala.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
        crawlingService.portalCrawling();
    }

    @GetMapping(value = "/crawling/dorm")
    public void dormCrawling() throws Exception{
        crawlingService.dormCrawling();
    }

    @GetMapping(value="/crawling/youtube")
    public void youtubeCrawling() throws Exception{
        crawlingService.youtubeCrawling();
    }

}