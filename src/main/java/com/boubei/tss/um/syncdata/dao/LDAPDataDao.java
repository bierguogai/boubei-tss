/* ==================================================================   
 * Created [2009-08-29] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.um.syncdata.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.dom4j.Document;
import org.dom4j.Element;

import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.um.helper.dto.GroupDTO;
import com.boubei.tss.um.helper.dto.UserDTO;
import com.boubei.tss.um.syncdata.SyncDataHelper;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.XMLDocUtil;

/** 
 * 从LDAP数据里同步用户组织信息
 */
public class LDAPDataDao implements IOutDataDao {
    
    public final static String DEFAULT_VALUE = "defaultValue";
    
    /** 组需要的属性  */
    public final static String APPLICATION_ID_GROUP = "applicationId";
    public final static String DESCRIPTION_GROUP    = "description";
    
    /** 用户需要的属性 */
    public final static String LOGIN_NAME_USER   = "loginName";
    public final static String EAMIL_USER        = "email";
    public final static String SEX_USER          = "sex";
    public final static String BIRTHDAY_USER     = "birthday";
    public final static String EMPLOYEE_NO_USER  = "employeeNo";
    public final static String USER_STATUS       = "disabled";
 
    private static final String GROUP_FILTER_STR = "(objectclass=organizationalUnit)";
    private static final String USER_FILTER_STR  = "CN=*";
    
    private static final String OU_TAG  = "OU=".toLowerCase();
    private static final String SN_TAG  = "SN".toLowerCase();

    private DirContext getConnection(Map<String, String> map){
    	// 初始化参数设置
        Hashtable<String, String>  env = new Hashtable<String, String> ();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.PROVIDER_URL, map.get(SyncDataHelper.URL));
        env.put(Context.SECURITY_PRINCIPAL, map.get(SyncDataHelper.USERNAME));
        env.put(Context.SECURITY_CREDENTIALS, map.get(SyncDataHelper.PASSWORD));

        // 连接到数据源
        DirContext conn = null;
        try {
            conn = new InitialDirContext(env);
        } catch (Exception e) {
            throw new BusinessException("连接LDAP失败", e);
        }
        return conn;
    }

    /* 
     * attributes格式如：
     *  <attributes>
            <applicationId defaultValue="OA">applicationId</applicationId>
            <id defaultValue="">id</id>
            <parentGroupId defaultValue="">parentGroupId</parentGroupId>
            <groupName defaultValue="">groupName</groupName>
            <description defaultValue="">description</description>
            <groupOrder defaultValue="">groupOrder</groupOrder>
        </attributes> 
     * @see com.boubei.tss.um.syncdata.dao.IOutDataDao#getOtherGroups(java.lang.Object[])
     */
	public List<?> getOtherGroups(Map<String, String> paramsMap, String attributes, String groupId) {
        Map<String, String> fieldNames = new HashMap<String, String>();
        Map<String, String> defaultValues = new HashMap<String, String>();
        for (Iterator<?> it = XMLDocUtil.dataXml2Doc(attributes).getRootElement().elementIterator(); it.hasNext();) {
            Element element = (Element) it.next();
            fieldNames.put(element.getName(), element.getText());
            defaultValues.put(element.getName(), element.attributeValue(DEFAULT_VALUE));
        }
        
        List<GroupDTO> items = new ArrayList<GroupDTO>();
        try {
        	DirContext conn =  getConnection(paramsMap);
            NamingEnumeration<SearchResult> en = ldapSearch(conn, groupId, GROUP_FILTER_STR);         
            while (en != null && en.hasMoreElements()) {
                SearchResult searchResult = en.next();
                String dn = searchResult.getName();

                // 组合全路径
                dn = !EasyUtils.isNullOrEmpty(dn) ? (dn + "," + groupId) : groupId;
                
                if (dn.indexOf(OU_TAG) < 0)  continue;

                GroupDTO group = new GroupDTO();

                // 获得组的属性
                dn = dn.toLowerCase().replaceAll(", ", ",");
                group.setId(getGroupId(dn));
                group.setName(getGroupName(dn));
                group.setParentId(getParentGroupId(dn));
                
                Attributes attrs = searchResult.getAttributes();
                // description
                String value = getValueFromAttribute(attrs, fieldNames.get(DESCRIPTION_GROUP));
				group.setDescription(value);
                
                items.add(group);
            }
        } catch (NamingException e) {           
            throw new BusinessException("获取外部用户组失败！",e);
        }
        return items;
    }

    public List<?> getOtherUsers(Map<String, String> paramsMap, String attributes, String groupId, Object...otherParams) {
        String filterString =  otherParams.length > 0 ? (String)otherParams[0] : USER_FILTER_STR;
        
        Document doc = XMLDocUtil.dataXml2Doc(attributes);
        Map<String, String> fieldNames = new HashMap<String, String>();
        Map<String, String> defaultValues = new HashMap<String, String>();
        
        for (Iterator<?> it = doc.getRootElement().elementIterator(); it.hasNext();) {
            Element element = (Element) it.next();
            fieldNames.put(element.getName(), element.getText());
            defaultValues.put(element.getName(), element.attribute(DEFAULT_VALUE).getText());
        }
        
        List<UserDTO> items = new ArrayList<UserDTO>();
        Set<String> loginNameSet = new HashSet<String> ();
        Set<String> dnCache = new HashSet<String> ();
        // 数据查询
        try {
        	DirContext conn =  getConnection(paramsMap);
            NamingEnumeration<SearchResult> en = ldapSearch(conn, groupId, filterString);         
            while (en != null && en.hasMoreElements()) {
                SearchResult sr = en.next();
                String dn = sr.getName();
                
                // 组合全路径
                dn = dn + "," + groupId;
                if(dnCache.contains(dn)) continue;
                
                Attributes attrs = sr.getAttributes();
                
                if (attrs.get(SN_TAG) == null){
                    continue;
                }
                
                UserDTO user = new UserDTO();
                user.setId(dn);
                user.setGroupId(getGroupId(dn));                
                user.setUserName( getNameValueFromAttribute( attrs, SN_TAG ) );
                
                // 获得用户的属性              
                // loginName
                String uid_in_ldap = getNameValueFromAttribute(attrs, fieldNames.get(LOGIN_NAME_USER));
                if (uid_in_ldap != null) { // uid简称 有可能重名，重名只导入第一个
                    if(loginNameSet.contains(uid_in_ldap)) {
                        continue;
                    }
                    user.setLoginName(uid_in_ldap);
                } 
                else {
                    user.setLoginName(dn);
                }
                
                // email
                String emailName = fieldNames.get(EAMIL_USER);
                String emailValue = getValueFromAttribute(attrs, emailName);
                user.setEmail(emailValue);
 
                // sex
                String sexName = fieldNames.get(SEX_USER);
            	String sexValue = getValueFromAttribute(attrs, sexName);
            	user.setSex(sexValue);
				

                // birthday
				String birthdayName = fieldNames.get(BIRTHDAY_USER);
				String birthdayValue = getValueFromAttribute(attrs, birthdayName);
            	user.setBirthday(DateUtil.parse(birthdayValue));
				
                // employeeNo
				String employeeNoName = fieldNames.get(EMPLOYEE_NO_USER);
                user.setEmployeeNo(defaultValues.get(employeeNoName));
                
                // disabled
                String disabled = fieldNames.get(USER_STATUS);
                user.setAuthMethod(defaultValues.get(disabled));
                
                items.add(user);
                dnCache.add(dn);
                loginNameSet.add(user.getLoginName());
            }
        } catch (NamingException e) {           
            throw new BusinessException("获取外部用户失败！",e);
        }
        return items;
    }

    /**
     * <p>
     * LDAP查询
     * </p>
     * @param ctx
     * @param searchBase
     * @param filterString
     * @return
     */
    private NamingEnumeration<SearchResult> ldapSearch(DirContext ctx, String searchBase, String filterString) {
        SearchControls constraints = new SearchControls();
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        try {
            return ctx.search(searchBase, filterString, constraints);
        } catch (Exception e) {
            throw new BusinessException("ldap search failed, please check parameters!", e);
        } 
    }

    /**
     * @param dn 
     *         类似：cn=李文斌,ou=行政政法处,ou=省厅,o=gzcz
     * @return 
     *         ou=行政政法处,ou=省厅,o=gzcz
     */
    String getGroupId(String dn) {
        int position = -1;
        if ((position = dn.indexOf(OU_TAG)) >= 0) {
            return dn.substring(position);
        }
        return null;
    }
    
    /**
     * @param dn 
     *         类似：cn=李文斌,ou=行政政法处,ou=省厅,o=gzcz
     * @return 
     *         行政政法处
     */
    String getGroupName(String dn) {
        int position = dn.indexOf(OU_TAG);
        if (position >= 0) {
            String temp = dn.substring(position);
            if ((position = temp.indexOf(",")) >= 0) {
                return temp.substring(3, position); //=号和,号之间的就是groupName
            } 
        } 
        return null;
    }

    /**
     * @param dn 
     *         类似：ou=行政政法处,ou=省厅,o=gzcz
     * @return 
     *         ou=省厅,o=gzcz
     */
    String getParentGroupId(String dn) {
        int position = dn.indexOf(OU_TAG);
        if (position >= 0) {
            String selfId = dn.substring(position);
            if ((position = selfId.indexOf("," + OU_TAG)) >= 0) {
                return selfId.substring(position + 1);
            } 
        }
        return null;
    }
    
    private String getValueFromAttribute(Attributes attrs, String attrName){
    	javax.naming.directory.Attribute attr = attrs.get(attrName);
    	if( attr == null ) {
    		return null;
    	}
    	String attrString = attr.toString();
        return attrString.substring(attrString.indexOf(":") + 1);
    }

    private String getNameValueFromAttribute(Attributes attrs, String attrName){
    	javax.naming.directory.Attribute attr = attrs.get(attrName);
    	if( attr == null ) {
    		return null;
    	}
    	String attrString = attr.toString();
        return attrString.substring(attrString.indexOf(":") + 2);
    }
}
