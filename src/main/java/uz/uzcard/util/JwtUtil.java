package uz.uzcard.util;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import uz.uzcard.dto.JwtDTO;
import uz.uzcard.exception.AppBadRequestException;

import java.util.Date;

@Slf4j
public class JwtUtil {

    private static final String secretKey = "Lock";

    private static final long AFTER_REGISTRATION = 7;

    private static final long BEFORE_REGISTRATION = 3;

    public static String encodeBeforeRegistration(String id) {
        return encode(id, BEFORE_REGISTRATION);
    }

    public static String encodeAfterRegistration(String id) {
        return encode(id, AFTER_REGISTRATION);
    }

    private static String encode(String subject, long time) {
        log.info("JWT Creating {}", subject);

        JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.setSubject(subject);
        jwtBuilder.setIssuedAt(new Date());
        jwtBuilder.signWith(SignatureAlgorithm.HS256, secretKey);

        if (time == AFTER_REGISTRATION) {
            jwtBuilder.setExpiration(new Date(System.currentTimeMillis() + (60 * 60 * 1000 * 24 * AFTER_REGISTRATION))); // for a week
        } else if (time == BEFORE_REGISTRATION) {
            jwtBuilder.setExpiration(new Date(System.currentTimeMillis() + (BEFORE_REGISTRATION * 60 * 1000))); // for 3 minutes
        }

        return jwtBuilder.compact();
    }

    public static JwtDTO decode(String token) {
        try {
            JwtParser jwtParser = Jwts.parser();

            jwtParser.setSigningKey(secretKey);
            Jws jws = jwtParser.parseClaimsJws(token);

            Claims claims = (Claims) jws.getBody();

            return new JwtDTO(claims.getSubject());

        } catch (JwtException e) {
            log.warn("JWT Invalid {}", token);
            throw new AppBadRequestException("JWT Invalid!");
        }
    }

}
