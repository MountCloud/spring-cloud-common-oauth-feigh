package org.mountcloud.springcloud.common.oauth2feigh.permission;

import org.mountcloud.springcloud.common.oauth2feigh.config.RoleConfig;
import org.mountcloud.springcloud.mvc.common.config.ApplicationContextConfig;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

/**
  * @author zhanghaishan
  * @version V1.0
  *
  * TODO: 设置方法验证的一些配置，默认是系统用户最高，然后是管理员，然后是用户，最后是未登录用户，如果想自定义的话注册一个RoleHierarchy就可以了。
  * 2020/1/17.
  */
public class Oauth2SystemRoleMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler{

	private static RoleHierarchyImpl defaultRoleHierarchy = new RoleHierarchyImpl();

	static {
		defaultRoleHierarchy = new RoleHierarchyImpl();
		String roleStr = RoleConfig.SYSTEM_ROLE+" > ROLE_ADMIN\r\n"+
				"ROLE_ADMIN > ROLE_USER\r\n"+
				"ROLE_USER > ROLE_ANONYMOUS";
		defaultRoleHierarchy.setHierarchy(roleStr);
	}

	/**
	 *
	 * @return
	 */
	@Override
	protected RoleHierarchy getRoleHierarchy() {

		RoleHierarchy roleHierarchyImpl = ApplicationContextConfig.getBean(RoleHierarchy.class);
		if(roleHierarchyImpl!=null){
			return roleHierarchyImpl;
		}

		return defaultRoleHierarchy;
	}
	
}
