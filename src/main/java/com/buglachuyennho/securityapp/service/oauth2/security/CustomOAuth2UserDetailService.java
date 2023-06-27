package com.buglachuyennho.securityapp.service.oauth2.security;

import com.buglachuyennho.securityapp.domain.Role;
import com.buglachuyennho.securityapp.domain.User;
import com.buglachuyennho.securityapp.exception.BaseException;
import com.buglachuyennho.securityapp.repo.RoleRepo;
import com.buglachuyennho.securityapp.repo.UserRepo;
import com.buglachuyennho.securityapp.service.oauth2.OAuth2UserDetailCustom;
import com.buglachuyennho.securityapp.service.oauth2.OAuth2UserDetailFactory;
import com.buglachuyennho.securityapp.service.oauth2.OAuth2UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserDetailService extends DefaultOAuth2UserService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private OAuth2User checkingOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserDetails oAuth2UserDetails =
                OAuth2UserDetailFactory.getOAuth2UserDetail(
                        oAuth2UserRequest.getClientRegistration().getRegistrationId(),
                        oAuth2User.getAttributes());

        if (ObjectUtils.isEmpty(oAuth2UserDetails)) {
            throw new BaseException("400", "Can not found oauth2 user from properties");
        }

        Optional<User> user = userRepo.findUserByUsernameAndProvider(
                oAuth2UserDetails.getEmail(),
                oAuth2UserRequest.getClientRegistration().getRegistrationId());
        User userDetail;
        if (user.isPresent()) {
            userDetail = user.get();

            if (!userDetail.getProvider().
                    equals(oAuth2UserRequest.getClientRegistration().getRegistrationId())) {
                throw new BaseException("400", "Ivalid site login with " + userDetail.getProvider());
            }

            userDetail = updateOAuth2UserDetail(userDetail, oAuth2UserDetails);
        } else {
            userDetail = registerNewOAuth2UserDetail(oAuth2UserRequest, oAuth2UserDetails);
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        userDetail.getRoles().forEach(role -> {
            authorities.add(
                    new SimpleGrantedAuthority(role.getName())
            );
        });
        System.out.println(authorities);
        OAuth2UserDetailCustom oAuth2UserDetailCustom =  new OAuth2UserDetailCustom(
                userDetail.getId(),
                userDetail.getUsername(),
                userDetail.getPassword(),
                authorities
        );
        return oAuth2UserDetailCustom;
    }

    public User registerNewOAuth2UserDetail(OAuth2UserRequest oAuth2UserRequest, OAuth2UserDetails oAuth2UserDetails) {
        User user = new User();
        user.setUsername(oAuth2UserDetails.getEmail());
        user.setProvider(oAuth2UserRequest.getClientRegistration().getRegistrationId());
        user.setAvatarUrl(oAuth2UserDetails.getImageUrl());
        user.setName(oAuth2UserDetails.getName());
        Role role = roleRepo.findRoleByName("ROLE_USER").orElse(null);
        user.getRoles().add(role);
        return userRepo.save(user);
    }

    public User updateOAuth2UserDetail(User user, OAuth2UserDetails oAuth2UserDetails) {
        user.setUsername(oAuth2UserDetails.getEmail());
        user.setAvatarUrl(oAuth2UserDetails.getImageUrl());
        user.setName(oAuth2UserDetails.getName());
        return userRepo.save(user);
    }
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return checkingOAuth2User(userRequest, oAuth2User);
        }
        catch (AuthenticationException e) {
            throw e;
        }
        catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }
}
