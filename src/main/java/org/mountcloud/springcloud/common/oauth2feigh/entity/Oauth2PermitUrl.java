/**
 * 
 */
package org.mountcloud.springcloud.common.oauth2feigh.entity;

/**
 * @author zhanghaishan
 * @version V1.0
 * TODO:此类主要是方便Permit url，使用的话仅需要实例化它并且注解@Bean，urls写需要permit的url
 * 2020年1月15日.
 */
public class Oauth2PermitUrl {
	
	private String[] urls;
	
	public Oauth2PermitUrl(String ...urls) {
		this.urls = urls;
	}

	public String[] getUrls() {
		return urls;
	}

	public void setUrls(String[] urls) {
		this.urls = urls;
	}
}
