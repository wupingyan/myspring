package org.wpy.myspring.springframework.webmvc.servlet;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wpy.myspring.springframework.annotation.Controller;
import org.wpy.myspring.springframework.annotation.RequestMapping;
import org.wpy.myspring.springframework.annotation.RequestParam;
import org.wpy.myspring.springframework.context.ApplicationContext;

public class DispatcherServlet extends HttpServlet{
	
	private static final String CONFIG_LOCATION="contextConfigLocation";
	
	private List<HandlerMapping> handlerMapping=new ArrayList<HandlerMapping>();
	
	private Map<HandlerMapping,HandlerAdapter> handlerAdapter=new HashMap<HandlerMapping,HandlerAdapter>();

	private List<ViewResolver> viewResolver=new ArrayList<ViewResolver>();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try{
			doDispatcher(req,resp);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		//根据用户请求获取一个handler
		HandlerMapping handlerMapping=getHandler(req);
		if(handlerMapping==null){
			resp.getWriter().write("404 not found!");
			return;
		}
		HandlerAdapter handerAdapter=getHandlerAdapter(handlerMapping);
		//调用方法，得到返回值
		ModelAndView mv = handerAdapter.handler(req, resp, handlerMapping);
		
		//真正的返回输出
		processDispatchResult(resp,mv);
	}

	private void processDispatchResult(HttpServletResponse resp, ModelAndView mv) throws Exception {
		if(mv==null){
			return;
		}
		if(this.viewResolver.isEmpty()){
			return;
		}
		for(ViewResolver viewResolver:this.viewResolver){
			if(!mv.getViewName().equals(viewResolver.getViewName())){
				continue;
			}
			String out=viewResolver.viewResolver(mv);
			if(out !=null){
				resp.getWriter().write(out);
				break;
			}
		}
	}

	private HandlerAdapter getHandlerAdapter(HandlerMapping handlerMapping) {
		if(this.handlerAdapter.isEmpty()){
			return null;
		}
		return this.handlerAdapter.get(handlerMapping);
	}

	private HandlerMapping getHandler(HttpServletRequest req) {
		if(this.handlerMapping.isEmpty()){
			return null;
		}
		String url=req.getRequestURI();
		String contextPath=req.getContextPath();
		url=url.replace(contextPath, "").replace("/+", "/");
		for(HandlerMapping hm:this.handlerMapping){
			Matcher matcher=hm.getUrl().matcher(url);
			if(matcher.matches()){
				return hm;
			}
		}
		return null;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ApplicationContext context=new ApplicationContext(config.getInitParameter(CONFIG_LOCATION));
		initStrategies(context);
	}

	private void initStrategies(ApplicationContext context) {
		initMultipartResolver(context);
		initLocaleResolver(context);
		initThemeResolver(context);
		initHandlerMappings(context);
		initHandlerAdpaters(context);
		initHandlerExceptionResolver(context);
		initRequestToViewNameTranslator(context);
		initViewResolvers(context);
		initFlashMapManager(context);
	}

	private void initFlashMapManager(ApplicationContext context) {
	}

	private void initViewResolvers(ApplicationContext context) {
		
	}

	private void initRequestToViewNameTranslator(ApplicationContext context) {
		
	}

	private void initHandlerExceptionResolver(ApplicationContext context) {
		
	}

	private void initHandlerAdpaters(ApplicationContext context) {
		for(HandlerMapping handlerMapping:this.handlerMapping){
			//形参列表
			Map<String,Integer> paramMapping=new HashMap<String,Integer>();
			//每一个参数可以有多个annotation,用二维数组
			Annotation[][] annotations=handlerMapping.getMethod().getParameterAnnotations();
			
			//这里只处理了命名参数
			for(int i=0;i<annotations.length;i++){
				for(Annotation a:annotations[i]){
					if(a instanceof RequestParam){
						String paramName = ((RequestParam)a).value().trim();
						if(paramName !=""){
							paramMapping.put(paramName, i);
						}
					}
				}
			}
			
			//处理非命名参数，这里只处理request和response
			Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();
			for(int i=0;i<paramTypes.length;i++){
				Class<?> type=paramTypes[i];
				if(type==HttpServletRequest.class || type==HttpServletResponse.class){
					paramMapping.put(type.getName(), i);
				}
			}
			this.handlerAdapter.put(handlerMapping, new HandlerAdapter(paramMapping));
		}
	}
	
	/**
	 * 将Controller中配置的RequestMapping和method一一对应
	 * @param context
	 */
	private void initHandlerMappings(ApplicationContext context) {
		//容器中的bean名称
		String[] beanDefinitionNames = context.getBeanDefinitionNames();
		for(String beanName:beanDefinitionNames){
			Object controller = context.getBean(beanName);
			Class<?> clazz = controller.getClass();
			if(!clazz.isAnnotationPresent(Controller.class)){
				continue;
			}
			String baseUrl="";
			if(clazz.isAnnotationPresent(RequestMapping.class)){
				baseUrl=clazz.getAnnotation(RequestMapping.class).value().trim();
			}
			
			Method[] methods=clazz.getMethods();
			for(Method method:methods){
				if(!method.isAnnotationPresent(RequestMapping.class)){
					continue;
				}
				String subUrl=method.getAnnotation(RequestMapping.class).value().trim();
				String regex=("/"+baseUrl+subUrl.replaceAll("\\*", ".*").replaceAll("/+", "/"));
				Pattern pattern=Pattern.compile(regex);
				this.handlerMapping.add(new HandlerMapping(controller,method,pattern));
				System.out.println("mapping: "+regex+" "+method);
			}
		}
	}

	private void initThemeResolver(ApplicationContext context) {
		
	}

	private void initLocaleResolver(ApplicationContext context) {
		
	}

	private void initMultipartResolver(ApplicationContext context) {
		
	}

}
