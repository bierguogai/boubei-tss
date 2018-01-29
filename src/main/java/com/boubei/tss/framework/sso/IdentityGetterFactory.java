package com.boubei.tss.framework.sso;

import com.boubei.tss.framework.Config;
import com.boubei.tss.util.BeanUtil;

/**
 * <p>
 * 身份读取器工厂类
 * </p>
 */
public class IdentityGetterFactory {
    
    /** 用户身份转换器 */
    public static IdentityGetter getter;

    /**
     * 获取身份转换对象
     * @return
     */
    public static IdentityGetter create() {
        if (getter == null) {
            String cn = Config.getAttribute(SSOConstants.IDENTITY_GETTER);
            getter = (IdentityGetter) BeanUtil.newInstanceByName(cn);
        }
        
        return getter;
    }
}
