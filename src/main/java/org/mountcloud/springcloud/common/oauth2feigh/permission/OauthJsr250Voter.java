/**
 * 
 */
package org.mountcloud.springcloud.common.oauth2feigh.permission;

import java.util.Collection;

import org.mountcloud.springcloud.common.oauth2feigh.util.SecurityUtil;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.annotation.Jsr250Voter;
import org.springframework.security.core.Authentication;

/**
 * @author zhanghaishan
 * @version V1.0
 * org.mountcloud.mvc.common.oauth2feigh.permission
 * TODO:
 * 2020年1月13日.
 */
public class OauthJsr250Voter extends Jsr250Voter{
	
	
	/**
		vote()方法的返回结果会是AccessDecisionVoter中定义的三个常量之一。
		ACCESS_GRANTED表示同意，ACCESS_DENIED表示返回，ACCESS_ABSTAIN表示弃权。
		如果一个AccessDecisionVoter不能判定当前Authentication是否拥有访问对应受保护对象的权限，则其vote()方法的返回值应当为弃权ACCESS_ABSTAIN。
		int ACCESS_GRANTED = 1;
		int ACCESS_ABSTAIN = 0;
		int ACCESS_DENIED = -1;
	 */
	@Override
	public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> definition) {
		//系统权限级别最高
		if(SecurityUtil.isSystemUser()) {
			return ACCESS_GRANTED;
		}
		return super.vote(authentication, object, definition);
	}

}
