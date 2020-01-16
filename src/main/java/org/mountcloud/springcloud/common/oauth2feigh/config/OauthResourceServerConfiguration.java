package org.mountcloud.springcloud.common.oauth2feigh.config;

import java.util.Map;

import org.mountcloud.springcloud.common.oauth2feigh.entity.Oauth2PermitUrl;
import org.mountcloud.springcloud.common.oauth2feigh.service.Oauth2UserInfoTokenServices;
import org.mountcloud.springcloud.mvc.common.config.ApplicationContextConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

/**
  * @author zhanghaishan
  * @version V1.0
  *
  * TODO: oauth资源服务的配置，用于配置资源服务的安全策略，我将这个类的order设置成了101，如果有自己特殊的配置需要设置order在101之前
  * 2020/1/17.
  */
@Order(101)
public final class OauthResourceServerConfiguration extends ResourceServerConfigurerAdapter{

	@Autowired
	private ResourceServerProperties sso;
	
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorizeRequests = http.authorizeRequests();

		try{
			Map<String, Oauth2PermitUrl> urls = ApplicationContextConfig.getApplicationContext().getBeansOfType(Oauth2PermitUrl.class);
			if(urls!=null&&urls.size()>0) {
				for(String key : urls.keySet()) {
					Oauth2PermitUrl oauth2PermitUrl = urls.get(key);
					if(oauth2PermitUrl.getUrls()!=null&&oauth2PermitUrl.getUrls().length>0) {
						authorizeRequests.antMatchers(oauth2PermitUrl.getUrls());
					}
				}
			}
		}catch (BeansException ex){
		}

		authorizeRequests.antMatchers("/actuator/**").permitAll();
		authorizeRequests.anyRequest().authenticated().and().httpBasic();
	}
	
	
	@Bean
	public ResourceServerTokenServices myUserInfoTokenServices() {
	    return new Oauth2UserInfoTokenServices(sso.getUserInfoUri(), sso.getClientId());
	}
	
    @Override
    public void configure(ResourceServerSecurityConfigurer config) {
        config.tokenServices(myUserInfoTokenServices());
    }

	
}
