package com.boubei.tss.demo.crud;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.AbstractTest4;
import com.boubei.tss.framework.EasyUIDataGrid;
import com.boubei.tss.modules.param.Param;

public class CRUDTest extends AbstractTest4 {
	
	@Autowired DemoAction action;
	
	List<Param> list1;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		list1 = paramService.getComboParam("EntityState");
	}
	
	@Test
	public void test() {
		List<DemoEntity> list = action.getAllEntities();
		Assert.assertEquals(0, list.size());
		
		DemoEntity entity = new DemoEntity();
		entity.setCode("test 1");
		entity.setName("test 1");
		entity.setState(list1.get(0));
		entity = action.save(entity );
		
		Long id = entity.getId();
		Assert.assertNotNull(id);
		entity = action.getEntityById(id);
		Assert.assertNotNull(entity);
		Assert.assertEquals("test 1", entity.getCode());
		
		entity.setName("test 1 update");
		action.save(entity);
		entity = action.getEntityById(id);
		Assert.assertEquals("test 1 update", entity.getName());
		
		list = action.getAllEntities();
		Assert.assertEquals(1, list.size());
		
		DemoSO so = new DemoSO();
		so.setCode("test 1");
		EasyUIDataGrid dg = action.search(request, so , 1, 10);
		Assert.assertEquals(1, dg.total);
		
		so.setCode("test 22");
		dg = action.search(request, so , 1, 10);
		Assert.assertEquals(0, dg.total);
		
		action.delete(id);
		
		list = action.getAllEntities();
		Assert.assertEquals(0, list.size());
	}

}
