package org.wpy.myspring.springframework.webmvc.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class HandlerMapping {
	private Object controller;
	private Method method;
	private Pattern url; //url正则
	
	public HandlerMapping(Object controller, Method method, Pattern url) {
		this.controller = controller;
		this.method = method;
		this.url = url;
	}
	public Object getController() {
		return controller;
	}
	public void setController(Object controller) {
		this.controller = controller;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public Pattern getUrl() {
		return url;
	}
	public void setUrl(Pattern url) {
		this.url = url;
	}
	
	
}
