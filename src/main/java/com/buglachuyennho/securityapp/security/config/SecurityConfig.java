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

    // cors config
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("https://localhost:3000");
        config.addAllowedHeader("*");
        config.addExposedHeader(HttpHeaders.AUTHORIZATION);
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(); // enables Cross-Origin Resource Sharing
        http.csrf().disable(); // disables Cross-Site Request Forgery (enable if needed)
        http.sessionManagement().sessionCreationPolicy(STATELESS); //  sets the session creation policy to STATELESS. In a stateless application, session management is not needed

        http.authorizeHttpRequests().requestMatchers("/api/login/**", "/api/v1/token/refresh/**", "/api/v1/register/**", "/oauth2/**").permitAll(); // permits access to specific paths without authentication

        http.authorizeHttpRequests().requestMatchers("/api/user/**").hasAnyAuthority("ROLE_USER"); // restricts access to the "/api/user/**" path to only users with the "ROLE_USER" authority
        http.authorizeHttpRequests().anyRequest().authenticated(); // requires authentication for all other endpoints or paths.
        http.apply(customDsl()); // applies additional custom security configurations(authentication and authorization) based on the defined DSL.
        http.oauth2Login() // oauth2 login configuration
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
