package com.ysy.jt809;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Administrator
 */

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class Jt809Application {

	public static void main(String[] args) {
		SpringApplication.run(Jt809Application.class, args);
	}

}
