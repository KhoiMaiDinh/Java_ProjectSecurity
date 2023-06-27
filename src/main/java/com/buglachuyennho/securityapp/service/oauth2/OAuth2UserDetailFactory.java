package com.buglachuyennho.securityapp.service.oauth2;

import com.buglachuyennho.securityapp.domain.AuthProvider;
import com.buglachuyennho.securityapp.exception.BaseException;

import java.util.Map;

public class OAuth2UserDetailFactory {
    public static OAuth2UserDetails getOAuth2UserDetail(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equals(AuthProvider.facebook.name()))
            return new OAuth2FacebookUser(attributes);
        else
            throw new BaseException("400", "Sorry! Login with " + registrationId + " is  not supported");
    }
}
