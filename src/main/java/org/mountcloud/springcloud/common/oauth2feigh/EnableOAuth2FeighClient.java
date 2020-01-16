package org.mountcloud.springcloud.common.oauth2feigh;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mountcloud.springcloud.common.oauth2feigh.config.Oauth2FeighClientConfig;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

/**
 * @author zhanghaishan
 * @version V1.0
 * TODO: 开启具有Oauth2的Feigh客户端
 * 2020年1月8日.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableOAuth2Client
@Import(Oauth2FeighClientConfig.class)
public @interface EnableOAuth2FeighClient {

}
