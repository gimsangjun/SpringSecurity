package io.security.basicSecurity.jwt;

import io.jsonwebtoken.*;
import io.security.basicSecurity.Entity.User;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@NoArgsConstructor // 인자가 없는 생성자를 만들어 주는 어노테이션
// 토큰과 관련된 요청을 처리 , https://mangkyu.tistory.com/57
public final class JwtTokenUtils {

    private static final String secretKey = "ThisIsA_SecretKeyForJwtExample";

    /**
     * 토큰의 구조 https://aonee.tistory.com/70
     * Header : JWT 토큰의 유형이나 사용된 해시 알고리즘의 정보
     * Payload : 클레임(Claims)을 포함. 즉, 클라이언트에 대한 정보
     *      사용자에 대한 속성 (Key,Value)형태로 이루어져있음.
     *      무조건 따라야하는것은 아니지만 권장.
     *      iss(issuer : 토큰발행자), exp(expiration time,토큰 만료시간), sub(subject, 토큰 제목), aud(audience,토큰 대상자) 와 같은 클레임들이 있음.
     * Signature : Header와 Payload를 secret Key로 담는다 -> 서명을 통해 메세지가 중간에 변경되지 않았다는 것을 증명.
     */
    public static String createToken(User user){
        JwtBuilder builder = Jwts.builder()
                .setSubject(user.getUsername())
                .setHeader(createHeader())
                .setClaims(createClaims(user))
                .setExpiration(createExpireDateForOneYear())
                .signWith(SignatureAlgorithm.HS256,createSigningKey());

        return builder.compact();

    }

    public static boolean isValidToken(String token){
        try{
            Claims claims = getClaimsFormToken(token);
            log.info("expireTime : "+claims.getExpiration());
            log.info("email : " + claims.get("username"));
            log.info("role :" + claims.get("role"));
            return true;
        } catch (ExpiredJwtException expiredJwtException){
            log.error("Token Expired");
            return false;
        } catch (JwtException exception){
            log.error("Token Tampered"); // 변조됨.
            return false;
        } catch (NullPointerException exception){
            log.error("Token is null");
            return false;
        }
    }

    // Header : JWT 토큰의 유형이나 사용된 해시 알고리즘의 정보
    private static Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();

        header.put("typ","JWT");
        header.put("alg","HS256");
        header.put("regDate", System.currentTimeMillis());

        return header;
    }

    // User 정보를 바탕으로 Claim 만들기
    private static Map<String, Object> createClaims(User user) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("username",user.getUsername());
        claims.put("nickname",user.getNickname());
        claims.put("authorities",user.getAuthorities());

        return claims;
    }

    private static Key createSigningKey() {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        return new SecretKeySpec(apiKeySecretBytes,SignatureAlgorithm.HS256.getJcaName());
    }

    // 토큰 만료시간 설정
    private static Date createExpireDateForOneYear() {
        // 토큰 만료시간 30일로 지정.
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 30);
        return c.getTime();

    }

    private static Claims getClaimsFormToken(String token) {
        return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey)).parseClaimsJws(token).getBody();
    }

    private static String getUsernameFormToken(String token){
        Claims claims = getClaimsFormToken(token);
        return (String) claims.get("username");
    }

    private static String getNicknameFormToken(String token){
        Claims claims = getClaimsFormToken(token);
        return (String) claims.get("nickname");
    }

    // user의 권한도 가져올수 있게
}
