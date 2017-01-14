package com.boubei.tss.demo.crud;

import java.util.HashMap;
import java.util.Map;

import com.boubei.tss.framework.persistence.pagequery.MacrocodeQueryCondition;

public class DemoSO extends MacrocodeQueryCondition {
    
	private String  name;
	private String  code; 
	private Long stateId;
	 
    private String  udf1;  
    private String  udf2;
    private String  udf3;
    
	public Map<String, Object> getConditionMacrocodes() {
		Map<String, Object> map = new HashMap<String, Object>() ;
        map.put("${name}", " and o.name = :name");
        map.put("${code}", " and o.code = :code");
        map.put("${stateId}", " and o.state.id = :stateId");
        
        map.put("${udf1}", " and o.udf1 = :udf1");
        map.put("${udf2}", " and o.udf2 like :udf2");
        map.put("${udf3}", " and o.udf3 > :udf3");
        
        return map;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUdf1() {
		return udf1;
	}

	public void setUdf1(String udf1) {
		this.udf1 = udf1;
	}

	public String getUdf2() {
		if(udf2 != null){
			udf2 = "%" + udf2.trim() + "%";           
        }
		return udf2;
	}

	public void setUdf2(String udf2) {
		this.udf2 = udf2;
	}

	public String getUdf3() {
		return udf3;
	}

	public void setUdf3(String udf3) {
		this.udf3 = udf3;
	}

	public Long getStateId() {
		return stateId;
	}

	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}
}
