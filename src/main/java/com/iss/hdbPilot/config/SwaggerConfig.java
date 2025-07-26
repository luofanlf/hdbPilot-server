package com.iss.hdbPilot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置类
 * 用于配置API文档的基本信息
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HdbPilot Server API")
                        .version("1.0.0")
                        .description("HdbPilot Server 后端API接口文档")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("luofan036@gmail.com")
                                .url("https://github.com/luofanlf/hdbPilot-server"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
    }
} 