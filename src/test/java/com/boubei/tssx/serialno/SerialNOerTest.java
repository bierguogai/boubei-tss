package com.boubei.tssx.serialno;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.AbstractTest4;
import com.boubei.tss.util.DateUtil;

public class SerialNOerTest extends AbstractTest4 {
	
	@Autowired SerialNOer serialNOer;
	
	@Test
	public void test() {
		String precode = "SO";
		String sn = precode + DateUtil.format(DateUtil.today(), "yyyyMMdd");
		Assert.assertEquals(sn + "0001", serialNOer.create(precode) );
		Assert.assertEquals(sn + "0002", serialNOer.create(precode) );
		Assert.assertEquals(sn + "0003", serialNOer.create(precode) );

	}

}
