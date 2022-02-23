package in.koala.serviceImpl.sns;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import in.koala.domain.sns.SnsUser;
import in.koala.enums.ErrorMessage;
import in.koala.enums.SnsType;
import in.koala.exception.NonCriticalException;
import in.koala.service.sns.SnsLogin;
import in.koala.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleLogin implements SnsLogin {

    @Value("${google.client-id}")
    private String GOOGLE_CLIENT_ID;

    private final JwtUtil jwtUtil;

    @Override
    public SnsUser requestUserProfileByToken(String token) {
        if(verifyToken(token)){
            Map claims = jwtUtil.getClaimFromJwt(token);

            return SnsUser.builder()
                    .account(getSnsType() + "_" + claims.get("sub"))
                    .email((String) claims.get("email"))
                    .nickname(getSnsType() + "_" + claims.get("sub"))
                    .profile((String) claims.get("picture"))
                    .snsType(SnsType.GOOGLE)
                    .build();

        } else{
            throw new NonCriticalException(ErrorMessage.IDENTITY_TOKEN_INVALID_EXCEPTION);
        }
    }

    private boolean verifyToken(String idToken) {

        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();

            System.out.println(verifier.verify(idToken).getPayload());
            return verifier.verify(idToken) != null;

        } catch (GeneralSecurityException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public SnsType getSnsType() {
        return SnsType.GOOGLE;
    }

    @Override
    public SnsUser requestUserProfile(String code) throws Exception {
        return null;
    }

    @Override
    public String getRedirectUri() {
        return null;
    }
}
