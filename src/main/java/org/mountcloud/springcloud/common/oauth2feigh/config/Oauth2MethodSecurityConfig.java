package org.mountcloud.springcloud.common.oauth2feigh.config;

import java.util.ArrayList;
import java.util.List;

import org.mountcloud.springcloud.common.oauth2feigh.permission.Oauth2SystemRoleMethodSecurityExpressionHandler;
import org.mountcloud.springcloud.common.oauth2feigh.permission.OauthAuthenticatedVoter;
import org.mountcloud.springcloud.common.oauth2feigh.permission.OauthJsr250Voter;
import org.mountcloud.springcloud.common.oauth2feigh.permission.OauthPreInvocationAuthorizationAdviceVoter;
import org.mountcloud.springcloud.common.oauth2feigh.permission.OauthRoleVoter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.expression.method.ExpressionBasedPreInvocationAdvice;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
  * @author zhanghaishan
  * @version V1.0
  *
  * TODO: oauth方法验证配置，主要是加一些特殊权限，比如角色登记划分，设置最高权限
  * 2020/1/17.
  */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true,jsr250Enabled = true,securedEnabled = true) // 开启方法权限注解
public class Oauth2MethodSecurityConfig extends GlobalMethodSecurityConfiguration{
	
	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
		MethodSecurityExpressionHandler methodSecurityExpressionHandler = new Oauth2SystemRoleMethodSecurityExpressionHandler();
		return methodSecurityExpressionHandler;
	}
	
	@Override
	protected AccessDecisionManager accessDecisionManager() {
		List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<AccessDecisionVoter<? extends Object>>();
		ExpressionBasedPreInvocationAdvice expressionAdvice = new ExpressionBasedPreInvocationAdvice();
		expressionAdvice.setExpressionHandler(getExpressionHandler());

//		vote()方法的返回结果会是AccessDecisionVoter中定义的三个常量之一。
//		ACCESS_GRANTED表示同意，ACCESS_DENIED表示返回，ACCESS_ABSTAIN表示弃权。
//		如果一个AccessDecisionVoter不能判定当前Authentication是否拥有访问对应受保护对象的权限，则其vote()方法的返回值应当为弃权ACCESS_ABSTAIN。
//		int ACCESS_GRANTED = 1;
//		int ACCESS_ABSTAIN = 0;
//		int ACCESS_DENIED = -1;
		decisionVoters.add(new OauthPreInvocationAuthorizationAdviceVoter(expressionAdvice));
		decisionVoters.add(new OauthJsr250Voter());
		decisionVoters.add(new OauthRoleVoter());
		decisionVoters.add(new OauthAuthenticatedVoter());
		return new AffirmativeBased(decisionVoters);
	}
}
