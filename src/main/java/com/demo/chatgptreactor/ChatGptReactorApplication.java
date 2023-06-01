package com.demo.chatgptreactor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by syl nerosyl@live.com on 2023/5/14
 *
 * @author syl
 */
@EnableR2dbcRepositories
@EnableTransactionManagement
@SpringBootApplication
public class ChatGptReactorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatGptReactorApplication.class, args);
	}


//	@Bean
//	public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
//		return new PropertySourcesPlaceholderConfigurer();
//	}


}
