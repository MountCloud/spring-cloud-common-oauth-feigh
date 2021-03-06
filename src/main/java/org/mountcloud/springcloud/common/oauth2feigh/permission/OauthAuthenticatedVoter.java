/**
 * 
 */
package org.mountcloud.springcloud.common.oauth2feigh.permission;

import java.util.Collection;

import org.mountcloud.springcloud.common.oauth2feigh.util.SecurityUtil;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.core.Authentication;

/**
 * @author zhanghaishan
 * @version V1.0
 * TODO: AuthenticatedVoter 验证器，添加系统用户验证
 * 2020年1月13日.
 */
public class OauthAuthenticatedVoter extends AuthenticatedVoter{

	
	@Override
	public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
		//系统权限级别最高
		if(SecurityUtil.isSystemUser()) {
			return ACCESS_GRANTED;
		}
		return super.vote(authentication, object, attributes);
	}
	
}
