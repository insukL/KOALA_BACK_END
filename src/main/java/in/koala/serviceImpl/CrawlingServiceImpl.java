package in.koala.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.koala.domain.Crawling;
import in.koala.enums.CrawlingSite;
import in.koala.enums.ErrorMessage;
import in.koala.exception.CrawlingException;
import in.koala.mapper.CrawlingMapper;
import in.koala.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CrawlingServiceImpl implements CrawlingService {

   @Value("${dorm.url}")
    private String dormUrl;

    @Value("${youtube.channel.id}")
    private String youtubeChannelID;

    @Value("${youtube.access.key}")
    private String youtubeAccessKey;

    @Value("${youtube.api.call.url}")
    private String youtubeApiUrl;

    @Value("${facebook.api.call.url}")
    private String facebookApiUrl;

    @Value("${facebook.access.token}")
    private String facebookAccessToken;

    @Value("${instagram.api.call.url}")
    private String instagramApiUrl;

    @Value("${instagram.access.token}")
    private String instagramAccessToken;

    private final CrawlingMapper crawlingMapper;


    @Override
    public String test() {
        return crawlingMapper.test();
    }

    @Override
    public void updateLog(Integer site, Timestamp crawlingAt) {
        crawlingMapper.updateLog(site, crawlingAt);
    }

    @Override
    public void updateTable(List<Crawling> crawlingInsertList, List<Crawling> crawlingUpdateList){
        if(!crawlingInsertList.isEmpty()){
            crawlingMapper.addCrawlingData(crawlingInsertList);
        }
        if(!crawlingUpdateList.isEmpty()){
            crawlingMapper.updateCrawlingData(crawlingUpdateList);
        }
    }

    @Override
    public Timestamp getLatelyCrawlingTime() {
        return crawlingMapper.getLatelyCrawlingTime();
    }

    @Override
    public Boolean portalCrawling(Timestamp crawlingAt) throws Exception {

        // 크롤링한 객체들을 담을 List - 신규 데이터
        List<Crawling> crawlingInsertList = new ArrayList<Crawling>();
        // 크롤링한 객체들을 담을 List - 중복된 데이터
        List<Crawling> crawlingUpdateList = new ArrayList<Crawling>();
        Integer site = CrawlingSite.PORTAL.getCode();

        // 14= 일반공지, 15=장학공지, 16=학사공지, 150=채용공지, 151=현장실습공지, 148=총학생회, 21=학생생활
        String[] boardList = new String[]{"14", "15", "16", "150", "151", "148", "21"};
        // 아우미르 크롤링이랑 로직은 비슷함
        List<Crawling> crawlingList = new ArrayList<Crawling>();
        try{
            for(String boardNumber : boardList) {
                String portalUrl = "http://portal.koreatech.ac.kr/ctt/bb/bulletin?b="+ boardNumber;

                Connection conn = Jsoup.connect(portalUrl);
                Document html = conn.get();

                Elements elements = html.select(".bc-s-tbllist > tbody > tr");
                for(Element boardUrl : elements){
                    String title = boardUrl.select("td > div > span").attr("title");
                    StringBuffer buffer = new StringBuffer(boardUrl.absUrl("data-url"));
                    String url = buffer.insert(4,"s").toString();
                    String createdAt = boardUrl.select(".bc-s-cre_dt").text();
                    Crawling crawling = new Crawling(title, url, site, createdAt, crawlingAt);

                    // 크롤링 객체를 전부 담기 위해 리스트에 추가
                    if(crawlingMapper.checkDuplicatedData(crawling) == 0){
                        crawlingInsertList.add(crawling);
                    }
                    else{
                        crawlingUpdateList.add(crawling);
                    }
                }
            }
            //crawling_log 테이블에 로그 남기기
            updateLog(site, crawlingAt);

            // 크롤링 객체를 담은 리스트를 db에 추가
            updateTable(crawlingInsertList, crawlingUpdateList);

            return true;
        }
        catch (IOException e){
            throw new CrawlingException(ErrorMessage.UNABLE_CONNECT_TO_DORM);
        }

    }

    @Override
    public Boolean dormCrawling(Timestamp crawlingAt) throws Exception {

        // 크롤링한 객체들을 담을 List - 신규 데이터
        List<Crawling> crawlingInsertList = new ArrayList<Crawling>();
        // 크롤링한 객체들을 담을 List - 중복된 데이터
        List<Crawling> crawlingUpdateList = new ArrayList<Crawling>();
        Integer site = CrawlingSite.DORM.getCode();

        try{
            //아우미르 공지사항에 접속해서 html 파일을 전체 다 긁어오기
            Connection conn = Jsoup.connect(dormUrl);
            Document html = conn.get();

            //제목, url이 boadList 클래스 아래의 tobdy > tr 태그 내부에만 있기에 tr 태그 이하의 하위 태그들 다 긁어오기
            Elements elements = html.select(".boardList > tbody > tr");
            
            //tr 태그 이하의 하위 태그들에 대하여 제목과 url 추출
            for(Element element : elements) {
                // td 태그 하위 a태그에 있는 text = 제목
                String title = element.select("td > a").text();
                // 제목을 클릭했을 때 넘어가는 url을 절대 경로로 받기 위해서 child(1)=td / child(1).child(a) = a 이므로 absUrl()을 사용해서 절대 경로의 url 추출
                String url = element.child(1).child(0).absUrl("href");
                // td 태그 중에서 center 클래스로 지정되었는 태그들이 많은데 3번째로 존재하는 td 태그에 작성일이 담겨있음
                String createdAt = element.select(".center").get(2).text();

                Crawling crawling = new Crawling(title, url, site, createdAt, crawlingAt);

                // 크롤링 객체를 전부 담기 위해 리스트에 추가
                if(crawlingMapper.checkDuplicatedData(crawling) == 0){
                    crawlingInsertList.add(crawling);
                }
                else{
                    crawlingUpdateList.add(crawling);
                }
            }

            //crawling_log 테이블에 로그 남기기
            updateLog(site, crawlingAt);

            // 크롤링 객체를 담은 리스트를 db에 추가
            updateTable(crawlingInsertList, crawlingUpdateList);

            return true;
        }
        catch (IOException e){
            throw new CrawlingException(ErrorMessage.UNABLE_CONNECT_TO_PORTAL);
        }
    }



    @Override
    public Boolean youtubeCrawling(Timestamp crawlingAt) throws Exception {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date tmp = new Date();
        String now = format.format(tmp);
        List<Crawling> crawlingList = new ArrayList<Crawling>();

        Integer site = CrawlingSite.YOUTUBE.getCode();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(youtubeApiUrl)
                .queryParam("part", "snippet")
                .queryParam("channelId",  youtubeChannelID)
                .queryParam("key",  youtubeAccessKey)
                .queryParam("maxResults", "50")
                .queryParam("type", "video")
                .queryParam("publishedAfter", now);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);

        RestTemplate rt = new RestTemplate(factory);

        try{
            ResponseEntity<String> youtube = rt.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<String>(headers),
                    String.class
            );

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(youtube.getBody());
            JSONArray jsonArray = (JSONArray) jsonObject.get("items");

            if(!jsonArray.isEmpty()){
                for(int i=0;i<jsonArray.size();i++){

                    JSONObject jsonObj = (JSONObject)jsonArray.get(i);

                    Map<String, String> map1 = new ObjectMapper().readValue(jsonObj.get("id").toString(), Map.class);
                    Map<String, String> map2 = new ObjectMapper().readValue(jsonObj.get("snippet").toString(), Map.class);

                    String title = map2.get("title");
                    String url = "https://www.youtube.com/watch?v=" + map1.get("videoId");
                    SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
                    String createdAt = format2.format(format2.parse(map2.get("publishTime")));
                    Crawling crawling = new Crawling(title, url, site, createdAt, crawlingAt);
                    if(crawlingMapper.checkDuplicatedData(crawling) == 0){
                        crawlingList.add(crawling);
                    }
                }
            }
        }
        catch (IllegalStateException e){
            throw new CrawlingException(ErrorMessage.UNABLE_CONNECT_TO_YOUTUBE);
        }

        //crawling_log 테이블에 로그 남기기
        updateLog(site, crawlingAt);

        // 크롤링 객체를 담은 리스트를 db에 추가
        if(!crawlingList.isEmpty())
            crawlingMapper.addCrawlingData(crawlingList);

        return true;
    }


    @Override
    public Boolean facebookCrawling(Timestamp crawlingAt) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'+0000'");

        format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        parseFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        List<Crawling> crawlingList = new ArrayList<Crawling>();

        Integer site  = CrawlingSite.FACEBOOK.getCode();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(facebookApiUrl)
                .path("/me/posts")
                .queryParam("access_token", facebookAccessToken);

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);

        RestTemplate rt = new RestTemplate(factory);
        HttpHeaders headers = new HttpHeaders();

        try {
            Boolean next = true;

            while(next) {
                Map<String, Object> responseBody = rt.exchange(
                        builder.toUriString(),
                        HttpMethod.GET,
                        new HttpEntity<String>(headers),
                        Map.class
                ).getBody();

                ArrayList postList = (ArrayList)responseBody.get("data");

                for(int i=0;i<postList.size();i++) {
                    Map<String, String> data = (Map)postList.get(i);

                    String title = data.get("message");
                    String url = "https://www.facebook.com/" + data.get("id");

                    Date postDate = parseFormat.parse(data.get("created_time"));
                    String createdAt = format.format(postDate);

                    Crawling crawling = new Crawling(title, url, site, createdAt, crawlingAt);
                    if(crawlingMapper.checkDuplicatedData(crawling) == 0){
                        crawlingList.add(crawling);
                    }
                    else {
                        next = false;
                        break;
                    }
                }

                // Next page check
                Map<String, Object> paging = (Map)responseBody.get("paging");
                Map<String, Object> cursor = (Map)paging.get("cursors");

                if(next && paging.containsKey("next")) {
                    builder.replaceQueryParam("after", cursor.get("after"));
                }
                else {
                    next = false;
                }
            }
        }
        catch (IllegalStateException e){
            throw new CrawlingException(ErrorMessage.UNABLE_CONNECT_TO_FACEBOOK);
        }

        //crawling_log 테이블에 로그 남기기
        updateLog(site, crawlingAt);

        // 크롤링 객체를 담은 리스트를 db에 추가
        if(!crawlingList.isEmpty())
            crawlingMapper.addCrawlingData(crawlingList);

        return true;
    }

    @Override
    public Boolean instagramCrawling(Timestamp crawlingAt) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'+0000'");

        format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        parseFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        List<Crawling> crawlingList = new ArrayList<Crawling>();

        Integer site = CrawlingSite.INSTAGRAM.getCode();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(instagramApiUrl)
                .path("/me/media")
                .queryParam("fields", "caption,permalink,timestamp")
                .queryParam("access_token", instagramAccessToken);

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);

        RestTemplate rt = new RestTemplate(factory);
        HttpHeaders headers = new HttpHeaders();

        try {
            Boolean next = true;

            while(next) {
                Map<String, Object> responseBody = rt.exchange(
                        builder.toUriString(),
                        HttpMethod.GET,
                        new HttpEntity<String>(headers),
                        Map.class
                ).getBody();

                ArrayList postList = (ArrayList)responseBody.get("data");

                for(int i=0;i<postList.size();i++) {
                    Map<String, String> data = (Map)postList.get(i);

                    String title = data.get("caption");
                    String url = data.get("permalink");

                    Date postDate = parseFormat.parse(data.get("timestamp"));
                    String createdAt = format.format(postDate);

                    Crawling crawling = new Crawling(title, url, site, createdAt, crawlingAt);

                    if(crawlingMapper.checkDuplicatedData(crawling) == 0){
                        crawlingList.add(crawling);
                    }
                    else {
                        next = false;
                        break;
                    }
                }

                // Next page check
                Map<String, Object> paging = (Map)responseBody.get("paging");
                Map<String, Object> cursor = (Map)paging.get("cursors");

                if(next && paging.containsKey("next")) {
                    builder.replaceQueryParam("after", cursor.get("after"));
                }
                else {
                    next = false;
                }
            }
        }
        catch (IllegalStateException e){
            throw new CrawlingException(ErrorMessage.UNABLE_CONNECT_TO_INSTAGRAM);
        }

        //crawling_log 테이블에 로그 남기기
        updateLog(site, crawlingAt);

        // 크롤링 객체를 담은 리스트를 db에 추가
        if(!crawlingList.isEmpty())
            crawlingMapper.addCrawlingData(crawlingList);

        return true;
    }

    @Override
    public Boolean executeAll() throws Exception {


        Timestamp crawlingAt = new Timestamp(System.currentTimeMillis());

        if(this.portalCrawling(crawlingAt) && this.dormCrawling(crawlingAt) && this.youtubeCrawling(crawlingAt))
            return true;
        else
            return false;
    }

}