package com.ysy.oath;
import com.github.surpassm.EnableSecurity;
import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;


/**
 * @author mc
 * Create date 2019/2/13 10:35
 * Version 1.0
 * Description 启动类
 */


@EnableSecurity
@EnableSwagger2Doc
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = "com.liaoin.demo.mapper")
public class OathApplication {

	public static void main(String[] args) {
		SpringApplication.run(OathApplication.class, args);
	}

}
