/**
 * 
 */
package org.mountcloud.springcloud.common.oauth2feigh.util;

import java.util.Collection;

import org.mountcloud.springcloud.common.oauth2feigh.config.RoleConfig;
import org.mountcloud.springcloud.common.oauth2feigh.entity.BaseUserDetails;
import org.mountcloud.springproject.common.util.StringUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author zhanghaishan
 * @version V1.0
 * org.mountcloud.mvc.common.oauth2feigh.util
 * TODO:
 * 2020年1月13日.
 */
public class SecurityUtil {
	
	public static BaseUserDetails getLoginUser() {
		BaseUserDetails baseUserDetails = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication != null) {
			Object principal = authentication.getPrincipal();
			if(principal!=null&&principal instanceof BaseUserDetails) {
				baseUserDetails = (BaseUserDetails) principal;
			}
		}
		return baseUserDetails;
	}
	
	public static boolean isSystemUser() {
		BaseUserDetails baseUserDetails = getLoginUser();
		if(baseUserDetails!=null) {
			 Collection<? extends GrantedAuthority> galist = baseUserDetails.getAuthorities();
			 if(galist!=null&&galist.size()>0) {
				 for(GrantedAuthority ga : galist) {
					 if(StringUtil.equals(ga.getAuthority(), RoleConfig.SYSTEM_ROLE)) {
						 return true;
					 }
				 }
			 }
		}
		return false;
	}

}
