package com.boubei.tssx.serialno;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boubei.tss.framework.persistence.ICommonService;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.util.DateUtil;

@Controller
@RequestMapping("/serialno")
public class SerialNOer {
	
	@Autowired ICommonService commonService;
	
	@RequestMapping(value = "/{precode}")
	@ResponseBody
	public Object create(@PathVariable("code") String precode) {
		String hql = " from SerialNO where day = ? and precode = ? ";
		String domain = Environment.getDomain();
		if(domain != null) {
			hql += " and domain = ? ";
		}
		
		SerialNO sn;
		Date today = DateUtil.today();
		List<?> list = commonService.getList(hql, today, precode);
		if(list.isEmpty()) {
			sn = new SerialNO();
			sn.setDay( today );
			sn.setDomain(domain);
			sn.setPrecode(precode);
			sn.setLastNum(1);
			sn.setCreateTime(new Date());
			sn.setUpdator(Environment.getUserCode());
			commonService.create(sn);
		} 
		else {
			sn = (SerialNO) list.get(0);
			int lastNum = sn.getLastNum();
			sn.setLastNum(++lastNum);
			commonService.update(sn);
		}
		
		String result = "000" + sn.getLastNum();
		result = result.substring(result.length() - 4);
		return precode + DateUtil.format(today, "yyyyMMdd") + result;
	}

}
