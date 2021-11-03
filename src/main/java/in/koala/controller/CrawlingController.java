package in.koala.controller;

import in.koala.service.CrawlingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class CrawlingController {

    @Resource(name = "crawlingServiceImpl")
    private CrawlingService crawlingService;

    @GetMapping(value = "/dorm")
    public void dormCrawling() throws Exception{
        crawlingService.dormCrawling();
    }

    @GetMapping(value="/youtube")
    public void youtubeCrawling() throws Exception{
        crawlingService.youtubeCrawling();
    }
}
