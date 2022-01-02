package in.koala.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;

@Configuration
public class FcmConfig {
    @Value("${firebase.key.path}")
    private Resource key;

    @PostConstruct
    public void initFirebase() throws Exception {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(key.getInputStream()))
                .build();
        FirebaseApp.initializeApp(options);
    }
}