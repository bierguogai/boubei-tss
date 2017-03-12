package com.boubei.tss.dm.record;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boubei.tss.cache.Cacheable;
import com.boubei.tss.cache.Pool;
import com.boubei.tss.cache.extension.CacheHelper;
import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.data.sqlquery.SQLExcutor;
import com.boubei.tss.dm.data.util.DataExport;
import com.boubei.tss.dm.record.ddl._Database;
import com.boubei.tss.dm.record.file.RecordAttach;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.exception.ExceptionEncoder;
import com.boubei.tss.framework.persistence.pagequery.PageInfo;
import com.boubei.tss.framework.web.dispaly.grid.DefaultGridNode;
import com.boubei.tss.framework.web.dispaly.grid.GridDataEncoder;
import com.boubei.tss.framework.web.dispaly.grid.IGridNode;
import com.boubei.tss.framework.web.mvc.BaseActionSupport;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;

/**
 * 小技巧：
 * 1、隐藏创建人、创建时间、修改人、修改时间、修改次数这5列，在过滤条件里加：1=1<#if 1=0>hideCUV</#if>
 *   needLog = false 时，也将自动隐藏这5列
 */
@Controller
@RequestMapping("/auth/xdata")
public class _Recorder extends BaseActionSupport {
	
	@Autowired RecordService recordService;
	
	_Database getDB(Long recordId) {
		Pool cache = CacheHelper.getLongCache();
		
		String cacheKey = "_db_record_" + recordId;
		Cacheable cacheItem = cache.getObject(cacheKey);
				
		_Database _db;
		if(cacheItem == null) {
			Record record = recordService.getRecord(recordId);
			
			cache.putObject(cacheKey, _db = _Database.getDB(record));
		}
		else{
			_db = (_Database) cacheItem.getValue();
		}
		
		return _db;
	}
	
	@RequestMapping("/define/{recordId}")
    @ResponseBody
    public Object getDefine(@PathVariable("recordId") Long recordId) {
		Record record = recordService.getRecord(recordId);
		if(!record.isActive()) {
			throw new BusinessException("该数据录入已被停用，无法再录入数据！");
		}
		
        return 
        	new Object[] { 
        		getDB(recordId).getFields(), 
        		record.getCustomizeJS(), 
        		record.getCustomizeGrid(),
        		record.getNeedFile()
        	};
    }
	
	public static final int PAGE_SIZE = 50;
	
    @RequestMapping("/xml/{recordId}/{page}")
    public void showAsGrid(HttpServletRequest request, HttpServletResponse response, 
            @PathVariable("recordId") Long recordId, 
            @PathVariable("page") int page) {
 
        _Database _db = getDB(recordId);
        
        SQLExcutor ex = _db.select(page, PAGE_SIZE, getRequestMap(request));
        
        // 读取记录的附件信息
        Map<Object, Object> itemAttach = new HashMap<Object, Object>();
        if(_db.needFile) {
        	String sql = "select itemId item, count(*) num from dm_record_attach where recordId = ? group by itemId";
			List<Map<String, Object>> attachResult = SQLExcutor.query(DMConstants.LOCAL_CONN_POOL, sql, recordId);
        	for(Map<String, Object> temp : attachResult) {
        		itemAttach.put(temp.get("item").toString(), temp.get("num"));
        	}
        }
        
        List<IGridNode> temp = new ArrayList<IGridNode>();
		for(Map<String, Object> item : ex.result) {
            DefaultGridNode gridNode = new DefaultGridNode();
            gridNode.getAttrs().putAll(item);
            
            Object itemId = item.get("id").toString();
            Object attachNum = itemAttach.get(itemId);
            if(attachNum != null) {
            	gridNode.getAttrs().put("fileNum", "<a href='javascript:void(0)' onclick='manageAttach(" + itemId + ")'>" + attachNum + "</a>");
            }
            
            temp.add(gridNode);
        }
        GridDataEncoder gEncoder = new GridDataEncoder(temp, _db.getGridTemplate());
        
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageSize(PAGE_SIZE);
        pageInfo.setTotalRows(ex.count);
        pageInfo.setPageNum(page);
        
        print(new String[] {"RecordData", "PageInfo"}, new Object[] {gEncoder, pageInfo});
    }
    
    @RequestMapping("/json/{recordId}/{page}")
    @ResponseBody
    public List<Map<String, Object>> showAsJSON(HttpServletRequest request, 
    		@PathVariable("recordId") Long recordId, @PathVariable("page") int page) {
    	
        _Database _db = getDB(recordId);
        return _db.select(page, PAGE_SIZE, getRequestMap(request)).result;
    }
	
    @RequestMapping(value = "/{recordId}", method = RequestMethod.POST)
    public void create(HttpServletRequest request, HttpServletResponse response, 
    		@PathVariable("recordId") Long recordId) {
    	
    	_Database _db = getDB(recordId);
    	try {
    		_db.insert( getRequestMap(request) );
    	}
    	catch(Exception e) {
    		throwEx(e, _db + "里新增");
    	}
    	printSuccessMessage();
    }
    
    @RequestMapping(value = "/rid/{recordId}", method = RequestMethod.POST)
    @ResponseBody
    public Object createAndReturnID(HttpServletRequest request, @PathVariable("recordId") Long recordId) {
    	_Database _db = getDB(recordId);
    	Long newID = null;
    	try {
    		newID = _db.insertRID( getRequestMap(request) );
    	}
    	catch(Exception e) {
    		throwEx(e, _db + "里新增");
    	}
    	return newID;
    }
    
    private void throwEx(Exception e, String op) {
    	Throwable firstCause = ExceptionEncoder.getFirstCause(e);
		String errorMsg = "在" + op + "数据时出错了：" + firstCause;
		log.debug(errorMsg);
		throw new BusinessException(errorMsg);
    }
    
    @RequestMapping(value = "/{recordId}/{id}", method = RequestMethod.POST)
    public void update(HttpServletRequest request, HttpServletResponse response, 
    		@PathVariable("recordId") Long recordId, 
    		@PathVariable("id") Long id) {
    	
    	_Database _db = getDB(recordId);
    	try {
			_db.update(id, getRequestMap(request) );
    		printSuccessMessage();
    	}
    	catch(Exception e) {
    		throwEx(e, _db + "里修改");
    	}
    }
    
    /**
     * 批量更新选中记录行的某个字段值，用在批量审批等场景
     */
    @RequestMapping(value = "/batch/{recordId}", method = RequestMethod.POST)
    public void updateBatch(HttpServletResponse response, 
    		@PathVariable("recordId") Long recordId, 
    		String ids, String field, String value) {
    	
		getDB(recordId).updateBatch(ids, field, value);
        printSuccessMessage();
    }
    
    Map<String, String> getRequestMap(HttpServletRequest request) {
    	Map<String, String[]> parameterMap = request.getParameterMap();
    	Map<String, String> requestMap = new HashMap<String, String>();
    	for(String key : parameterMap.keySet()) {
    		String[] values = parameterMap.get(key);
    		if(values != null && values.length > 0) {
    			requestMap.put( key, values[0] );
    		}
    	}
    	
    	return requestMap;
    }
    
    @RequestMapping(value = "/{recordId}/{id}", method = RequestMethod.DELETE)
    public void delete(HttpServletResponse response, 
    		@PathVariable("recordId") Long recordId, 
    		@PathVariable("id") Long id) {
    	
    	exeDelete(recordId, id);
        printSuccessMessage();
    }
    
    private void exeDelete(Long recordId, Long id) {
    	_Database db = getDB(recordId);
		db.delete(id);
    	
		if(db.needFile) { // 删除附件
	    	List<?> attachs = recordService.getAttachList(recordId, id);
	    	for(Object attach : attachs) {
	    		Long attachId = ((RecordAttach)attach).getId();
	    		this.deleteAttach(null, attachId);
	    	}
		}
    }
    
    @RequestMapping(value = "/batch/{recordId}", method = RequestMethod.DELETE)
    public void deleteBatch(HttpServletResponse response, 
    		@PathVariable("recordId") Long recordId, String ids) {
    	
    	String[] idArray = ids.split(",");
    	for(String id : idArray) {
    		exeDelete(recordId, EasyUtils.obj2Long(id));
    	}
        printSuccessMessage();
    }
    
    /************************************* record batch import **************************************/
    
    /**
     * 将前台（一般为生成好的table数据）数据导出成CSV格式
     */
    @RequestMapping("/import/tl/{recordId}")
    public void getImportTL(HttpServletResponse response, @PathVariable("recordId") Long recordId) {
    	 _Database _db = getDB(recordId);
		
		String fileName = _db.recordName + "-导入模板.csv";
        String exportPath = DataExport.getExportPath() + "/" + fileName;
 
        DataExport.exportCSV(exportPath, EasyUtils.list2Str(_db.fieldNames));
        
        DataExport.downloadFileByHttp(response, exportPath);
    }
    
    /************************************* record attach operation **************************************/
    
	@RequestMapping("/attach/json/{recordId}/{itemId}")
    @ResponseBody
    public List<?> getAttachList(@PathVariable("recordId") Long recordId, 
    		@PathVariable("itemId") Long itemId) {
		
		return recordService.getAttachList(recordId, itemId);
    }
	
	@RequestMapping("/attach/json/{recordId}")
    @ResponseBody
    public List<?> getAttachList(@PathVariable("recordId") Long recordId) {
		return recordService.getAttachList(recordId, null);
    }
	
	@RequestMapping("/attach/xml/{recordId}/{itemId}")
    public void getAttachListXML(HttpServletResponse response, 
    		@PathVariable("recordId") Long recordId, @PathVariable("itemId") Long itemId) {
		
		List<?> list = recordService.getAttachList(recordId, itemId);
        GridDataEncoder attachGrid = new GridDataEncoder(list, DMConstants.GRID_RECORD_ATTACH);
        print("RecordAttach", attachGrid);
    }
	
	@RequestMapping(value = "/attach/{id}", method = RequestMethod.DELETE)
    public void deleteAttach(HttpServletResponse response, @PathVariable("id") Long id) {
		RecordAttach attach = recordService.getAttach(id);
		recordService.deleteAttach(id);
		FileHelper.deleteFile(new File(attach.getAttachPath()));
		if(response != null) {
			printSuccessMessage();
		}
    }
	
	@RequestMapping("/attach/download/{id}")
	public void downloadAttach(HttpServletResponse response, @PathVariable("id") Long id) throws IOException {
		RecordAttach attach = recordService.getAttach(id);
		if(attach == null) {
			throw new BusinessException("该附件找不到了，可能已被删除!");
		}
        FileHelper.downloadFile(response, attach.getAttachPath(), attach.getName());
	}
}
