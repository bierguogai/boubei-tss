/* ==================================================================   
 * Created [2009-08-29] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.um.helper;

import com.boubei.tss.util.EasyUtils;

/**
 * 定义密码规则。
 */
public class PasswordRule {
	
	public static final int UNQUALIFIED_LEVEL = 0;	//不可用
	public static final int LOW_LEVEL         = 1;	//低
	public static final int MEDIUM_LEVEL      = 2;	//中
	public static final int HIGHER_LEVEL      = 3;	//高
	
	private int leastLength;		 // 最短长度
	private int leastStrength;	     // 最低强度
	private int lowStrength;	     // 低强度临界值
	private int higherStrength;      // 高强度
	
	private boolean canEq2LoginName; // 是否可以和用户名相同
	private String impermissible;    // 不允许的密码，用","隔开
 
	public static PasswordRule getDefaultPasswordRule(){
		PasswordRule rule = new PasswordRule();
		rule.canEq2LoginName = false;
		rule.leastLength    = 6;
		rule.leastStrength  = 8;
		rule.lowStrength    = 16;
		rule.higherStrength = 60;
		rule.impermissible  = "123, 1234, 123456, 11111";
		return rule;
	}
 
	// 密码强度定义.
    static int factor[] = {1, 2, 3, 4};
    static int kindFactor[] = {0, 0, 30, 50, 70};
    static String[] regex = {"0123456789", 
                             "abcdefghijklmnopqrstuvwxyz", 
                             "ABCDEFGHIJKLMNOPQRSTUVWXYZ", 
                             "~`!@#$%^&*()-=_+,./<>?;:|"};

    static int getStrengthValue(String pwd) {
        int strengthValue = 0;
        int composedKind = 0;
        for (int i = 0; i < regex.length; i++) {
            int matched = 0;
            for (int j = 0; j < pwd.length(); j++) {
                if (regex[i].indexOf(pwd.charAt(j)) >= 0)
                    matched++;
            }
 
            if (matched != 0) {
                strengthValue += matched * factor[i];
                composedKind++;
            }
        }
        strengthValue += kindFactor[composedKind];
        return strengthValue;
    }
    
	public static int getStrengthLevel(String password, String loginName) {
		PasswordRule rule = getDefaultPasswordRule();

		password =  EasyUtils.obj2String(password);
		int flag = checkAvailable(rule, password);
		
		// 如果不允许登录名和密码相同 则将相同的设为不可用
		if( !rule.canEq2LoginName && password.equals(loginName)){
			flag = 0;
        }
		
        int strength = PasswordRule.getStrengthValue(password);
        if(flag == 0) {
            return PasswordRule.UNQUALIFIED_LEVEL;
        } 
        if(strength < rule.leastStrength) {
            return PasswordRule.UNQUALIFIED_LEVEL;
        }
        if(strength < rule.lowStrength) {
            return PasswordRule.LOW_LEVEL;
        }
        if(strength < rule.higherStrength) {
            return PasswordRule.MEDIUM_LEVEL;
        } 
        return PasswordRule.HIGHER_LEVEL;
	}
	
	static int checkAvailable(PasswordRule rule, String password){
		int flag = 1;
		
		// 密码长度小于要求的最低长度 则设为不可用
		if(password.length() < rule.leastLength) {
			flag = 0;
		}
		
		// 密码和禁用密码相同，则设为不可用
		rule.impermissible = EasyUtils.obj2String(rule.impermissible);
		String[] impermissibles = rule.impermissible.split(",");
		for(int i = 0; i < impermissibles.length; i++ ){
			if(password.equals(impermissibles[i].trim())) {
				flag = 0;
			}
		}
			
		return flag;
	}
}
