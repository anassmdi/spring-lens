package org.springlens.springlens;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring-lens")
public class SpringLensProperties {
    /**
     * Enable or disable the Spring Lens, true by default.
     */
    private boolean enabled = true;

    /**
     * Custom path to SpringLens, /spring-lens by default
     */
    private String path = "/spring-lens";
    /**
     * Custom password, admin by default.
     */
    private String password = "admin";
}
