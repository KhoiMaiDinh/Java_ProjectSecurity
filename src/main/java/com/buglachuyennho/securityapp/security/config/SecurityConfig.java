package com.buglachuyennho.securityapp.security.config;


import com.buglachuyennho.securityapp.handler.CustomOAuth2FailureHandler;
import com.buglachuyennho.securityapp.handler.CustomOAuth2SuccessHandler;
import com.buglachuyennho.securityapp.service.oauth2.security.CustomOAuth2UserDetailService;
import com.buglachuyennho.securityapp.service.oauth2.security.HttpCookieOAuth2AuthorizationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


import static com.buglachuyennho.securityapp.filter.MyCustomDsl.*;
import static org.springframework.security.config.http.SessionCreationPolicy.*;

@Configuration
@EnableWebSecurity
//@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private CustomOAuth2UserDetailService customOAuth2UserDetailService;
    @Autowired
    private CustomOAuth2FailureHandler customOAuth2FailureHandler;
    @Autowired
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000");  // TODO: lock down before deploying
        config.addAllowedHeader("*");
        config.addExposedHeader(HttpHeaders.AUTHORIZATION);
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors();
        http.csrf().disable();
//        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        http.sessionManagement().sessionCreationPolicy(STATELESS);

        http.authorizeHttpRequests().requestMatchers("/api/login/**", "/api/v1/token/refresh/**", "/api/v1/register/**", "/oauth2/**").permitAll();

        http.authorizeHttpRequests().requestMatchers("/api/user/**").hasAnyAuthority("ROLE_USER");
        http.authorizeHttpRequests().anyRequest().authenticated();
        http.apply(customDsl());
        http.oauth2Login()
                .authorizationEndpoint(authorization -> authorization
                        .baseUri("/oauth2/authorize")
                        .authorizationRequestRepository(cookieAuthorizationRequestRepository()))
                .redirectionEndpoint()
                    .baseUri("/oauth2/callback/*")
                    .and()
                .userInfoEndpoint()
                    .userService(customOAuth2UserDetailService)
                    .and()
                .successHandler(customOAuth2SuccessHandler)
                .failureHandler(customOAuth2FailureHandler);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

}
