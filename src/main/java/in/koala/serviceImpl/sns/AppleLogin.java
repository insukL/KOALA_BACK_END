package in.koala.serviceImpl.sns;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.koala.domain.sns.SnsUser;
import in.koala.serviceImpl.sns.dto.Key;
import in.koala.serviceImpl.sns.dto.PublicKeys;
import in.koala.enums.ErrorMessage;
import in.koala.enums.SnsType;
import in.koala.exception.CriticalException;
import in.koala.exception.NonCriticalException;
import in.koala.service.sns.SnsLogin;
import in.koala.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;

@RequiredArgsConstructor
@Service
public class AppleLogin implements SnsLogin {

    @Value("${APPLE.AUD}")
    private String AUD;

    private final JwtUtil jwtUtil;

    @Override
    public SnsUser requestUserProfile(String code) throws Exception {
        return null;
    }

    @Override
    public SnsUser requestUserProfileByToken(String token) {
        Claims claims = this.verifyToken(token)
                .orElseThrow(() -> new NonCriticalException(ErrorMessage.IDENTITY_TOKEN_INVALID_EXCEPTION));

        return SnsUser.builder()
                .account(getSnsType() + "_" + claims.get("sub").toString())
                .email(claims.get("email").toString())
                .nickname(getSnsType() + "_" + claims.get("sub").toString())
                .profile(null)
                .snsType(SnsType.APPLE)
                .build();
    }

    @Override
    public SnsType getSnsType() {
        return SnsType.APPLE;
    }

    @Override
    public String getRedirectUri() {
        return null;
    }

    private Optional<Claims> verifyToken(String identityToken) {
        try {
            PublicKeys applePublicKeys = requestApplePublicKeys();
            Key key = selectAppropriateKey(applePublicKeys, identityToken);

            byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
            byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

            BigInteger n = new BigInteger(1, nBytes);
            BigInteger e = new BigInteger(1, eBytes);

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance(key.getKty());
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            return Optional.of(jwtUtil.validateIdToken(identityToken, publicKey, AUD));

        } catch (JsonProcessingException | NoSuchAlgorithmException | InvalidKeySpecException e){
            throw new CriticalException(ErrorMessage.APPLE_PUBLIC_KEY_EXCEPTION);
        }
    }

    private PublicKeys requestApplePublicKeys() throws JsonProcessingException {
        RestTemplate rt = new RestTemplate();

        ResponseEntity<String> response = rt.getForEntity(
                "https://appleid.apple.com/auth/keys",
                String.class
        );

        return new ObjectMapper().readValue(response.getBody(), PublicKeys.class);
    }

    private Key selectAppropriateKey(PublicKeys keys, String token){
        List<Key> keyList = keys.getKeys();

        Map header = jwtUtil.getHeaderFromJwt(token);

        if(header == null || header.get("kid") == null){
            throw new NonCriticalException(ErrorMessage.IDENTITY_TOKEN_INVALID_EXCEPTION);
        }

        String kid = header.get("kid").toString();

        for(Key key : keyList){
            if(key.getKid().equals(kid)){
                return key;
            }
        }

        throw new CriticalException(ErrorMessage.IDENTITY_TOKEN_INVALID_EXCEPTION);
    }
}
