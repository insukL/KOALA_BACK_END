package in.koala.serviceImpl;

import com.google.common.collect.Lists;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import in.koala.domain.Crawling;
import in.koala.domain.Keyword;
import in.koala.enums.CrawlingSite;
import in.koala.service.KeywordPushService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeywordPushServiceImpl implements KeywordPushService {
    @Value("${alarm.image.url}")
    private String iconUrl;

    @NotNull
    @Override
    public void pushNotification(List<String> tokens, String keyword, Crawling crawling) throws Exception{
        String title = new StringBuilder()
                .append(crawling.getSite().getName())
                .append("에서 '")
                .append(keyword)
                .append("'관련 게시글이 업로드 되었습니다.")
                .toString();

        for (List<String> t : Lists.partition(tokens, 500)) {
            pushNotification(t, title, crawling.getUrl());
        }
    }

    private void pushNotification(List<String> tokens, String title, String url) throws Exception{
        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setImage(iconUrl)
                        .build())
                .putData("url", url)
                .build();
        try {
            FirebaseMessaging.getInstance().sendMulticast(message);
        } catch (FirebaseMessagingException e) {
            throw new Exception();
        }
    }

    @Override
    public void subscribe(Keyword keyword, Long id) throws Exception{
    }

    @Override
    public void unsubscribe(Keyword keyword, Long id) throws Exception{
    }

    @Override
    public void modifySubscription(List<CrawlingSite> oldSite,
                                   List<CrawlingSite> newSite,
                                   Long id, String keywordName) throws Exception{
    }
}
