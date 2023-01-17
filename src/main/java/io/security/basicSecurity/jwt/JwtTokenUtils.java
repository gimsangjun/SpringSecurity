package io.security.basicSecurity.jwt;

import io.jsonwebtoken.*;
import io.security.basicSecurity.app.user.domain.MyUserDetails;
import io.security.basicSecurity.app.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
// 토큰과 관련된 요청을 처리 , https://mangkyu.tistory.com/57
public class JwtTokenUtils {

    private static final long JWT_TOKEN_VALIDITY =  5*60*60;

    @Value("${jwt.secret}")
    private String secret;

    /**
     * 토큰의 구조
     * {Header}.{Payload}.{Verify Signature} 와 같은 형태로 사용
     * Header : JWT 토큰의 유형이나 사용된 해시알고리즘의 정보가 들어간다
     *      "alg" : "HS256"
     *      "typ" : "JWT"
     * Payload : 클레임(Claims)을 포함한다. 즉, 클라이언트에 대한 정보가 들어가 있다.
     *      "sub: 토큰 제목 (subject)"
     * Signature : Header 와 Payload를 secret Key로 담는다. -> 서명을 통해 메세지가 중간에 변경되지 않았다는 것을 증명
     */

    // Email이 JWT의 subject(제목)이다.
    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Boolean ignoreTokenExpiration(String token) {
        // here you specify tokens, for that the expiration is ignored
        return false;
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>(); // 나중에 따로 추가가능.
        return doGenerateToken(claims, user.getEmail());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY*1000)).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public Boolean canTokenBeRefreshed(String token) {
        return (!isTokenExpired(token) || ignoreTokenExpiration(token));
    }

    public Boolean validateToken(String token, MyUserDetails userDetails) {
        final String email = getEmailFromToken(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token)); // 여기서 Username은 Email를 나타내도록 override했다.
    }

    /**
     * @param header : request헤더에서 키 값이 Authorization인 Value
     * @return 토큰값의 헤더에 Key : "Authorization", Value = "Bearer 토큰값" 이렇게 들어올텐데,
     *          Bearer를 자르고 토큰값만
     */
    public static String getTokenFromHeader(String header) {
        return header.split(" ")[1];
    }

    // TODO: 토큰헤더 추가해야됨
    /**
     *      "alg" : "HS256"
     *      "typ" : "JWT"
      */
}
