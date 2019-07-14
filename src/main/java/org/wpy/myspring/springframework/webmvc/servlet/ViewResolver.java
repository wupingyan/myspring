package org.wpy.myspring.springframework.webmvc.servlet;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewResolver {
	private String viewName;
	private File templateFile;
	
	public ViewResolver(String viewName,File templateFile){
		this.viewName=viewName;
		this.templateFile=templateFile;
	}
	
	public String viewResolver(ModelAndView mav) throws Exception{
		StringBuffer sb=new StringBuffer();
		RandomAccessFile ra=new RandomAccessFile(this.templateFile,"r");
		try{
			String line=null;
			while((line=ra.readLine())!=null){
				line=new String(line.getBytes("ISO-8859-1"),"UTF-8");
				Matcher matcher=matcher(line);
				while(matcher.find()){
					for(int i=0;i<matcher.groupCount();i++){
						//把${xx}中间的名称取出来
						String paramName=matcher.group(i);
						Object paramValue = mav.getModel().get(paramName);
						if(paramValue==null){
							continue;
						}
						line=line.replaceAll("$\\{"+paramValue+"\\}", String.valueOf(paramValue));
						line=new String(line.getBytes("UTF-8"),"ISO-8859-1");
					}
					sb.append(line);
				}
			}
		}finally{
			ra.close();
		}
		return null;
	}

	private Matcher matcher(String str) {
		Pattern pattern=Pattern.compile("$\\{(.+?)\\}",Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
		return matcher;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public File getTemplateFile() {
		return templateFile;
	}

	public void setTemplateFile(File templateFile) {
		this.templateFile = templateFile;
	}
	
	
}
