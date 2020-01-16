package org.mountcloud.springcloud.common.oauth2feigh.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mountcloud.springcloud.common.oauth2feigh.entity.BaseUserDetails;
import org.mountcloud.springproject.common.util.*;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
  * @author zhanghaishan
  * @version V1.0
  *
  * TODO: oauth资源服务的token服务，这里认为获取token信息的借口返回的是RestfResult，data为BaseUserDetails类型
  * 2020/1/17.
  */
public class Oauth2UserInfoTokenServices extends UserInfoTokenServices{

	public Oauth2UserInfoTokenServices(String userInfoEndpointUrl, String clientId) {
		super(userInfoEndpointUrl, clientId);
	}
	
	@Override
	public OAuth2Authentication loadAuthentication(String accessToken)
			throws AuthenticationException, InvalidTokenException {
		LoggerUtil.getLogger(Oauth2UserInfoTokenServices.class).debug("loadAuthentication:"+accessToken);
		return super.loadAuthentication(accessToken);
	}

	
	
	@Override
	protected Object getPrincipal(Map<String, Object> map) {
		LoggerUtil.getLogger(Oauth2UserInfoTokenServices.class).debug("getPrincipal"+ GsonUtil.GsonString(map));
		BaseUserDetails baseUserDetails = new BaseUserDetails();

		baseUserDetails.setUserId(StringUtil.toLong(MapUtil.getVal(map, "data.userId")));
		baseUserDetails.setUsername(StringUtil.toString(MapUtil.getVal(map, "data.username")));
		List authorities = ListUtil.toList(MapUtil.getVal(map, "data.authorities"));

		//必须将权限放到map里，外层会取这个权限
		map.put("authorities",authorities);

		List<GrantedAuthority> grantedAuthoritys = new ArrayList<GrantedAuthority>();
		if(authorities!=null) {
			authorities.forEach((a)->{
				Map amap = MapUtil.toMap(a);
				String arantedAuthorityStr = StringUtil.toString(MapUtil.getVal(amap, "authority"));
				if(arantedAuthorityStr!=null) {
					SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(arantedAuthorityStr);
					grantedAuthoritys.add(simpleGrantedAuthority);
				}
			});
		}
		baseUserDetails.setAuthorities(grantedAuthoritys);
		baseUserDetails.setAccountNonExpired(StringUtil.toBoolean(MapUtil.getVal(map, "data.accountNonExpired")));
		baseUserDetails.setAccountNonLocked(StringUtil.toBoolean(MapUtil.getVal(map, "data.accountNonLocked")));
		baseUserDetails.setCredentialsNonExpired(StringUtil.toBoolean(MapUtil.getVal(map, "data.credentialsNonExpired")));
		baseUserDetails.setEnabled(StringUtil.toBoolean(MapUtil.getVal(map, "data.enabled")));
		
		return baseUserDetails;
	}

}
