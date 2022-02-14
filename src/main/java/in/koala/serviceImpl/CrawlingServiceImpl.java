package in.koala.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.koala.domain.Crawling;
import in.koala.domain.CrawlingToken;
import in.koala.enums.CrawlingSite;
import in.koala.enums.ErrorMessage;
import in.koala.exception.CrawlingException;
import in.koala.exception.NonCriticalException;
import in.koala.mapper.CrawlingMapper;
import in.koala.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    @Value("${instagram.api.call.url}")
    private String instagramApiUrl;

    private final CrawlingMapper crawlingMapper;

    @Override
    public String test() {
        return crawlingMapper.test();
    }

    @Override
    public Boolean portalCrawling(Timestamp crawlingAt) {

        List<Crawling> crawlingInsertList = new ArrayList<>();
        List<Crawling> crawlingUpdateList = new ArrayList<>();

        // 14= 일반공지, 15=장학공지, 16=학사공지, 150=채용공지, 151=현장실습공지, 148=총학생회, 21=학생생활
        String[] boardList = new String[]{"14", "15", "16", "150", "151", "148", "21"};

        for(String boardNumber : boardList) {
            Document portalHtml;
            String portalUrl = "https://portal.koreatech.ac.kr/ctt/bb/bulletin?b=" + boardNumber;

            try{
                portalHtml = getConnectionToKoreaTechSite(portalUrl);
            }
            catch (Exception e){
                throw new CrawlingException(ErrorMessage.UNABLE_CONNECT_TO_PORTAL);
            }

            Elements bulletins = portalHtml.select(".bc-s-tbllist > tbody > tr");
            for(Element bulletin : bulletins){
                Crawling crawling = getPortalDataOnBulletin(bulletin, crawlingAt);
                if(!crawlingMapper.checkDuplicatedData(crawling)){
                    crawlingInsertList.add(crawling);
                }
                else{
                    crawlingUpdateList.add(crawling);
                }
            }
        }
        updateLog(CrawlingSite.PORTAL, crawlingAt);
        updateTable(crawlingInsertList, crawlingUpdateList);
        return true;
    }

    @Override
    public Boolean dormCrawling(Timestamp crawlingAt) {

        List<Crawling> crawlingInsertList = new ArrayList<>();
        List<Crawling> crawlingUpdateList = new ArrayList<>();
        Document dormHtml;

        try{
            dormHtml = getConnectionToKoreaTechSite(dormUrl);
        }
        catch (Exception e){
            throw new CrawlingException(ErrorMessage.UNABLE_CONNECT_TO_DORM);
        }

        Elements bulletins = dormHtml.select(".boardList > tbody > tr");
        for(Element bulletin : bulletins) {
            Crawling crawling = getDormDataOnBulletin(bulletin, crawlingAt);
            if(!crawlingMapper.checkDuplicatedData(crawling)){
                crawlingInsertList.add(crawling);
            }
            else{
                crawlingUpdateList.add(crawling);
            }
        }
        updateLog(CrawlingSite.DORM, crawlingAt);
        updateTable(crawlingInsertList, crawlingUpdateList);
        return true;
    }


    @Override
    public Boolean youtubeCrawling(Timestamp crawlingAt) throws Exception {

        List<Crawling> crawlingList = new ArrayList<>();
        String time = makeYoutubeTimeFormat(crawlingMapper.getLatelyCrawlingTime());
        ResponseEntity<String> youtube;

        try{
            youtube = requestToYoutubeAPI(time);
        }
        catch (Exception e){
            throw new CrawlingException(ErrorMessage.UNABLE_CONNECT_TO_YOUTUBE);
        }

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(youtube.getBody());
        JSONArray jsonArray = (JSONArray) jsonObject.get("items");
        if(!jsonArray.isEmpty()){
            for(Object data : jsonArray){
                try{
                    Crawling crawling = getYoutubeDataOnJsonArray(data, crawlingAt);
                    if(!crawlingMapper.checkDuplicatedData(crawling)){
                        crawlingList.add(crawling);
                    }
                }
                catch (Exception e){
                    throw new CrawlingException(ErrorMessage.YOUTUBE_JSON_PARSE_EXCEPTION);
                }
            }
        }

        updateLog(CrawlingSite.YOUTUBE, crawlingAt);
        if(!crawlingList.isEmpty())
            crawlingMapper.addCrawlingData(crawlingList);
        return true;
    }

    @Override
    public Boolean facebookCrawling(Long tokenId, Timestamp crawlingAt) throws Exception {

        CrawlingSite site  = CrawlingSite.FACEBOOK;

        // read token
        CrawlingToken token = getCrawlingTokenById(tokenId);
        if(token.getSite() != site)
            throw new NonCriticalException(ErrorMessage.CRAWLING_TOKEN_INVALID_EXCEPTION);

        String facebookAccessToken = token.getToken();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'+0000'");

        format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        parseFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        List<Crawling> crawlingList = new ArrayList<Crawling>();

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

                    Crawling crawling = Crawling.builder()
                            .title(title)
                            .url(url)
                            .createdAt(createdAt)
                            .site(site)
                            .crawlingAt(crawlingAt)
                            .build();

                    if(crawlingMapper.checkDuplicatedData(crawling)){
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
    public Boolean instagramCrawling(Long tokenId, Timestamp crawlingAt) throws Exception {

        CrawlingSite site = CrawlingSite.INSTAGRAM;

        // read token
        CrawlingToken token = getCrawlingTokenById(tokenId);
        if(token.getSite() != site)
            throw new NonCriticalException(ErrorMessage.CRAWLING_TOKEN_INVALID_EXCEPTION);

        String instagramAccessToken = token.getToken();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'+0000'");

        format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        parseFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        List<Crawling> crawlingList = new ArrayList<Crawling>();

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

                    Crawling crawling = Crawling.builder()
                            .title(title)
                            .url(url)
                            .createdAt(createdAt)
                            .crawlingAt(crawlingAt)
                            .build();

                    if(crawlingMapper.checkDuplicatedData(crawling)){
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
    public void addCrawlingToken(CrawlingToken token) throws Exception {
        crawlingMapper.addToken(token);
    }

    @Override
    public List<CrawlingToken> getCrawlingToken() throws Exception {
        return crawlingMapper.getToken();
    }

    @Override
    public void updateCrawlingToken(CrawlingToken token) throws Exception {
        if(!crawlingMapper.checkTokenById(token.getId()))
            throw new NonCriticalException(ErrorMessage.CRAWLING_TOKEN_NOT_EXIST);
        crawlingMapper.updateToken(token);
    }

    @Override
    public void deleteCrawlingToken(Long id) throws Exception {
        if(!crawlingMapper.checkTokenById(id))
            throw new NonCriticalException(ErrorMessage.CRAWLING_TOKEN_NOT_EXIST);
        crawlingMapper.deleteTokenById(id);
    }

    @Override
    public Timestamp getLatelyCrawlingTime() {
        return crawlingMapper.getLatelyCrawlingTime();
    }

    @Override
    public Boolean executeAll() throws Exception {
        Timestamp crawlingAt = new Timestamp(System.currentTimeMillis());

        if(this.portalCrawling(crawlingAt) && this.dormCrawling(crawlingAt) && this.youtubeCrawling(crawlingAt))
            return true;
        else
            return false;
    }

    private Document getConnectionToKoreaTechSite(String url) throws Exception{
        Connection conn = Jsoup.connect(url);
        Document html = conn.get();
        return html;
    }

    private Crawling getPortalDataOnBulletin(Element bulletin, Timestamp crawlingAt){
        String title = bulletin.select("td > div > span").attr("title");
        StringBuffer buffer = new StringBuffer(bulletin.absUrl("data-url"));
        String url = buffer.insert(4,"s").toString();
        String createdAt = bulletin.select(".bc-s-cre_dt").text();

        Crawling crawling = Crawling.builder()
                .title(title)
                .url(url)
                .site(CrawlingSite.PORTAL)
                .createdAt(createdAt)
                .crawlingAt(crawlingAt)
                .build();
        return crawling;
    }

    private Crawling getDormDataOnBulletin(Element bulletin, Timestamp crawlingAt){
        String title = bulletin.select("td > a").text();
        String url = bulletin.child(1).child(0).absUrl("href");
        String createdAt = bulletin.select(".center").get(2).text();

        Crawling crawling = Crawling.builder()
                .title(title)
                .url(url)
                .site(CrawlingSite.DORM)
                .createdAt(createdAt)
                .crawlingAt(crawlingAt)
                .build();
        return crawling;
    }

    private void updateLog(CrawlingSite site, Timestamp crawlingAt) {
        crawlingMapper.updateLog(site, crawlingAt);
    }

    private void updateTable(List<Crawling> crawlingInsertList, List<Crawling> crawlingUpdateList){
        if(!crawlingInsertList.isEmpty()){
            crawlingMapper.addCrawlingData(crawlingInsertList);
        }
        if(!crawlingUpdateList.isEmpty()){
            crawlingMapper.updateCrawlingData(crawlingUpdateList);
        }
    }

    private String makeYoutubeTimeFormat(Timestamp latelyCrawlingTime){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return format.format(latelyCrawlingTime);
    }

    private ResponseEntity<String> requestToYoutubeAPI(String time) throws Exception{
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(youtubeApiUrl)
                .queryParam("part", "snippet")
                .queryParam("channelId",  youtubeChannelID)
                .queryParam("key",  youtubeAccessKey)
                .queryParam("maxResults", "50")
                .queryParam("type", "video")
                .queryParam("publishedAfter", time);

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
        return youtube;
    }

    private Crawling getYoutubeDataOnJsonArray(Object data, Timestamp crawlingAt) throws ParseException, JsonProcessingException {
        JSONObject jsonObj = (JSONObject) data;
        Map<String, String> idMap = new ObjectMapper().readValue(jsonObj.get("id").toString(), Map.class);
        Map<String, String> snippetMap = new ObjectMapper().readValue(jsonObj.get("snippet").toString(), Map.class);
        String title = snippetMap.get("title");
        String url = "https://www.youtube.com/watch?v=" + idMap.get("videoId");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        String createdAt = format2.format(format2.parse(snippetMap.get("publishTime")));
        Crawling crawling = Crawling.builder()
                .title(title)
                .url(url)
                .site(CrawlingSite.YOUTUBE)
                .createdAt(createdAt)
                .crawlingAt(crawlingAt)
                .build();
        return crawling;
    }

    private CrawlingToken getCrawlingTokenById(Long id) throws Exception {
        if(!crawlingMapper.checkTokenById(id))
            throw new NonCriticalException(ErrorMessage.CRAWLING_TOKEN_NOT_EXIST);
        return crawlingMapper.getTokenById(id);
    }

}