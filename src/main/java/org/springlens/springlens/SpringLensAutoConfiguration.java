package org.springlens.springlens;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(SpringLensProperties.class)
@ConditionalOnProperty(prefix = "spring-lens", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SpringLensAutoConfiguration {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SpringLensAutoConfiguration.class);

    @PostConstruct
    public void init() {
        logger.info("[SpringLens] SpringLens Auto Configuration initialized");
    }

    @Bean
    public SpringLensController springLensController() {
        return new SpringLensController();
    }

    @Bean
    public boolean configureSpringLensAppender() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

        SpringLensAppender springLensAppender = new SpringLensAppender();
        springLensAppender.setContext(context);
        springLensAppender.setName("SPRING_LENS_APPENDER");
        springLensAppender.start();

        rootLogger.addAppender(springLensAppender);
        return true;
    }
}
