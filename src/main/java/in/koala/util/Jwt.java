package in.koala.util;

import in.koala.enums.ErrorMessage;
import in.koala.enums.TokenType;
import in.koala.exception.NonCriticalException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class Jwt {

    @Value("${spring.jwt.secret}")
    private String key;


    public String generateToken(Long id, TokenType tokenType){

        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg","HS256");

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("id", id );
        payloads.put("sub", tokenType.name());

        return Jwts.builder().setHeader(headers).setClaims(payloads).setExpiration(tokenType.getTokenExp()).signWith(SignatureAlgorithm.HS256, key.getBytes()).compact();
    }

    public boolean isValid(String token, TokenType tokenType){

        if(token == null) throw new NonCriticalException(ErrorMessage.JWT_NOT_EXIST);
        if(!token.startsWith("Bearer ")) throw new NonCriticalException(ErrorMessage.JWT_NOT_START_BEARER);

        Claims claims = this.getClaimsFromJwtToken(token, tokenType);

        String sub = String.valueOf(claims.get("sub"));

        if(!sub.equals(tokenType.name())) {
            if (tokenType.equals(TokenType.ACCESS)) {
                // access token 에 refresh token 이 들어간 경우
               throw new NonCriticalException(ErrorMessage.ACCESSTOKEN_INVALID_EXCEPTION);

            } else {
                // refresh token 에 access token 이 들어간 경우
                throw new NonCriticalException(ErrorMessage.REFRESHTOKEN_INVALID_EXCEPTION);
            }
        }

        return true;
    }

    public Claims getClaimsFromJwtToken(String token, TokenType tokenType){
        //System.out.println(token);
        Claims claims = null;
        String sub = null;
        token = token.substring(7);

        try{
            claims = Jwts.parser()
                    .setSigningKey(key.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

        } catch(ExpiredJwtException e){
            if(tokenType.equals(TokenType.ACCESS)) {
                throw new NonCriticalException(ErrorMessage.ACCESSTOKEN_EXPIRED_EXCEPTION);

            } else {
                throw new NonCriticalException(ErrorMessage.REFRESHTOKEN_EXPIRED_EXCEPTION);
            }

        } catch(Exception e){
            if(tokenType.equals(TokenType.ACCESS)) {
                throw new NonCriticalException(ErrorMessage.ACCESSTOKEN_INVALID_EXCEPTION);

            } else {
                throw new NonCriticalException(ErrorMessage.REFRESHTOKEN_INVALID_EXCEPTION);
            }
        }

        if(claims.get("id") == null || claims.get("sub") == null || claims.get("exp") == null){
            if(tokenType.equals(TokenType.ACCESS)){
                throw new NonCriticalException(ErrorMessage.ACCESSTOKEN_INVALID_EXCEPTION);

            } else{
                throw new NonCriticalException(ErrorMessage.REFRESHTOKEN_INVALID_EXCEPTION);
            }
        }

        return claims;
    }
}
