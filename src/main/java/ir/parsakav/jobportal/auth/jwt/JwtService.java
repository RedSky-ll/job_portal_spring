package ir.parsakav.jobportal.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.expiration-minutes:120}") private long expirationMinutes;

    private Key key;

    @PostConstruct
    void init() {
        // HS256 نیاز به کلید >= 256 بیت (حداقل 32 بایت) دارد
        byte[] raw = secret.getBytes(StandardCharsets.UTF_8);
        if (raw.length < 32) {
            throw new IllegalStateException(
                    "jwt.secret is too short for HS256. Provide at least 32 bytes (256 bits). " +
                            "Current bytes: " + raw.length);
        }
        key = Keys.hmacShaKeyFor(raw);
    }

    /** ساخت توکن از ایمیل و نقش مشخص */
    public String generateToken(String email, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(email)
                .addClaims(Map.of("role", role))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expirationMinutes * 60)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** اوورلود مناسب برای SuccessHandler/سایر جاها */
    public String generateToken(UserDetails ud) {
        String role = resolveRole(ud.getAuthorities());
        return generateToken(ud.getUsername(), role);
    }

    /** استخراج ایمیل/نام‌کاربری از توکن */
    public String extractUsername(String token) {
        return parse(token).getBody().getSubject();
    }

    /** استخراج نقش از توکن (claim: role) */
    public String extractRole(String token) {
        Object r = parse(token).getBody().get("role");
        return r == null ? "" : r.toString();
    }

    /** اعتبارسنجی پایهٔ توکن */
    public boolean isTokenValid(String token, UserDetails ud) {
        Claims c = parse(token).getBody();
        return ud.getUsername().equals(c.getSubject()) && c.getExpiration().after(new Date());
    }

    private String resolveRole(Collection<? extends GrantedAuthority> auths) {
        if (auths == null) return "";
        for (GrantedAuthority a : auths) {
            if (a == null) continue;
            String r = a.getAuthority();
            if (r != null && !r.isBlank()) return r.replaceFirst("^ROLE_", "");
        }
        return "";
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}
