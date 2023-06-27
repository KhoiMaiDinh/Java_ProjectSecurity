//package com.buglachuyennho.chatapp.security.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
//import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
//
//@Configuration
//@RequiredArgsConstructor
//public class ValidationConfig {
//
//    @Bean
//    public ValidatingMongoEventListener validatingMongoEventListener(
//            final LocalValidatorFactoryBean factory) {
//        return new ValidatingMongoEventListener(factory);
//    }
//}