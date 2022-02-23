package in.koala.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.koala.enums.ErrorMessage;
import in.koala.enums.TokenType;
import in.koala.enums.UserType;
import in.koala.exception.NonCriticalException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.*;

@Component
public class JwtUtil {

    @Value("${spring.jwt.secret}")
    private String key;

    public Map getHeaderFromJwt(String token){
        Base64.Decoder decoder = Base64.getDecoder();
        String[] chunks = token.split("\\.");
        String header = new String(decoder.decode(chunks[0]));

        HashMap<String,String> jsonMap = null;

        try{
            jsonMap = new ObjectMapper().readValue(header, HashMap.class);
        } catch (Exception e){
            e.printStackTrace();
        }

        return jsonMap;
    }


    public String generateToken(Long id, TokenType tokenType, UserType userType){

        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg","HS256");

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("id", id );
        payloads.put("sub", tokenType.name());
        payloads.put("aud", userType.name());

        // 토큰 유효기간 설정
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        calendar.add(tokenType.getCalendar(), tokenType.getTokenRemainTime());

        return Jwts.builder().setHeader(headers).setClaims(payloads).setExpiration(calendar.getTime()).signWith(SignatureAlgorithm.HS256, key.getBytes()).compact();
    }

    public boolean validateIdToken(String idToken, PublicKey key) {
        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(idToken)
                    .getBody();

        } catch (ExpiredJwtException e) {
            throw new NonCriticalException(ErrorMessage.IDENTITY_TOKEN_EXPIRED_EXCEPTION);

        } catch (Exception e) {
            throw new NonCriticalException(ErrorMessage.IDENTITY_TOKEN_INVALID_EXCEPTION);
        }

        return true;
    }


    public boolean validateToken(String token, TokenType tokenType){

        if(token == null) throw new NonCriticalException(ErrorMessage.ACCESS_TOKEN_NOT_EXIST);
        if(!token.startsWith("Bearer ")) throw new NonCriticalException(ErrorMessage.JWT_NOT_START_BEARER);

        Claims claims = null;
        token = token.substring(7);

        try{
            claims = Jwts.parser()
                    .setSigningKey(key.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

        } catch(ExpiredJwtException e){
            if(tokenType.equals(TokenType.ACCESS)) {
                throw new NonCriticalException(ErrorMessage.ACCESSTOKEN_EXPIRED_EXCEPTION);

            } else if(tokenType.equals(TokenType.REFRESH)){
                throw new NonCriticalException(ErrorMessage.REFRESHTOKEN_EXPIRED_EXCEPTION);

            } else if(tokenType.equals(TokenType.SOCKET)){
                throw new NonCriticalException(ErrorMessage.SOCKETTOKEN_EXPIRED_EXCEPTION);
            }

        } catch(Exception e){
            if(tokenType.equals(TokenType.ACCESS)) {
                throw new NonCriticalException(ErrorMessage.ACCESSTOKEN_INVALID_EXCEPTION);

            } else if(tokenType.equals(TokenType.REFRESH)){
                throw new NonCriticalException(ErrorMessage.REFRESHTOKEN_INVALID_EXCEPTION);

            } else if(tokenType.equals(TokenType.SOCKET)){
                throw new NonCriticalException(ErrorMessage.SOCKETTOKEN_INVALID_EXCEPTION);
            }
        }

        if(claims.get("id") == null || claims.get("sub") == null || claims.get("exp") == null || claims.get("aud") == null){
            if(tokenType.equals(TokenType.ACCESS)){
                throw new NonCriticalException(ErrorMessage.ACCESSTOKEN_INVALID_EXCEPTION);

            } else if(tokenType.equals(TokenType.REFRESH)){
                throw new NonCriticalException(ErrorMessage.REFRESHTOKEN_INVALID_EXCEPTION);
            }
        }

        String sub = String.valueOf(claims.get("sub"));

        if(!sub.equals(tokenType.name())) {
            if (tokenType.equals(TokenType.ACCESS)) {
                // access token 에 다른 토큰이 들어간 경우
               throw new NonCriticalException(ErrorMessage.ACCESSTOKEN_INVALID_EXCEPTION);

            } else if(tokenType.equals(TokenType.REFRESH)){
                // refresh token 에 다른 토큰이 들어간 경우
                throw new NonCriticalException(ErrorMessage.REFRESHTOKEN_INVALID_EXCEPTION);
            } else if(tokenType.equals(TokenType.SOCKET)){
                // socket token 에 다른 토큰이 들어간 경우
                throw new NonCriticalException(ErrorMessage.SOCKETTOKEN_INVALID_EXCEPTION);
            }
        }

        return true;
    }

    public Map getClaimFromJwt(String token){
        Base64.Decoder decoder = Base64.getDecoder();
        String[] chunks = token.split("\\.");
        String header = new String(decoder.decode(chunks[1]));

        HashMap<String,String> claims = null;

        try{
            claims = new ObjectMapper().readValue(header, HashMap.class);
        } catch (Exception e){
            e.printStackTrace();
        }

        return claims;
    }
}
