package org.wpy.myspring.springframework.beans;

public class BeanPostProcessor {
	public Object PostProcessBeforeInitialization(Object obj,String beanName){
		return obj;
	}
	
	public Object postProcessAfterInitialization(Object obj,String beanName){
		return obj;
	}
}
