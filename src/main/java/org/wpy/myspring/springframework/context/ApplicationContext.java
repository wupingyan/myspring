package org.wpy.myspring.springframework.context;

import java.lang.reflect.Field;
import java.security.Provider.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.wpy.myspring.springframework.annotation.Autowired;
import org.wpy.myspring.springframework.annotation.Controller;
import org.wpy.myspring.springframework.beans.BeanDefinition;
import org.wpy.myspring.springframework.beans.BeanPostProcessor;
import org.wpy.myspring.springframework.beans.BeanWrapper;
import org.wpy.myspring.springframework.context.support.BeanDefinitionReader;
import org.wpy.myspring.springframework.core.BeanFactory;

public class ApplicationContext implements BeanFactory{
	private String[] configLocations;
	private BeanDefinitionReader reader;
	
	private Map<String,BeanDefinition> beanDefinitionMap=new ConcurrentHashMap<String,BeanDefinition>();
	
	private Map<String,BeanWrapper> beanWrapperMap=new HashMap<String,BeanWrapper>();
	
	//保证注册单例式
	private Map<String,Object> beanCacheMap=new HashMap<String,Object>();
	
	public ApplicationContext(String... configLocations){
		this.configLocations=configLocations;
		refresh();
	}
	
	public void refresh(){
		this.reader=new BeanDefinitionReader(this.configLocations);
		//得到全限定类名
		List<String> beanDefinitions = reader.loadBeanDefinitions();
		doRegistry(beanDefinitions);
		doAutowired();
	}

	private void doAutowired() {
		for(Map.Entry<String, BeanDefinition> entry:this.beanDefinitionMap.entrySet()){
			String beanName = entry.getKey();
			//非懒加载需要初始化
			if(!this.beanDefinitionMap.get(beanName).isLazyInit()){
				getBean(beanName);
			}
		}
	}
	
	@Override
	public Object getBean(String beanName) {
		BeanDefinition beanDefinition=this.beanDefinitionMap.get(beanName);
		BeanPostProcessor postProcessor=new BeanPostProcessor();
		Object instance=instantionBean(beanDefinition);
		if(instance == null){
			return null;
		}
		//实例初始化之前调用一次
		postProcessor.PostProcessBeforeInitialization(instance, beanName);
		
		BeanWrapper wrapperBean=new BeanWrapper(instance);
		wrapperBean.setPostProcessor(postProcessor);
		this.beanWrapperMap.put(beanName, wrapperBean);
		
		//实例初始化之后在调一次
		postProcessor.postProcessAfterInitialization(instance, beanName);
		populateBean(beanName,instance);
		return this.beanWrapperMap.get(beanName).getWrappedInstance();
		
	}

	@SuppressWarnings({"unchecked","rawtypes"})
	private void populateBean(String beanName, Object instance) {
		Class clazz = instance.getClass();
		if(!(clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(Service.class))){
			return;
		}
		try{
			Field[] fields = clazz.getDeclaredFields();
			for(Field field:fields){
				if(!field.isAnnotationPresent(Autowired.class)){
					continue;
				}
				Autowired autowired = field.getAnnotation(Autowired.class);
				String autowiredName = autowired.values().trim();
				if("".equals(autowiredName)){
					autowiredName=field.getType().getName();
				}
				field.setAccessible(true);
				if(this.beanWrapperMap.containsKey(autowiredName)){
					field.set(instance, this.beanWrapperMap.get(autowiredName).getWrappedInstance());
				}else{
					this.getBean(autowiredName);
					field.set(instance, this.beanWrapperMap.get(autowiredName).getWrappedInstance());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	//传一个beanDefinition 返回一个bean实例
	private Object instantionBean(BeanDefinition beanDefinition) {
		Object instance=null;
		String className = beanDefinition.getBeanClassName();
		try{
			if(this.beanCacheMap.containsKey(className)){
				instance=this.beanCacheMap.get(className);
			}else{
				Class<?> clazz=Class.forName(className);
				instance=clazz.newInstance();
				this.beanCacheMap.put(className, instance);
			}
			return instance;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private void doRegistry(List<String> beanDefinitions) {
		try{
			for(String className:beanDefinitions){
				Class<?> clazz = Class.forName(className);
				//接口不能实例化
				if(clazz.isInterface()){
					continue;
				}
				BeanDefinition beanDefinition=reader.registerBean(className);
				if(beanDefinition != null){
					//factoryBeanName=>controller
					this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
				}
				//获取该类实现的接口
				Class<?>[] interfaces = clazz.getInterfaces();
				for(Class<?>i:interfaces){
					System.out.println(i.getName());
					this.beanCacheMap.put(i.getName(), beanDefinition);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String[] getBeanDefinitionNames(){
		return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
	}
	
	public Properties getConfig(){
		return this.reader.getConfig();
	}
}
