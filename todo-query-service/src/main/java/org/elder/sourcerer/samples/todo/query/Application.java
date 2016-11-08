package org.elder.sourcerer.samples.todo.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import io.swagger.annotations.Api;
import org.elder.sourcerer.samples.todo.query.converters.JsonConverter;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.sql.DataSource;

@SpringBootApplication
@EnableAutoConfiguration
@EnableSpringDataWebSupport
@EnableSwagger2
@ComponentScan("org.elder.sourcerer.samples.todo.query")
public class Application {
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JsonConverter.setObjectMapper(mapper);
        return mapper;
    }

    @Bean
    public Docket customDocket() {
        ApiInfo apiInfo = new ApiInfo(
                "TODO Query Service",
                "REST service implementation for querying TODO items",
                "1.0.0",
                null,
                new Contact(
                        "Elder Developers",
                        "https://www.github.com/elder-oss",
                        "tech@elder.org"),
                null,
                null);

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .build()
                .apiInfo(apiInfo);
    }

    @Bean
    DataSource getDataSource(
            @Value("${spring.datasource.url}") final String url,
            @Value("${spring.datasource.username}") final String username,
            @Value("${spring.datasource.password}") final String password) {
        // Spring auto configures an embedded database with its own url when one is detected,
        // explicitly create the h2 data source here to force it to use our settings :/
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setURL(url);
        jdbcDataSource.setUser(username);
        jdbcDataSource.setPassword(password);
        return jdbcDataSource;
    }
}
