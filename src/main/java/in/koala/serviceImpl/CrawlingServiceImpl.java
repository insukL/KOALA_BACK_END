package in.koala.serviceImpl;

import in.koala.service.CrawlingService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.Charset;

@Service
public class CrawlingServiceImpl implements CrawlingService {

    @Value("${dorm.page.find.url}")
    private String dormPageFindUrl;

    @Value("${youtube.koreatch.channel.id}")
    private String youtubeKoreatechChannelID;

    @Value("${youtube.access.key}")
    private String youtubeAccessKey;

    @Override
    public void dormCrawling() throws Exception {
        int page = 0;
        int maxPage = 0;
        int idx = 0;

        try{
            Connection conn = Jsoup.connect(dormPageFindUrl);
            Document html = conn.get();
            Elements elements = html.select(".listCount");
            System.out.println(elements.text());
            maxPage = Integer.parseInt(elements.text().substring(18,20));
        }
        catch (IOException e){
            e.printStackTrace();
        }

        while(page<=maxPage){
            try{
                ++page;
                String url = "https://dorm.koreatech.ac.kr/content/board/list.php?now_page="+page+"&GUBN=&SEARCH=&BOARDID=notice"; // 아우미르
                Connection conn = Jsoup.connect(url);
                Document html = conn.get();

                Elements elements = html.select(".boardList > tbody > tr > td:not(.center) > a");
                for(Element element : elements) {
                    System.out.println(++idx + " - " + "주소 : " + element.attr("abs:href") + "\n     제목 : " + element.text());
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void youtubeCrawling() throws Exception {
        Boolean check = true;

        String url = "https://www.googleapis.com/youtube/v3/search";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("part", "snippet")
                .queryParam("channelId",  youtubeKoreatechChannelID)
                .queryParam("key",  youtubeAccessKey)
                .queryParam("maxResults", "50")
                .queryParam("type", "video");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);

        RestTemplate rt = new RestTemplate(factory);

        ResponseEntity<String> youtube = rt.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<String>(headers),
                String.class
        );

        if(youtube.getStatusCodeValue()==200){
            System.out.println(youtube.getBody());
        }
    }
}