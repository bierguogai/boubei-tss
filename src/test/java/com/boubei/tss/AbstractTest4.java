package com.boubei.tss;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.sso.IdentityCard;
import com.boubei.tss.framework.sso.TokenUtil;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.modules.param.ParamService;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.helper.dto.OperatorDTO;
import com.boubei.tss.um.permission.PermissionHelper;
import com.boubei.tss.util.URLUtil;
import com.boubei.tssx.ParamsInit;

@ContextConfiguration(
	  locations={
			"classpath:META-INF/spring-framework.xml",
			"classpath:META-INF/spring-um.xml",
		    "classpath:META-INF/spring-mvc.xml",
		    "classpath:META-INF/spring-test.xml"
	  }
) 
@TransactionConfiguration(defaultRollback = true) // 自动回滚设置为false，否则数据将不插进去
public abstract class AbstractTest4 extends AbstractTransactionalJUnit4SpringContextTests { 
 
    protected static Logger log = Logger.getLogger(AbstractTest4.class);    
    
    @Autowired protected H2DBServer dbserver;
    
    @Autowired protected ParamsInit systemInit;
    @Autowired protected ParamService paramService;
    
    @Autowired protected PermissionHelper permissionHelper;
    
    protected MockHttpServletRequest request;
    protected MockHttpServletResponse response;
    
    @Before
    public void setUp() throws Exception {
    	
        Global.setContext(super.applicationContext);
        Context.setResponse(response = new MockHttpServletResponse());
		Context.initRequestContext(request = new MockHttpServletRequest());
        
        init();
    }
    
    private void init() {
    	if(paramService.getParam(0L) == null) {
    		String sqlPath = URLUtil.getResourceFileUrl("sql/mysql").getPath();
    		H2DBServer.excuteSQL(sqlPath);
    	}
        
    	// 初始化虚拟登录用户信息
        login(UMConstants.ADMIN_USER_ID, UMConstants.ADMIN_USER_NAME);
        
        systemInit.init();
    }
    
    protected void login(Long userId, String loginName) {
    	OperatorDTO loginUser = new OperatorDTO(userId, loginName);
    	String token = TokenUtil.createToken("1234567890", userId); 
        IdentityCard card = new IdentityCard(token, loginUser);
        Context.initIdentityInfo(card);
    }
}
