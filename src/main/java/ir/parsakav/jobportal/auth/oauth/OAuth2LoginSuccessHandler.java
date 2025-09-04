package ir.parsakav.jobportal.auth.oauth;

import ir.parsakav.jobportal.accounts.domain.User;
import ir.parsakav.jobportal.accounts.service.AccountsService;
import ir.parsakav.jobportal.auth.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// auth/oauth/OAuth2LoginSuccessHandler.java
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final AccountsService accountsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String email = ((DefaultOAuth2User) token.getPrincipal()).getAttribute("email");
        User user = accountsService.ensureUserFromGoogle(email); // اگر نبود بساز، role=SEEKER
        String jwt = jwtService.generateToken(user.getEmail(), user.getRole().name());
        res.sendRedirect("/login/success?token=" + jwt); // فرانت بگیرد و ذخیره کند
    }
}
