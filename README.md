# USE 使用
##  USE spring cloud common ，使用spring cloud common方式
```
<parent>
  <groupId>org.mountcloud</groupId>
  <artifactId>spring-cloud-common-parent</artifactId>
  <version>2.2.1.RELEASE-Hoxton.RELEASE-1.1</version>
</parent>
<dependency>
  <groupId>org.mountcloud</groupId>
  <artifactId>spring-cloud-mvc-common</artifactId>
</dependency>
```
## OR Use alone，或者单独引用。
```
<dependency>
  <groupId>org.mountcloud</groupId>
  <artifactId>spring-cloud-mvc-common</artifactId>
  <version>1.1</version>
</dependency>
```
## Open Functions 开启功能
 此注解将为feigh开启oauth鉴权
```
@EnableOAuth2FeighClient

需要添加配置文件,need config：

security:
   oauth2:
      client:
         client-id: clientid
         client-secret: clientsecret
         access-token-uri: ${oauthurl:http://127.0.0.1:8904}/api/auth/oauth/token
         user-authorization-uri: ${oauthurl:http://127.0.0.1:8904}/api/auth/oauth/authorize
         user-logout-uri: ${oauthurl:http://127.0.0.1:8904}/api/auth/oauth/logout
         grant-type: client_credentials
```
此注解将启用OauthResource鉴权
```
@EnableOauthResourceServer

需要添加配置文件,need config：

security:
   oauth2:
      resource:
         filter-order: 50
         id: ${spring.application.name}
         #这个接口需要返回（need return data formate）——org.mountcloud.springproject.common.result.RestfulResult<org.mountcloud.springcloud.common.oauth2feigh.entity.BaseUserDetails>
         user-info-uri: ${oauthurl:http://127.0.0.1:8904}/api/auth/user/me
         prefer-token-info: false
```

# 描述
  每个SpringCloud服务（除了Gateway这些特殊服务）都需要对接oauth进行鉴权，有可能也需要提供接口供其他服务使用或者客户端使用（Resource），使用过程中也是需要鉴权，而feigh在调用其他服务接口时也是需要进行鉴权，而且这些工作在每个服务中都是重复性的工作，于是我将这些业务提取了出来作为了一个单独的模块。
  
# 约束
  使用此模块需要遵循这些约束：
  
  引入此模块后，ROLE_SYSTEM是系统级权限，权限最高，会忽略掉一切权限验证，除非项目中做了调整，这样做的目的是为了让feigh享有最高权限，权限的管理不放在feigh处而是放在暴露api的Controller来管理权限，下面是默认的权限关系。
```
ROLE_SYSTEM > ROLE_ADMIN
ROLE_ADMIN > ROLE_USER
```
  
  （可选或者自定义）为feigh提供ROLE_SYSTEM，可以参看下面的代码。
  
  Oauth Resource（security.oauth2.resource.user-info-uri此项配置）用token获取用户信息的接口必须返回此格式的内容：
 
```
org.mountcloud.springproject.common.result.RestfulResult<org.mountcloud.springcloud.common.oauth2feigh.entity.BaseUserDetails>
```
  因为此模块中，resource解析用户信息业务获取权限部分针对此类型做了适配，也是约束rest接口均返回RestfulResult的一种方式。
  
  参考的代码片段（demo）
```
@RestController
@RequestMapping("/user")
public class OauthUserController {
	
	@Autowired
	private OauthClientService oauthClientService;
	

	/**
	 * 查看token用户详情
	 * @return RestfulResult BaseUserDetails
	 */
	@GetMapping("/me")
	public RestfulResult<BaseUserDetails> getMe() {
		
		RestfulResult<BaseUserDetails> restfulResult = new RestfulResult<BaseUserDetails>();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication instanceof OAuth2Authentication) {
			
			OAuth2Authentication oAuth2Auth = (OAuth2Authentication) authentication;
			OAuth2Request request = oAuth2Auth.getOAuth2Request();
			
			String clientId = request.getClientId();

			authentication = oAuth2Auth.getUserAuthentication();
			
			//获取登录上来的用户权限,这个方法里取数据来自于UserDetailsService
			if (authentication instanceof UsernamePasswordAuthenticationToken) {
				Object principal = authentication.getPrincipal();
				if (principal instanceof BaseUserDetails) {
					restfulResult.setData((BaseUserDetails)principal);
				}
			}
			
			//如果不是通过账号密码登录的用户，则获取client的权限
			if(restfulResult.getData()==null) {
				
				//（避免看不懂）此步骤等价于：select * from oauthclient where clientId = ?
				OauthClient client = oauthClientService.findClientByClientId(clientId);
				
				BaseUserDetails baseUserDetails = new BaseUserDetails();
				baseUserDetails.setUsername(clientId);
				
				//client.getAuthorities() 是 String类型，值为（例子）: "ROLE_USER,ROLE_CTO"
				List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(client.getAuthorities());
				baseUserDetails.setAuthorities(grantedAuthorities);
				
				restfulResult.setData(baseUserDetails);
			}
		}

		return restfulResult;
	}

}
```
所以security.oauth2.resource.user-info-uri配置为：http://${oauthserver}/user/me
