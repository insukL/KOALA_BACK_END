package in.koala.serviceImpl.sns;

import in.koala.domain.sns.AppleLogin.Key;
import in.koala.domain.sns.AppleLogin.ApplePublicKeys;
import in.koala.enums.ErrorMessage;
import in.koala.enums.SnsType;
import in.koala.exception.CriticalException;
import in.koala.exception.NonCriticalException;
import in.koala.service.sns.SnsLogin;
import in.koala.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AppleLogin implements SnsLogin {

    private final JwtUtil jwtUtil;

    @Override
    public Map requestUserProfile(String code) throws Exception {
        return null;
    }

    @Override
    public Map requestUserProfileBySnsToken(String identityToken) {

        Map<String, String> profile = null;

        try {
            Key key = this.selectAppropriateKey(requestApplePublicKey(), identityToken);

            byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
            byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

            BigInteger n = new BigInteger(1, nBytes);
            BigInteger e = new BigInteger(1, eBytes);

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance(key.getKty());
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            Claims claims = jwtUtil.getClaimsFromJwt(identityToken, publicKey);

            profile.put("account", "APPLE" + "_" + claims.get("sub").toString());
            profile.put("sns_email", claims.get("email").toString());
            profile.put("nickname", claims.get("sub").toString());
            profile.put("user_type", "4");
            profile.put("profile", null);

        } catch(NoSuchAlgorithmException e){
        } catch (InvalidKeySpecException e){
        } catch (Exception e) {
        }

        return profile;
    }

    @Override
    public SnsType getSnsType() {
        return SnsType.APPLE;
    }

    @Override
    public String getRedirectUri() {
        return null;
    }

    private boolean verifyToken(){
        return false;
    }

    private ApplePublicKeys requestApplePublicKey(){
        HttpHeaders headers = new HttpHeaders();
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<ApplePublicKeys> response = rt.exchange(
                "https://appleid.apple.com/auth/keys",
                HttpMethod.GET,
                request,
                ApplePublicKeys.class
        );

        return response.getBody();
    }

    private Key selectAppropriateKey(ApplePublicKeys keys, String token){
        List<Key> keyList = keys.getKeys();

        Header header = jwtUtil.getHeaderFromJwt(token);

        if(header.get("kid") == null){
            throw new NonCriticalException(ErrorMessage.APPLE_PUBLIC_KEY_EXCEPTION);
        }

        String kid = header.get("kid").toString();

        for(var key : keyList){
            if(key.getKid().equals(kid)){
                return key;
            }
        }

        throw new CriticalException(ErrorMessage.IDENTITY_TOKEN_INVALID_EXCEPTION);
    }
}
