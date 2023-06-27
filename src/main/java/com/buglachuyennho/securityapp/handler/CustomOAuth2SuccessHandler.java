package com.buglachuyennho.securityapp.handler;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.buglachuyennho.securityapp.exception.BadRequestException;
import com.buglachuyennho.securityapp.security.config.AppProperties;
import com.buglachuyennho.securityapp.service.oauth2.OAuth2UserDetailCustom;
import com.buglachuyennho.securityapp.service.oauth2.security.HttpCookieOAuth2AuthorizationRequestRepository;
import com.buglachuyennho.securityapp.util.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.buglachuyennho.securityapp.service.oauth2.security.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private String SECRET_KEY = "472B4B6250655367566B5970337336763979244226452948404D635166546A57";
    private AppProperties appProperties;
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Autowired
    CustomOAuth2SuccessHandler(HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository, AppProperties appProperties) {
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
        this.appProperties = appProperties;
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {


        OAuth2UserDetailCustom user = (OAuth2UserDetailCustom)authentication.getPrincipal();
        System.out.println(authentication.getPrincipal());
        System.out.println(user.getAuthorities());
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());
        String access_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60* 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
        String refresh_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 86400000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
        response.setHeader("access_token", access_token);
        response.setHeader("refresh_token", refresh_token);
//        Map<String, Object> responseMap = new HashMap<>();
//        responseMap.put("access_token", access_token);
//        responseMap.put("user", user);
        //tokens.put("refresh_token", refresh_token);

        // Add a session cookie
        Cookie sessionCookie = new Cookie( "java_jwt", refresh_token);
        sessionCookie.setMaxAge(60 * 60 * 24 * 30 * 6);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(true);
        sessionCookie.setDomain(".localhost:8080");

        String targetUrl = determineTargetUrl(request, response, authentication, access_token);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
        System.out.println(sessionCookie);
        clearAuthenticationAttributes(request, response);
//        response.addCookie( sessionCookie );
        response.sendRedirect(targetUrl);

//        response.setContentType(APPLICATION_JSON_VALUE); zz
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication, String accessToken) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

//        String token = tokenProvider.createToken(authentication);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("access_token", accessToken)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies( request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return appProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    log.info(authorizedRedirectUri);
                    // Only validate host and port. Let the clients use different paths if they want to
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                        return true;
                    }
                    return false;
                });
    }
}
