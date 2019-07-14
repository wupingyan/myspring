package org.wpy.myspring.springframework.webmvc.servlet;

import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HandlerAdapter {
	private Map<String,Integer> paramMapping;
	
	public HandlerAdapter(Map<String,Integer> paramMapping){
		this.paramMapping=paramMapping;
	}
	
	/**
	 * 根据用户请求的参数信息，跟method中的参数动态匹配
	 * @param req
	 * @param resp resp目的只有一个，给方法传参数
	 * @param handlerMapping
	 * @return
	 * @throws Exception
	 */
	public ModelAndView handler(HttpServletRequest req,HttpServletResponse resp,
			HandlerMapping handlerMapping)throws Exception{
		Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();
		//拿到自定义命名参数所在位置
		Map<String,String[]> reqParameterMap = req.getParameterMap();
		//构造实参列表
		Object[] paramValues=new Object[paramTypes.length];
		
		for(Map.Entry<String, String[]> param:reqParameterMap.entrySet()){
			//String arr=["a"]   \s空白字符
			//value最终及是获取a
			String value=Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll("\\s", "");
			if(!paramMapping.containsKey(param.getKey())){
				continue;
			}
			int index = this.paramMapping.get(param.getKey());
			paramValues[index]=caseStringValue(value,paramTypes[index]);
		}
		
		if(this.paramMapping.containsKey(HttpServletRequest.class.getName())){
			int index=this.paramMapping.get(HttpServletRequest.class.getName());
			paramValues[index]=req;
		}
		if(this.paramMapping.containsKey(HttpServletResponse.class.getName())){
			int index=this.paramMapping.get(HttpServletRequest.class.getName());
			paramValues[index]=resp;
		}
		
		//从handlerMapping中获取method，反射调用
		Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
		if(null==result){
			return null;
		}
		boolean isModelAndView =handlerMapping.getMethod().getReturnType()==ModelAndView.class;
		if(isModelAndView){
			return (ModelAndView)result;
		}else{
			return null;
		}
	}
	
	private Object caseStringValue(String value,Class<?> clazz){
		if(String.class==clazz){
			return value;
		}else if(Integer.class==clazz){
			return Integer.valueOf(value);
		}else if(int.class==clazz){
			return Integer.valueOf(value).intValue();
		}else{
			return null;
		}
	}
}
