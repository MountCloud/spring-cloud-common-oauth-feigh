package org.mountcloud.springcloud.common.oauth2feigh.config;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

import feign.FeignException;
import feign.RequestInterceptor;
import feign.Response;
import feign.Response.Body;
import feign.RetryableException;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;

/**
 * @author zhanghaishan
 * @version V1.0
 * TODO: Feigh客户端oauth配置
 * 2020年1月8日.
 */
public class Oauth2FeighClientConfig {
    // feign的OAuth2ClientContext
    private OAuth2ClientContext feignOAuth2ClientContext =  new DefaultOAuth2ClientContext();
    
    @Autowired
    private ClientCredentialsResourceDetails clientCredentialsResourceDetails;

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;
    
    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor(){
        return new OAuth2FeignRequestInterceptor(feignOAuth2ClientContext, clientCredentialsResourceDetails);
    }

    
    //尝试配置
    @Bean
    public Retryer retryer() {
    	return new Retryer.Default(100, 1000, 3);
    }
    
    @Bean
    public Decoder feignDecoder() {
    	return new Oauth2FeighClientResponseEntityDecoder(feignOAuth2ClientContext, new SpringDecoder(messageConverters));
    }
    
    @Bean
    public ErrorDecoder errorDecoder() {
    	return new Oauth2FeighClientErrorDecoder(feignOAuth2ClientContext);
    }
    
    
    //自定义feigh的返回解码器，如果有特殊需要可以放在这里
    class Oauth2FeighClientResponseEntityDecoder extends ResponseEntityDecoder{
    	
    	private OAuth2ClientContext oAuth2ClientContext;

		public Oauth2FeighClientResponseEntityDecoder(OAuth2ClientContext oAuth2ClientContext,Decoder decoder) {
			super(decoder);
			this.oAuth2ClientContext = oAuth2ClientContext;
		}
    	
		@Override
		public Object decode(Response response, Type type) throws IOException, FeignException {
			
			//状态码
			int status = response.status();
			//内容
			Body body = response.body();
			
			return super.decode(response, type);
		}
    	
    }
    
    
    //自定义feign的错误码解码器，比如404、401、403之类的，我需要对401进行操作，因为我的框架中feign使用的鉴权client是绝对信任的client，但是如果redis清空库或者redis中的token过期了，没有刷新会导致调用子接口401
    class Oauth2FeighClientErrorDecoder implements ErrorDecoder{
    	
    	private OAuth2ClientContext oAuth2ClientContext;
    	
    	public Oauth2FeighClientErrorDecoder(OAuth2ClientContext oAuth2ClientContext) {
			// TODO Auto-generated constructor stub
    		this.oAuth2ClientContext = oAuth2ClientContext;
		}

		@Override
		public Exception decode(String methodKey, Response response) {
			
			//在此处对错误码进行操作
			//如果是401的话，设置空token并且重试
			if(HttpStatus.UNAUTHORIZED.value()==response.status()) {
				oAuth2ClientContext.setAccessToken(null);
				return new RetryableException(response.status(), "Token validation failed", response.request().httpMethod(), new Date(), response.request());
			}
			
			return FeignException.errorStatus(methodKey, response);
		}
    	
    }
}
