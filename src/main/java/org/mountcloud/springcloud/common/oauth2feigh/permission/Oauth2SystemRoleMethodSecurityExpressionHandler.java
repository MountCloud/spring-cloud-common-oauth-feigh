package org.mountcloud.springcloud.common.oauth2feigh.permission;

import org.mountcloud.springcloud.common.oauth2feigh.config.RoleConfig;
import org.mountcloud.springcloud.mvc.common.config.ApplicationContextConfig;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

//不验证系统用户
public class Oauth2SystemRoleMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler{

	private static RoleHierarchyImpl defaultRoleHierarchy = new RoleHierarchyImpl();

	static {
		defaultRoleHierarchy = new RoleHierarchyImpl();
		String roleStr = RoleConfig.SYSTEM_ROLE+" > ROLE_ADMIN\r\n"+
				"ROLE_ADMIN > ROLE_USER";
		defaultRoleHierarchy.setHierarchy(roleStr);
	}

	@Override
	protected RoleHierarchy getRoleHierarchy() {

		RoleHierarchyImpl roleHierarchyImpl = ApplicationContextConfig.getBean(RoleHierarchyImpl.class);
		if(roleHierarchyImpl!=null){
			return roleHierarchyImpl;
		}

		return defaultRoleHierarchy;
	}
	
}
