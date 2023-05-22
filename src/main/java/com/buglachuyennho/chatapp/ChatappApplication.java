package com.buglachuyennho.chatapp;

import com.buglachuyennho.chatapp.domain.Role;
import com.buglachuyennho.chatapp.domain.User;
import com.buglachuyennho.chatapp.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

@SpringBootApplication
public class ChatappApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatappApplication.class, args);
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
