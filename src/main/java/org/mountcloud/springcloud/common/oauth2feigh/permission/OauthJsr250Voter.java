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
 * TODO: Jsr250Voter验证器添加系统用户验证
 * 2020年1月13日.
 */
public class OauthJsr250Voter extends Jsr250Voter{

	@Override
	public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> definition) {
		//系统权限级别最高
		if(SecurityUtil.isSystemUser()) {
			return ACCESS_GRANTED;
		}
		return super.vote(authentication, object, definition);
	}

}
