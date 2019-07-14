package org.wpy.myspring.springframework.beans;

public class BeanWrapper {
	private Object originalInstance;
	private Object wrappedInstance;
	//支持事件响应
	private BeanPostProcessor postProcessor;
	
	public BeanWrapper(Object instance){
		this.originalInstance=instance;
		this.wrappedInstance=instance;
	}
	
	public Object getOriginalInstance() {
		return originalInstance;
	}



	public void setOriginalInstance(Object originalInstance) {
		this.originalInstance = originalInstance;
	}



	public Object getWrappedInstance() {
		return wrappedInstance;
	}



	public void setWrappedInstance(Object wrappedInstance) {
		this.wrappedInstance = wrappedInstance;
	}



	public BeanPostProcessor getPostProcessor() {
		return postProcessor;
	}



	public void setPostProcessor(BeanPostProcessor postProcessor) {
		this.postProcessor = postProcessor;
	}



	//返回代理后的class
	public Class<?> getWrappedClass(){
		return this.wrappedInstance.getClass();
	}
	
	
}
