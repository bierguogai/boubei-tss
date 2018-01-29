package com.boubei.tss.framework.sso;

import java.util.ArrayList;
import java.util.List;

import com.boubei.tss.framework.Config;
import com.boubei.tss.util.BeanUtil;
import com.boubei.tss.util.EasyUtils;

/**
 * <p>
 * 登录自定义器工厂
 * </p>
 */
public class LoginCustomizerFactory {

    /** 自定义器实体 */
    protected static ILoginCustomizer customizer;

    /** 工厂类实体 */
    private static LoginCustomizerFactory factory;

    /**
     * <p>
     * 获取自定义器
     * </p>
     * @return ILoginCustomizer 自定义器
     */
    public ILoginCustomizer getCustomizer() {
        if (customizer == null) {
            String className = Config.getAttribute(SSOConstants.LOGIN_COSTOMIZER);
            className = (String) EasyUtils.checkNull(className, DoNothingLoginCustomizer.class.getName());
            String[] classNames = className.split(",");
            customizer = new ArrayLoginCustomizer(classNames);
        }
        return customizer;
    }

    /**
     * <p>
     * 实例化工厂类
     * </p>
     * @return
     */
    public static LoginCustomizerFactory instance() {
        if (factory == null) {
            factory = new LoginCustomizerFactory();
        }
        return factory;
    }
    
    public static void destroy() {
    	factory = null;
    	customizer = null;
    }
}

/** 
 * <p>
 * 登录时参数自定义器：
 * 可以同时执行多个定义的登录时参数自定义器，定义方法为使用逗号格开多个自定义器全类名
 * </p>
 */
class ArrayLoginCustomizer implements ILoginCustomizer {

    /**
     * 自定义器列表
     */
    private List<ILoginCustomizer> customizers;

    /**
     * 自定义器类名数组
     */
    private String[] classNames;
 
    public ArrayLoginCustomizer(String[] classNames) {
        this.classNames = classNames;
    }
 
    public void execute() {
        if (customizers == null) {
            init();
        }

        for(ILoginCustomizer customizer : customizers) {
        	customizer.execute();
        }
    }

    /**
     * <p>
     * 初始化自定义器列表
     * </p>
     */
    private void init() {
        customizers = new ArrayList<ILoginCustomizer>();
	    for (String className : classNames) {
            Object customizer = BeanUtil.newInstanceByName(className);
            customizers.add( (ILoginCustomizer) customizer );
        }
    }
}
