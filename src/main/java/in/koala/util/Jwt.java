package in.koala.util;

import in.koala.enums.ErrorMessage;
import in.koala.enums.TokenType;
import in.koala.exception.NonCriticalException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
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
        payloads.put("sub", "");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        if( tokenType.equals(TokenType.ACCESS)) {
            calendar.add(Calendar.HOUR_OF_DAY, 24);
        }
        else if(tokenType.equals(TokenType.REFRESH)){
            calendar.add(Calendar.DAY_OF_YEAR, 14);
        }
        Date exp = calendar.getTime();

        return Jwts.builder().setHeader(headers).setClaims(payloads).setExpiration(exp).signWith(SignatureAlgorithm.HS256, key.getBytes()).compact();
    }

    public boolean isValid(String token){

        if(token == null) throw new NonCriticalException(ErrorMessage.JWT_NOT_EXIST);
        if(!token.startsWith("Bearer ")) throw new NonCriticalException(ErrorMessage.JWT_NOT_START_BEARER);

        String jwt = token.substring(8);

        //Jwts.parser()
        return true;
    }
}
