package com.buglachuyennho.securityapp;

import com.buglachuyennho.securityapp.security.config.AppProperties;
import com.buglachuyennho.securityapp.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class SecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api").allowedOrigins("http://localhost:3000");
			}
		};
	}

	@Bean
	CommandLineRunner runner(UserService userService) {
		return args -> {
//			userService.saveRole(new Role("ROLE_USER"));
//			userService.saveRole(new Role("ROLE_MANAGER"));
//			userService.saveRole(new Role("ROLE_ADMIN"));
//			userService.saveRole(new Role("ROLE_SUPER_ADMIN"));
//
			//userService.saveUser(new User("Dinh","Khoi mai", "khoimai", "1234", new ArrayList<>()));
			//userService.saveUser(new User("Tran","Quoc Anh", "quocanh", "1234", new ArrayList<Role>()));

			//userService.addRoleToUser("khoimai", "ROLE_USER");
//			userService.addRoleToUser("khoimai", "ROLE_MANAGER");
//			userService.addRoleToUser("quocanh", "ROLE_USER");


		};
	}

}
