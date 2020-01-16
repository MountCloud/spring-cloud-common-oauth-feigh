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
		decisionVoters.add(new OauthPreInvocationAuthorizationAdviceVoter(expressionAdvice));
		decisionVoters.add(new OauthJsr250Voter());
		decisionVoters.add(new OauthRoleVoter());
		decisionVoters.add(new OauthAuthenticatedVoter());
		return new AffirmativeBased(decisionVoters);
	}
}
