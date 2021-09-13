package in.koala.serviceImpl;

import in.koala.service.CrawlingService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CrawlingServiceImpl implements CrawlingService {

    @Override
    public void dormCrawling() throws Exception {
        int page = 0;
        int maxPage = 0;
        int idx = 0;

        try{
            String url = "https://dorm.koreatech.ac.kr/content/board/list.php?BOARDID=notice"; //아우미르 공지사항 url
            Connection conn = Jsoup.connect(url);
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
                //String url = "https://portal.koreatech.ac.kr/ctt/bb/bulletin?b=14"; //아우누리 - 일반 공지사항(오류남 - 접근이 안되는것 같음..)
                String url = "https://dorm.koreatech.ac.kr/content/board/list.php?now_page="+page+"&GUBN=&SEARCH=&BOARDID=notice"; // 아우미르
                Connection conn = Jsoup.connect(url);
                Document html = conn.get();
                //System.out.println(html.toString()); //전체 html 출력

                Elements elements = html.select(".boardList > tbody > tr > td:not(.center) > a");
                //System.out.println(elements);

                for(Element element : elements) {
                    System.out.println(++idx + " - " + "주소 : " + element.attr("abs:href") + "\n     제목 : " + element.text());
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
