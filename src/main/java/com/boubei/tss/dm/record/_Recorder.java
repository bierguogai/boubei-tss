package com.boubei.tss.dm.record;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import com.boubei.tss.dm.DMUtil;
import com.boubei.tss.dm.data.sqlquery.SQLExcutor;
import com.boubei.tss.dm.data.util.DataExport;
import com.boubei.tss.dm.record.ddl._Database;
import com.boubei.tss.dm.record.file.RecordAttach;
import com.boubei.tss.dm.record.permission.RecordPermission;
import com.boubei.tss.dm.record.permission.RecordResource;
import com.boubei.tss.framework.SecurityUtil;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.exception.ExceptionEncoder;
import com.boubei.tss.framework.persistence.pagequery.PageInfo;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.framework.web.display.grid.DefaultGridNode;
import com.boubei.tss.framework.web.display.grid.GridDataEncoder;
import com.boubei.tss.framework.web.display.grid.IGridNode;
import com.boubei.tss.framework.web.mvc.BaseActionSupport;
import com.boubei.tss.um.permission.PermissionHelper;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;

@Controller
@RequestMapping( {"/auth/xdata", "/xdata/api"})
public class _Recorder extends BaseActionSupport {
	
	public static final int PAGE_SIZE = 50;
	
	@Autowired RecordService recordService;
	
	_Database getDB(Long recordId, String... permitOptions) {
		// 检测当前用户对当前录入表是否有指定的操作权限
		boolean flag = permitOptions.length == 0;
		for(String permitOption : permitOptions) {
			flag = flag || checkPermission( recordId, permitOption );
		}
		if(!flag) {
			throw new BusinessException("权限不足，操作失败。");
		}
		
		Pool cache = CacheHelper.getLongCache();
		String cacheKey = "_db_record_" + recordId;
		if( !cache.contains(cacheKey) ) {
			cache.putObject(cacheKey, recordService.getDB(recordId));
		} 
		
		Cacheable cacheItem = cache.getObject(cacheKey);
		return (_Database) cacheItem.getValue();
	}
	
	@RequestMapping("/define/{recordId}")
    @ResponseBody
    public Object getDefine(@PathVariable("recordId") Long recordId) {
		Record record = recordService.getRecord(recordId);
		if(!record.isActive()) {
			throw new BusinessException("该录入表已被停用，无法再录入数据！");
		}
		
        return new Object[] { 
        		getDB(recordId, Record.OPERATION_CDATA, Record.OPERATION_EDATA, Record.OPERATION_VDATA).getFields(), 
        		record.getCustomizeJS(), 
        		record.getCustomizeGrid(),
        		record.getNeedFile(),
        		record.getBatchImp(),
        		record.getName(),
        		record.getCustomizePage()
        	};
    }
	
	public Map<String, String> prepareParams(HttpServletRequest request, Long recordId) {
		Map<String, String> requestMap = DMUtil.getRequestMap(request, false);
		
		/* 其它系统调用接口时，传入其在TSS注册的用户ID; 检查令牌，令牌有效则自动完成登陆 */
    	String uName  = requestMap.get("uName"), uToken = requestMap.get("uToken");
    	if( !EasyUtils.isNullOrEmpty(uToken) && !EasyUtils.isNullOrEmpty(uName) ) {
    		Record record = recordService.getRecord(recordId);
        	if( !DMUtil.checkAPIToken(record, uName, uToken) ) {
    			throw new BusinessException("令牌验证未获通过，调用接口失败。");
    		}
    	} 
		                                                                                                                                                                                                                                                 
    	return requestMap;
    }
	
    @RequestMapping("/xml/{recordId}/{page}")
    public void showAsGrid(HttpServletRequest request, HttpServletResponse response, 
            @PathVariable("recordId") Long recordId, 
            @PathVariable("page") int page) {
 
    	Map<String, String> requestMap = prepareParams(request, recordId);
        _Database _db = getDB(recordId);
        
        SQLExcutor ex = _db.select(page, PAGE_SIZE, requestMap); // db.select 里已经包含权限控制
        
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
    	
    	Map<String, String> requestMap = prepareParams(request, recordId);
    	int _pagesize = getPageSize(requestMap, PAGE_SIZE*20);
    	
        _Database _db = getDB(recordId);
        return _db.select( page, _pagesize, requestMap ).result;
    }
    
    private int getPageSize(Map<String, String> m, int defaultSize) {
    	Object pagesize = EasyUtils.checkNull(m.get("pagesize"), m.get("rows"), defaultSize);
    	return EasyUtils.obj2Int(pagesize);
    }
    
    @RequestMapping("/export/{recordId}")
    public void exportAsCSV(HttpServletRequest request, HttpServletResponse response, 
    		@PathVariable("recordId") Long recordId) {
        
    	long start = System.currentTimeMillis();
    	
    	Map<String, String> requestMap = DMUtil.getRequestMap(request, true);  // GET Method Request
    	_Database _db = getDB(recordId);
        
    	int _page = EasyUtils.obj2Int( EasyUtils.checkNull(requestMap.get("page"), "1") );
    	int _pagesize = getPageSize(requestMap, 10*10000);
    	
		SQLExcutor ex = _db.select(_page, _pagesize, requestMap);

		String fileName = DateUtil.format(new Date()) +"_"+ recordId + Environment.getUserId() + ".csv";
        for (Map<String, Object> row : ex.result) { // 剔除
        	row.remove("createtime");
        	row.remove("creator");
        	row.remove("updatetime");
        	row.remove("updator");
        	row.remove("version");
        	row.remove("id");
        }
		String exportPath = DataExport.exportCSV(fileName, ex.result, _db.fieldNames);
        DataExport.downloadFileByHttp(response, exportPath);

        DMUtil.outputAccessLog("record-" + recordId, _db.recordName, "exportAsCSV", requestMap, start);
    }
	
    @RequestMapping(value = "/{recordId}", method = RequestMethod.POST)
    public void create(HttpServletRequest request, HttpServletResponse response, 
    		@PathVariable("recordId") Long recordId) {
    	
    	Map<String, String> requestMap = prepareParams(request, recordId);
    	
    	_Database _db = getDB(recordId, Record.OPERATION_CDATA);
    	try {
    		Long newID = _db.insertRID( requestMap );
    		printSuccessMessage( String.valueOf(newID) );
    	}
    	catch(Exception e) {
    		throwEx(e, _db + "表里新增");
    	}
    }
    
    @RequestMapping(value = "/rid/{recordId}", method = RequestMethod.POST)
    @ResponseBody
    public Object createAndReturnID(HttpServletRequest request, 
    		@PathVariable("recordId") Long recordId) {
    	
    	Map<String, String> requestMap = prepareParams(request, recordId);
    	
    	_Database _db = getDB(recordId, Record.OPERATION_CDATA);
    	Long newID = null;
    	try {
    		newID = _db.insertRID( requestMap );
    	}
    	catch(Exception e) {
    		throwEx(e, _db + "表里新增");
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
    	
    	Map<String, String> requestMap = prepareParams(request, recordId);
    	
    	// 检查用户对当前记录是否有编辑权限，防止篡改别人创建的记录
    	checkRowEditable(recordId, id);
    	
    	_Database _db = getDB(recordId);
    	try {
			_db.update(id, requestMap );
    		printSuccessMessage();
    	}
    	catch(Exception e) {
    		throwEx(e, _db + "表里修改");
    	}
    }
    
    /**
     * 批量更新选中记录行的某个字段值，用在批量审批等场景
     */
    @RequestMapping(value = "/batch/{recordId}", method = RequestMethod.POST)
    public void updateBatch(HttpServletRequest request, HttpServletResponse response, 
    		@PathVariable("recordId") Long recordId, 
    		String ids, String field, String value) {
    	
    	prepareParams(request, recordId);
    	
    	// 检查用户对当前记录是否有编辑权限，防止篡改别人创建的记录
		if( !checkPermission(recordId, Record.OPERATION_EDATA) && !checkPermission(recordId, Record.OPERATION_CDATA) ) {
			throw new BusinessException("您对此录入表没有维护权限");
		}
    	
		getDB(recordId).updateBatch(ids, field, value);
        printSuccessMessage();
    }
 
    
    @RequestMapping(value = "/{recordId}/{id}", method = RequestMethod.DELETE)
    public void delete(HttpServletRequest request, HttpServletResponse response, 
    		@PathVariable("recordId") Long recordId, 
    		@PathVariable("id") Long id) {
    	
    	prepareParams(request, recordId);
    	
    	exeDelete(recordId, id);
        printSuccessMessage();
    }
    
    private void exeDelete(Long recordId, Long id) {
		// 检查用户对当前记录是否有编辑权限
    	checkRowEditable(recordId, id);
    	
    	_Database db = getDB(recordId);
		db.delete(id);
    	
	    // 删除附件
    	List<?> attachs = recordService.getAttachList(recordId, id);
    	for(Object obj : attachs) {
    		RecordAttach attach = (RecordAttach)obj;
    		recordService.deleteAttach( attach.getId() );
    		FileHelper.deleteFile(new File(attach.getAttachPath()));
    	}
    }
    
    @RequestMapping(value = "/batch/{recordId}", method = RequestMethod.DELETE)
    public void deleteBatch(HttpServletRequest request, HttpServletResponse response, 
    		@PathVariable("recordId") Long recordId, String ids) {
    	
    	prepareParams(request, recordId);
    	
    	String[] idArray = ids.split(",");
    	for(String id : idArray) {
    		exeDelete(recordId, EasyUtils.obj2Long(id));
    	}
        printSuccessMessage();
    }
    
    /**
     * 批量新增、修改、删除，All in one。
     * @param request
     * @param recordId
     * @param csv
     * @return
     */
    @RequestMapping(value = "/cud/{recordId}", method = RequestMethod.POST)
    @ResponseBody
    public Object cudBatch(HttpServletRequest request, @PathVariable("recordId") Long recordId, String csv) {
    	_Database _db = getDB(recordId, Record.OPERATION_CDATA);
    	prepareParams(request, recordId);
    	
		String[] rows = EasyUtils.split(csv, "\n");
		List<Map<String, String>> insertList = new ArrayList<Map<String, String>>();
		int updateCount = 0, deleteCount = 0;;
		
		String[] headers = rows[0].split(",");
		for(int index = 1; index < rows.length; index++) { // 第一行为表头，不要
			String row = rows[index];
			String[] fields = row.split(",");
			
			Map<String, String> item = new HashMap<String, String>();
			for(int j = 0; j < fields.length; j++) {
    			item.put(headers[j], fields[j]);
        	}
			
			String _itemID = item.get("id");
			if( EasyUtils.isNullOrEmpty(_itemID) ) {
				insertList.add(item);
			} else {
				Long itemID = EasyUtils.obj2Long( _itemID );
				if( row.replaceAll(",", "").trim().equals(_itemID.trim()) ) { // 除了ID其它都为空
					exeDelete(recordId, itemID);
					deleteCount++;
				} else {
					checkRowEditable(recordId, itemID);
					_db.update(itemID, item);
					updateCount ++;
				}
			}
		}
    	_db.insertBatch(insertList);
    	
    	Map<String, Object> rtMap = new HashMap<String, Object>();
    	rtMap.put("created", insertList.size());
    	rtMap.put("updated", updateCount);
    	rtMap.put("deleted", deleteCount);
    	return rtMap;
    }
    
    /************************************* record batch import **************************************/
    
    /**
     * 将前台（一般为生成好的table数据）数据导出成CSV格式
     */
    @RequestMapping("/import/tl/{recordId}")
    public void getImportTL(HttpServletResponse response, @PathVariable("recordId") Long recordId) {
    	 _Database _db = getDB(recordId, Record.OPERATION_CDATA, Record.OPERATION_EDATA, Record.OPERATION_VDATA);
		
		String fileName = _db.recordName + "-导入模板.csv";
        String exportPath = DataExport.getExportPath() + "/" + fileName;
 
        DataExport.exportCSV(exportPath, EasyUtils.list2Str(_db.fieldNames));
        
        DataExport.downloadFileByHttp(response, exportPath);
    }
    
    /************************************* record attach operation **************************************/
    
	@RequestMapping("/attach/json/{recordId}/{itemId}")
    @ResponseBody
    public List<?> getAttachList(HttpServletRequest request, @PathVariable("recordId") Long recordId, 
    		@PathVariable("itemId") Long itemId) {
		
		prepareParams(request, recordId);
		
		// 检查用户对当前记录是否有查看权限
		if( !checkRowVisible(recordId, itemId) ) {
			throw new BusinessException("您对此记录没有浏览权限");
		}
		
		return recordService.getAttachList(recordId, itemId);
    }

	@RequestMapping("/attach/xml/{recordId}/{itemId}")
    public void getAttachListXML(HttpServletRequest request, HttpServletResponse response, 
    		@PathVariable("recordId") Long recordId, @PathVariable("itemId") Long itemId) {
		
		prepareParams(request, recordId);
		
		// 检查用户对当前记录是否有查看权限
		if( !checkRowVisible(recordId, itemId) ) {
			throw new BusinessException("您对此记录没有浏览权限");
		}
		
		List<?> list = recordService.getAttachList(recordId, itemId);
        GridDataEncoder attachGrid = new GridDataEncoder(list, DMConstants.GRID_RECORD_ATTACH);
        print("RecordAttach", attachGrid);
    }
	
	@RequestMapping(value = "/attach/{id}", method = RequestMethod.DELETE)
    public void deleteAttach(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") Long id) {
		prepareParams(request, EasyUtils.obj2Long(request.getParameter("recordId")));
		
		RecordAttach attach = recordService.getAttach(id);
		if(attach == null) {
			throw new BusinessException("该附件不存在，可能已被删除!");
		}
		
		// 检查用户对当前附件所属记录是否有编辑权限
		checkRowEditable(attach.getRecordId(), attach.getItemId());
		
		recordService.deleteAttach(id);
		FileHelper.deleteFile(new File(attach.getAttachPath()));
		
		printSuccessMessage();
    }
	
	@RequestMapping("/attach/download/{id}")
	public void downloadAttach(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") Long id) throws IOException {
		prepareParams(request, EasyUtils.obj2Long(request.getParameter("recordId")));
		
		RecordAttach attach = recordService.getAttach(id);
		if(attach == null) {
			throw new BusinessException("该附件不存在，可能已被删除!");
		}
		
		// 检查用户对当前附件所属记录是否有查看权限
		if( !checkRowVisible(attach.getRecordId(), attach.getItemId()) ) {
			throw new BusinessException("您对此附件没有查看权限");
		} 
		
		FileHelper.downloadFile(response, attach.getAttachPath(), attach.getName());
	}
	
	/************************************* check permissions：安全级别 > 5 才启用 **************************************/
	
	private boolean checkPermission(Long recordId, String permitOption) {
		if(SecurityUtil.getLevel() < SecurityUtil.LEVEL_6 ) return true;
		
		PermissionHelper helper = PermissionHelper.getInstance();
		String permissionTable = RecordPermission.class.getName();
		List<String> permissions = helper.getOperationsByResource(recordId, permissionTable, RecordResource.class);
		
		return permissions.contains( permitOption );
	}
	
	/**
	 * 检查用户对当前记录是否有编辑权限，防止篡改别人创建的记录
	 * @param recordId
	 * @param itemId
	 */
	private void checkRowEditable(Long recordId, Long itemId) {
		if(SecurityUtil.getLevel() < SecurityUtil.LEVEL_6 ) return;
		
		boolean flag = false;
		if( checkPermission(recordId, Record.OPERATION_EDATA) ) {
			flag = checkRowVisible(recordId, itemId); // 如果有【维护数据】权限，则只要可见就能编辑
		}
		if( !flag && checkPermission(recordId, Record.OPERATION_CDATA) ) {
			flag = checkRowAuthor(recordId, itemId); // 如果没有【维护数据】只有【新建】权限，则只能编辑自己创建的记录
		}
		
		if(!flag) {
			throw new BusinessException("您对此数据记录【" +itemId+ "】没有维护权限，无法修改或删除。");
		}
	}
	
	/**
	 * 因db.select方法里对数据进行了权限过滤，所以能按ID查询出来的都是有权限查看的
	 */
	private boolean checkRowVisible(Long recordId, Long itemId) {
		if(SecurityUtil.getLevel() < SecurityUtil.LEVEL_6 ) return true;
		
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("id", EasyUtils.obj2String(itemId));
		
        SQLExcutor ex = getDB(recordId).select( 1, 1, requestMap );
		return !ex.result.isEmpty();
	}
	
	private boolean checkRowAuthor(Long recordId, Long itemId) {
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("id", EasyUtils.obj2String(itemId));
		requestMap.put("creator", Environment.getUserCode());
		
        SQLExcutor ex = getDB(recordId).select( 1, 1, requestMap );
        return !ex.result.isEmpty();
	}
}
