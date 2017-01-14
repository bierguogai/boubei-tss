package com.boubei.tss.dm.record;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.boubei.tss.framework.persistence.TreeSupportDao;

@Repository("RecordDao")
public class RecordDaoImpl extends TreeSupportDao<Record> implements RecordDao {

	public RecordDaoImpl() {
        super(Record.class);
    }
 
	public Record deleteRecord(Record record) {
		Long id = record.getId();
        List<Record> list = getChildrenById(id);
        for(Record entity : list) {
            delete(entity);
        }
        return record;
	}
	
	public List<Record> getChildrenById(Long id, String operationId) {
		return getChildrenById(id);
	}
}