package org.wpy.myspring.springframework.beans;

public class BeanDefinition {
	private String factoryBeanName;//容器中的bean名称    默认类名小写
	private String beanClassName;//bean的全限定类名
	private boolean lazyInit=false;
	public String getFactoryBeanName() {
		return factoryBeanName;
	}
	public void setFactoryBeanName(String factoryBeanName) {
		this.factoryBeanName = factoryBeanName;
	}
	public String getBeanClassName() {
		return beanClassName;
	}
	public void setBeanClassName(String beanClassName) {
		this.beanClassName = beanClassName;
	}
	public boolean isLazyInit() {
		return lazyInit;
	}
	public void setLazyInit(boolean lazyInit) {
		this.lazyInit = lazyInit;
	}
	@Override
	public String toString() {
		return "BeanDefinition [factoryBeanName=" + factoryBeanName + ", beanClassName=" + beanClassName + ", lazyInit="
				+ lazyInit + "]";
	}
	
	
}
