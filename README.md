# USE 使用
```
Application Main class add
@ComponentScan(basePackages={"you project package","org.mountcloud.springcloud"})
```
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
 此注解将为feigh开启oauth鉴权,This annotation will enable oauth authentication for the feigh.
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
此注解将启用OauthResource鉴权,This annotation will enable OauthResource authentication
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

# NOTE
   Every SpringCloud service (except these special services of Gateway) needs to authenticate with the oauth. It may also need to provide an interface for other services or clients to use (Resource). In the process of use, authentication is also required, and feigh calls other The service interface also needs to be authenticated, and these tasks are repetitive tasks in each service, so I extracted these services as a separate module.

# 描述
  每个SpringCloud服务（除了Gateway这些特殊服务）都需要对接oauth进行鉴权，有可能也需要提供接口供其他服务使用或者客户端使用（Resource），使用过程中也是需要鉴权，而feigh在调用其他服务接口时也是需要进行鉴权，而且这些工作在每个服务中都是重复性的工作，于是我将这些业务提取了出来作为了一个单独的模块。
  

# Constraints
  Use of this module requires these constraints:
  
  After the introduction of this module, ROLE_SYSTEM is a system-level permission. It has the highest permission and ignores all permission verifications. Unless adjustments are made in the project, the purpose of doing this is to allow the right to enjoy the highest permission. The management of permissions is not placed at the right. The controller that exposes the api to manage permissions, the following is the default permission relationship.
```
ROLE_SYSTEM> ROLE_ADMIN
ROLE_ADMIN> ROLE_USER
```
  
  (Optional or custom) Provide ROLE_SYSTEM for the feigh, see the code below.
  
  The Oauth Resource (security.oauth2.resource.user-info-uri configuration) interface that uses tokens to obtain user information must return content in this format:
 
```
org.mountcloud.springproject.common.result.RestfulResult <org.mountcloud.springcloud.common.oauth2feigh.entity.BaseUserDetails>
```
  Because in this module, the resource parsing user information service obtaining permission part is adapted for this type, and it is also a way to restrict the rest interfaces to return RestfulResult.

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
  
  
# security.oauth2.resource.user-info-uri code,security.oauth2.resource.user-info-uri代码
  The data types that security.oauth2.resource.user-info-uri needs to return are already stated in the constraints。
  约束中已经说明了security.oauth2.resource.user-info-uri需要返回的数据类型
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


# Files 文件列表
```
.
├── .gitignore
├── pom.xml
├── README.md
└── src
    ├── main
    │   └── java
    │       └── org
    │           └── mountcloud
    │               └── springcloud
    │                   └── common
    │                       └── oauth2feigh
    │                           ├── config
    │                           │   ├── Oauth2FeighClientConfig.java
    │                           │   ├── Oauth2MethodSecurityConfig.java
    │                           │   ├── OauthResourceServerConfiguration.java
    │                           │   └── RoleConfig.java
    │                           ├── EnableOAuth2FeighClient.java
    │                           ├── EnableOauthResourceServer.java
    │                           ├── entity
    │                           │   ├── BaseUserDetails.java
    │                           │   └── Oauth2PermitUrl.java
    │                           ├── permission
    │                           │   ├── Oauth2SystemRoleMethodSecurityExpressionHandler.java
    │                           │   ├── OauthAuthenticatedVoter.java
    │                           │   ├── OauthJsr250Voter.java
    │                           │   ├── OauthPreInvocationAuthorizationAdviceVoter.java
    │                           │   └── OauthRoleVoter.java
    │                           ├── service
    │                           │   └── Oauth2UserInfoTokenServices.java
    │                           └── util
    │                               └── SecurityUtil.java
    └── test
        └── java
            └── org
                └── mountcloud
                    └── springcloud
                        └── common
                            └── oauth2feigh
                                └── package-info.java
```
