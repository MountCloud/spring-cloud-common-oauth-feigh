package org.mountcloud.springcloud.common.oauth2feigh;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mountcloud.springcloud.common.oauth2feigh.config.OauthResourceServerConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(OauthResourceServerConfiguration.class)
@EnableResourceServer
public @interface EnableOauthResourceServer {

}