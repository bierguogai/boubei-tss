package com.boubei.tss.modules.license;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boubei.tss.dm.DataExport;
import com.boubei.tss.framework.persistence.ICommonService;
import com.boubei.tss.um.entity.UserToken;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.EasyUtils;

@Controller
@RequestMapping("/auth/license")
public class LicenseAction {
	
	@Autowired ICommonService commonService;
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> genLicense(String owner, String product, 
			String version, String expiry, String type) throws Exception {
		
		License license = new License();
		license.owner = owner.trim();
		license.product = product.trim();
		license.version = version.trim();
		license.expiry = DateUtil.parse(expiry);
		license.type = EasyUtils.checkNull(type, License.LicenseType.Commercial).toString();
		
        LicenseFactory.sign(license);
        
        Map<String, String> map = new HashMap<String, String>();
        map.put("license", license.toString());
        return map;
	}
	
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public void download(HttpServletResponse response, @PathVariable Long id) throws Exception {
    	
    	UserToken ut = (UserToken) commonService.getEntity(UserToken.class, id);
    	String license = ut.getToken();
    	if( EasyUtils.isNullOrEmpty(license) || license.indexOf("signature") < 0 ) {
    		String[] reource = ut.getResource().split("\\|");
    		String expiry = DateUtil.format(ut.getExpireTime());
			Map<String, String> map = genLicense(ut.getUser(), reource[0], reource[1], expiry, null);
    		ut.setToken( license = map.get("license") );
    		commonService.update(ut);
    	}
        
    	String exportPath = DataExport.getExportPath() + "/" + ut.getUser() + "/tss.license";
    	
		// 先输出内容到服务端的导出文件中
        DataExport.exportCSV(exportPath, license);
        DataExport.downloadFileByHttp(response, exportPath);
    }
    
    @RequestMapping(value = "/current", method = RequestMethod.POST)
	@ResponseBody
    public boolean validate() {
		return License.validate();
    }
	
}
