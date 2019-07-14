package org.wpy.myspring.springframework;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Test {
	public static void main(String[] args) {
		String[] arr1=new String[]{"a","b"};
		String[] arr2=new String[]{"c","d"};
		Map<String,String[]> map=new HashMap<String,String[]>();
		map.put("arr1", arr1);
//		map.put("arr2", arr2);
		for(Map.Entry<String, String[]> param:map.entrySet()){
			System.out.println(param);
			System.out.println(param.getValue());
			System.out.println(Arrays.toString(param.getValue()));
			String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll("\\s", "");
			System.out.println(value);
		}
	}
}
