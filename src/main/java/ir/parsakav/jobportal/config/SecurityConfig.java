// ir/parsakav/jobportal/config/SecurityConfig.java
package ir.parsakav.jobportal.config;

import ir.parsakav.jobportal.auth.jwt.JwtAuthFilter;
import ir.parsakav.jobportal.auth.jwt.JwtService;
import ir.parsakav.jobportal.auth.security.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtFilter;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService; // برای ساخت توکن در success handler

    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    DaoAuthenticationProvider authProvider() {
        var p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/**", "/_debug/**", "/h2/**").permitAll()
                        // OAuth2 endpoints
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        // وب‌سوکت اگه داری:
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/uploads/**").permitAll()

                        // پابلیک
                        .requestMatchers(HttpMethod.GET, "/jobs/**").permitAll()
                        .anyRequest().authenticated()
                )
                // برای بقیهٔ مسیرها همچنان JSON بده
                .exceptionHandling(e -> e
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/oauth2/authorization/google"),
                                new AntPathRequestMatcher("/oauth2/**")
                        )
                        .authenticationEntryPoint((req, res, ex) -> {
                            res.setStatus(401);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"error\":\"unauthorized\"}");
                        })
                        .accessDeniedHandler((req, res, ex) -> {
                            res.setStatus(403);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"error\":\"forbidden\"}");
                        })
                )
                .authenticationProvider(authProvider())
                // OAuth2 login: success → JWT بساز و به فرانت برگرد
                .oauth2Login(o -> o
                        .authorizationEndpoint(a -> a.baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(r -> r.baseUri("/login/oauth2/code/*"))
                        .successHandler((req, res, auth) -> {
                            // principal می‌تونه DefaultOAuth2User باشه → ایمیل
                            var oauthUser = auth.getPrincipal();
                            String email = null;
                            if (oauthUser instanceof org.springframework.security.oauth2.core.user.OAuth2User ou) {
                                Object em = ou.getAttributes().get("email");
                                if (em != null) email = em.toString();
                            }
                            if (email == null) {
                                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Email not found from Google");
                                return;
                            }
                            var userDetails = userDetailsService.loadUserByUsername(email);
                            var jwt = jwtService.generateToken(userDetails);

                            // redirect_uri را از query بگیر (اگر فرستاده باشی)؛ وگرنه پیش‌فرض
                            String redirect = req.getParameter("redirect_uri");
                            if (redirect == null || redirect.isBlank()) {
                                // آدرس پیش‌فرض فرانت
                                redirect = "http://localhost:8080/#/oauth2/callback";
                            }
                            // توکن را به فرانت بده
                            String location = redirect + (redirect.contains("?") ? "&" : "?") + "token=" + jwt;
                            res.sendRedirect(location);
                        })
                        .failureHandler((req, res, ex) -> {
                            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth2 login failed");
                        })
                );

        // فیلتر JWT برای بقیهٔ درخواست‌ها
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
