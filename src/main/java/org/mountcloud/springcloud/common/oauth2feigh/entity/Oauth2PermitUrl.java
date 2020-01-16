/**
 * 
 */
package org.mountcloud.springcloud.common.oauth2feigh.entity;

/**
 * @author zhanghaishan
 * @version V1.0
 * org.mountcloud.mvc.common.oauth2feigh.config
 * TODO:
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
