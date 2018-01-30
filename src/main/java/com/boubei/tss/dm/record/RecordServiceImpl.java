/* ==================================================================   
 * Created [2016-3-5] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.dm.record;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boubei.tss.EX;
import com.boubei.tss.dm.ddl._Database;
import com.boubei.tss.dm.record.file.RecordAttach;
import com.boubei.tss.dm.record.permission.RecordPermission;
import com.boubei.tss.dm.record.permission.RecordResource;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.um.permission.PermissionHelper;
import com.boubei.tss.util.EasyUtils;

@Service("RecordService")
@SuppressWarnings("unchecked")
public class RecordServiceImpl implements RecordService {
	
	Logger log = Logger.getLogger(this.getClass());
    
    @Autowired RecordDao recordDao;

	public Record getRecord(Long id) {
		Record record = recordDao.getEntity(id);
		if(record == null) {
			throw new BusinessException(EX.parse(EX.DM_13, id));
		}
        recordDao.evict(record);
        return record;
	}
	
	// 名字、表名二者有一个能对上即可
	public Long getRecordID(String recordName, int type) {
		String hql = "select o.id from Record o where ? in (o.name, o.table) and type = ? order by o.id desc"; 
		List<?> list = recordDao.getEntities(hql, recordName, type); 
		
		// 如有重名取当前用户有权限查看的最新一个
		for(Object id : list) {
			Long recordId = EasyUtils.obj2Long(id);
			
			PermissionHelper helper = PermissionHelper.getInstance();
			String permissionTable = RecordPermission.class.getName();
			if( helper.checkPermission(recordId, permissionTable, RecordResource.class, 
					Record.OPERATION_CDATA, Record.OPERATION_VDATA, Record.OPERATION_EDATA) ) {
				return recordId;
			}
		}
		
		throw new BusinessException(EX.parse(EX.DM_14, recordName));
	}
	
	public _Database getDB(Long recordId) {
		Record record = getRecord(recordId);
		return _Database.getDB(record);
	}

	public List<Record> getAllRecords() {
		return (List<Record>) recordDao.getEntities("from Record o order by o.decode");
	}
	
	public List<Record> getVisiables() {
		return getAllRecords();
	}
	
	public List<Record> getRecordables() {
		return getAllRecords();
	}

	public List<Record> getAllRecordGroups() {
		String hql = "from Record o where o.type = ? order by o.decode";
		return (List<Record>) recordDao.getEntities(hql, Record.TYPE0);
	}
	
	public List<Record> getRecordsByPID(Long recordId, Long userId) {
		return recordDao.getChildrenById(recordId);
	}

	public Record createRecord(Record record) {
        Long groupId = record.getParentId();
		record.setSeqNo( recordDao.getNextSeqNo(groupId) );
        recordDao.create(record);
        
        // 表名自动生成
        String table = record.getTable();
        if( table != null ) {
        	table = table.replaceAll(Record.DEFAULT_TABLE, "t_" + groupId + "_" + record.getId());
    		record.setTable( table );
    		recordDao.update(record);
        }
        
        if(Record.TYPE1 == record.getType() && !record.inSysTable() ) {
        	_Database _db = _Database.getDB(record);
        	_db.createTable();
        	_db.createUniqueAndIndex();
        }

        return record;
	}
	
	public void updateRecord(Record record) {
		if(Record.TYPE0 == record.getType()) { // 分组
			recordDao.refreshEntity(record);
			return;
    	}
		
    	Record _old = recordDao.getEntity(record.getId());
    	recordDao.evict(_old);
    	
    	String oldDatasource = _old.getDatasource();
    	_old.setDatasource(record.getDatasource()); // 更新_old对象的数据源为新的数据源
		_Database _db = _Database.getDB(_old);     // eg：数据库类型从MySQL变成了Oracle，这里获取到的将是_Oracle
		_db.datasource = oldDatasource;
		
    	recordDao.refreshEntity(record);
    	
    	_db.alterTable(record);
	}

	public Record delete(Long id) {
		Record record = getRecord(id);
		checkOpPermission(id, Record.OPERATION_DELETE);
		
        return recordDao.deleteRecord(record);
	}

    public void startOrStop(Long id, Integer disabled) {
        List<Record> list = ParamConstants.TRUE.equals(disabled) ? 
        		recordDao.getChildrenById(id, Record.OPERATION_EDIT) : recordDao.getParentsById(id);
        
        for (Record record : list) {
            record.setDisabled(disabled);
            recordDao.updateWithoutFlush(record);
        }
        recordDao.flush();
    }
    
    // 判断对所有子节点是否都拥有指定的操作权限
    private boolean checkOpPermission(Long id, String operationId) {
    	List<?> canDelChilds = recordDao.getChildrenById(id, operationId);
		List<?> allSubChilds = recordDao.getChildrenById(id);
        
        //如果将要操作的数量==能够操作的数量,说明对所有节点都有操作权限,则返回true
        return canDelChilds.size() == allSubChilds.size();
    }
    
	public void sort(Long startId, Long targetId, int direction) {
		recordDao.sort(startId, targetId, direction);
	}

	public void move(Long id, Long groupId) {
		List<Record> list  = recordDao.getChildrenById(id);
        for (Record temp : list) {
            if (temp.getId().equals(id)) { // 判断是否是移动节点（即被移动枝的根节点）
                temp.setSeqNo(recordDao.getNextSeqNo(groupId));
                temp.setParentId(groupId);
            }
 
            recordDao.moveEntity(temp);
        }
	}
	
	public Integer getAttachSeqNo(Long recordId, Long itemId) {
		String hql = "select max(o.seqNo) from RecordAttach o where o.recordId = ? and o.itemId = ?";
        List<?> list = recordDao.getEntities(hql, recordId, itemId);
        Integer nextSeqNo = (Integer) EasyUtils.checkNull(list.get(0), 0);
        return nextSeqNo + 1;
	}

	public List<?> getAttachList(Long recordId, Long itemId) {
		String hql = "from RecordAttach o where o.recordId = ? and o.itemId = ?";
		return recordDao.getEntities(hql, recordId, itemId);
	}

	public RecordAttach deleteAttach(Long id) {
		RecordAttach attach = getAttach(id);
		recordDao.delete(attach);
		return attach;
	}

	public RecordAttach createAttach(RecordAttach attach) {
		recordDao.createObject(attach);
		return attach;
	}
	
	public RecordAttach getAttach(Long id) {
		return (RecordAttach) recordDao.getEntity(RecordAttach.class, id);
	}
}
