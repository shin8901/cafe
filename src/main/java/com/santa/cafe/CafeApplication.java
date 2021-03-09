package com.santa.cafe;

import com.santa.cafe.beverage.BeverageRepository;
import com.santa.cafe.beverage.BeverageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;

@EnableSwagger2
@EnableFeignClients
@SpringBootApplication
public class CafeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CafeApplication.class, args);
	}

	@PostConstruct
	public void insertInitialData() {
	}
}
