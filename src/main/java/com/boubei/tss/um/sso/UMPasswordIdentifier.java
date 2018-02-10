/* ==================================================================   
 * Created [2009-08-29] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.um.sso;

import org.apache.log4j.Logger;

import com.boubei.tss.EX;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.sso.IOperator;
import com.boubei.tss.framework.sso.IPWDOperator;
import com.boubei.tss.framework.sso.IdentityGetter;
import com.boubei.tss.framework.sso.IdentityGetterFactory;
import com.boubei.tss.framework.sso.PasswordPassport;
import com.boubei.tss.framework.sso.identifier.BaseUserIdentifier;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.um.service.ILoginService;
import com.boubei.tss.util.InfoEncoder;

/**
 * <p>
 * UM本地用户密码身份认证器<br>
 * 根据用户帐号、密码等信息，通过UM本地数据库进行身份认证
 * </p>
 */
public class UMPasswordIdentifier extends BaseUserIdentifier {
    
    protected Logger log = Logger.getLogger(this.getClass());
    
    protected ILoginService loginservice = (ILoginService) Global.getBean("LoginService");
    
    protected IOperator validate() throws BusinessException {
        PasswordPassport passport = new PasswordPassport();
        String loginName = passport.getLoginName(); // loginName/email/mobile
        String passwd = passport.getPassword();
        
        IPWDOperator operator;
		try {
            operator = loginservice.getOperatorDTOByLoginName(loginName); // mysql 不区分大小写
        } catch (BusinessException e) {
        	throw new BusinessException(e.getMessage(), false);
        }
		
		loginName = operator.getLoginName();
		int errorCount = loginservice.checkPwdErrorCount(loginName);
       
		String md5Passwd1 = User.encodePasswd(loginName, passwd);
		String md5Passwd2 = User.encodePasswd(loginName.toUpperCase(), passwd); // 转换成大写再次尝试
		String md5Passwd3 = User.encodePasswd(loginName.toLowerCase(), passwd); // 转换成小写再次尝试
        String md5Passwd0 = operator.getPassword(); // 数据库里存的MD5加密密码
        
        // 如果各种验证都不通过
		if ( !md5Passwd1.equals(md5Passwd0) 
				&& !md5Passwd2.equals(md5Passwd0)
				&& !md5Passwd3.equals(md5Passwd0) 
				&& !customizeValidate(operator, passwd) ) {
			
			// 记录密码连续输入错误的次数，超过10次将禁止登陆10分钟
			try {
				loginservice.recordPwdErrorCount(loginName, errorCount);
				errorCount ++;
			} catch(Exception e) { }
			
			log.debug("[" + loginName + ", " + passwd + "] is wrong inputing passwd " + errorCount + " times");
			String notice = errorCount == 10 ? EX.U_39 : EX.parse(EX.U_40, (10-errorCount));
			throw new BusinessException(notice);
        }
		else {
			loginSuccess("Logon by UM ");
		}
		
		try {
			loginservice.setLastLoginTime(operator.getId());
		} 
    	catch( Exception e ) { }
		
		return operator;
    }
    
    /*
     *  判断用户输入的密码是否和第三方系统的密码的一致，如果是，则将用户的平台里的密码也设置为该密码，并完成本次登录
     *  (适用于UM的用户从第三方导入的情况，因密码是加密的(且TSS里加密方式是账号 + 密码))
     */
    protected boolean customizeValidate(IPWDOperator operator, String passwd) {
        IdentityGetter ig = IdentityGetterFactory.create();
        boolean result = ig.indentify(operator, passwd);
        if(result) { // 如果密码是在第三方系统里验证通过，则设置到UM中
        	try {
        		Object token = loginservice.resetPassword(operator.getId(), passwd); 
        		loginSuccess( InfoEncoder.simpleEncode( (String)token, 12) );
        	} 
        	catch( Exception e ) { }
        }
		return result;
    }
}
