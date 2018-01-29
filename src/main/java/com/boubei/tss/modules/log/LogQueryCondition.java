package com.boubei.tss.modules.log;

import java.util.Date;
import java.util.Map;

import com.boubei.tss.framework.persistence.pagequery.MacrocodeQueryCondition;
import com.boubei.tss.util.DateUtil;

/** 
 * 日志的查询条件类
 */
public class LogQueryCondition extends MacrocodeQueryCondition {
    
	private String  operateTable;    // 操作对象    
	private String  operationCode;   // 操作类型
	 
    private String  operatorName;  
    private String  operatorIP;
    
    // 开始时间  -- 结束时间
    private Date    operateTimeFrom = DateUtil.subDays(new Date(), 30);
    private Date    operateTimeTo;
    
    private String  content;
    
	public Map<String, Object> getConditionMacrocodes() {
		Map<String, Object> map = super.getConditionMacrocodes(); ;
        map.put("${operateTable}",  " and o.operateTable  like :operateTable");
        map.put("${operationCode}", " and o.operationCode like :operationCode");
        map.put("${operatorName}",  " and o.operatorName  = :operatorName");
        map.put("${operatorIP}",    " and o.operatorIP    = :operatorIP");
        
        map.put("${operateTimeFrom}", " and o.operateTime >= :operateTimeFrom");
        map.put("${operateTimeTo}",   " and o.operateTime <= :operateTimeTo");
        
        map.put("${content}",   " and o.content like :content");
        
        return map;
	}
	
	protected String getCreatorIdField() {
		return "o.operatorId";
	}
 
    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorIP() {
        return operatorIP;
    }

    public void setOperatorIP(String operatorIP) {
        this.operatorIP = operatorIP;
    }

    public Date getOperateTimeFrom() {
        return operateTimeFrom;
    }

    public void setOperateTimeFrom(Date operateTimeFrom) {
        this.operateTimeFrom = operateTimeFrom;
    }

    public Date getOperateTimeTo() {
        return operateTimeTo;
    }

    public void setOperateTimeTo(Date operateTimeTo) {
        this.operateTimeTo = operateTimeTo;
    }

    public String getOperationCode() {
    	if(operationCode != null){
    		operationCode = "%" + operationCode.trim() + "%";           
        }
        return operationCode;
    }

    public void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
    }

    public String getOperateTable() {
    	if(operateTable != null){
    		operateTable = "%" + operateTable.trim() + "%";           
        }
        return operateTable;
    }

    public void setOperateTable(String operateTable) {
        this.operateTable = operateTable;
    }

	public String getContent() {
		if(content != null){
			content = "%" + content.trim() + "%";           
        }
        return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}