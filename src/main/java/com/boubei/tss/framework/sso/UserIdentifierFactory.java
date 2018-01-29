package com.boubei.tss.framework.sso;

import java.util.HashMap;
import java.util.Map;

import com.boubei.tss.util.BeanUtil;

/** 
 * <p> 生成并维护用户合法认证器 </p> 
 * 
 */
public class UserIdentifierFactory {

    static UserIdentifierFactory factory = null;

    static Map<String, IUserIdentifier> cache = new HashMap<String, IUserIdentifier>();

    private UserIdentifierFactory() {
    }

    /**
     * 获取认证类
     * @return
     */
    public IUserIdentifier getUserIdentifier(String className) {
        IUserIdentifier identifier = (IUserIdentifier) cache.get(className);
        if (identifier == null) {
            cache.put(className, identifier = (IUserIdentifier) BeanUtil.newInstanceByName(className));
        }
        return identifier;
    }

    /**
     * 实例化认证类工厂本身
     * @return
     */
    public static UserIdentifierFactory instance() {
        if (factory == null) {
            factory = new UserIdentifierFactory();
        }
        return factory;
    }
}
