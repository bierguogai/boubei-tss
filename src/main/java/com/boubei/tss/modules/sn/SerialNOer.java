package com.boubei.tss.modules.sn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.persistence.ICommonService;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.EasyUtils;

/**
 *	取号器，支持指定前缀及一次获取多个连号
 *	配合数据表字段使用，在字段定义默认值填入：XX-yyMMddxxxx，则在界面新增或导入数据时自动调用本取号器
 */
@Controller
@RequestMapping("/serialno")
public class SerialNOer {

	@RequestMapping(value = "/{precode}/{count}")
	@ResponseBody
	public synchronized List<String> create(@PathVariable("precode") String precode, 
			@PathVariable("count") int count) {
		
		ICommonService commonService = Global.getCommonService();
		
		String hql = " from SerialNO where day = ? and precode = ? and domain = ? ";
		String domain = (String) EasyUtils.checkNull(Environment.getDomain(), "noDomain");
		
		SerialNO first;
		Date today = DateUtil.today();
		List<?> list = commonService.getList(hql, today, precode, domain);
		if(list.isEmpty()) {
			first = new SerialNO();
			first.setDay( today );
			first.setDomain(domain);
			first.setPrecode(precode);
			first.setLastNum(0);
			first.setCreateTime(new Date());
			first.setCreator(Environment.getUserCode());
			commonService.create(first);
		} 
		else {
			first = (SerialNO) list.get(0);
		}
		
		List<String> result = new ArrayList<String>();
		count = Math.min(Math.max(1, count), 100000); // 单次最多1到100000个
		for(int i = 1; i <= count; i++) {
			int no = first.getLastNum() + i;
			String sn = "000" + no;
			sn = sn.substring(sn.length() - (no >= 10000 ? String.valueOf(no).length() : 4));
			sn = precode + DateUtil.format(today, "yyyyMMdd").substring(2) + sn;
			result.add(sn);
		}
		first.setLastNum(first.getLastNum() + count);
		first.setUpdateTime(new Date());
		first.setUpdator(Environment.getUserCode());
		commonService.update(first);
		
		return result;
	}

}
