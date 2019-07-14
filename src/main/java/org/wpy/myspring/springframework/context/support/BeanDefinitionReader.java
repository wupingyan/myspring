package org.wpy.myspring.springframework.context.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.wpy.myspring.springframework.beans.BeanDefinition;

public class BeanDefinitionReader {
	private Properties config=new Properties();
	//List<全限定类名>
	private List<String> registerBeanClasses=new ArrayList<String>();
	private static final String SCAN_PACKAGE="scanPackage";
	
	public BeanDefinitionReader(String... locations){
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));
		try{
			config.load(is);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		doScanner(config.getProperty(SCAN_PACKAGE));
	}

	private void doScanner(String packageName) {
		URL url=this.getClass().getClassLoader().getResource("/"+packageName.replace("\\.", "/"));
		File classDir=new File(url.getFile());
		for(File file:classDir.listFiles()){
			if(file.isDirectory()){
				doScanner(packageName+"/"+file.getName());
			}else{
				registerBeanClasses.add(packageName+"."+file.getName().replace(".class", ""));
			}
		}
	}
	
	public List<String> loadBeanDefinitions(){
		return this.registerBeanClasses;
	}
	
	public BeanDefinition registerBean(String className){
		BeanDefinition beanDefinition=new BeanDefinition();
		beanDefinition.setBeanClassName(className);
		String lowerClassName = lowerFirstCase(className.substring(className.lastIndexOf(".")+1));
		beanDefinition.setFactoryBeanName(lowerClassName);
		return beanDefinition;
	}
	
	public Properties getConfig(){
		return this.config;
	}
	
	private String lowerFirstCase(String beanName){
		char[] chars = beanName.toCharArray();
		chars[0] +=32;
		return String.valueOf(chars);
	}
}
