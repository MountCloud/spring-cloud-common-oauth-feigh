package org.mountcloud.springcloud.common.oauth2feigh.permission;

import java.util.Collection;

import org.aopalliance.intercept.MethodInvocation;
import org.mountcloud.springcloud.common.oauth2feigh.util.SecurityUtil;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdvice;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdviceVoter;
import org.springframework.security.core.Authentication;

/**
  * @author zhanghaishan
  * @version V1.0
  *
  * TODO: PreInvocationAuthorizationAdviceVoter 验证器添加系统用户验证
  * 2020/1/17.
  */
public class OauthPreInvocationAuthorizationAdviceVoter extends PreInvocationAuthorizationAdviceVoter{
	
	public OauthPreInvocationAuthorizationAdviceVoter(PreInvocationAuthorizationAdvice pre) {
		super(pre);
	}

	@Override
	public int vote(Authentication authentication, MethodInvocation method, Collection<ConfigAttribute> attributes) {
		//系统权限级别最高
		if(SecurityUtil.isSystemUser()) {
			return ACCESS_GRANTED;
		}
		return super.vote(authentication, method, attributes);
	}

}
