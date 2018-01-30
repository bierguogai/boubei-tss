/* ==================================================================   
 * Created [2016-3-5] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */

package com.boubei.tss.dm.report;

import com.boubei.tss.framework.Config;
import com.boubei.tss.util.BeanUtil;
 
public class ScriptParserFactory {
    
    protected static ScriptParser instance;
 
    public static ScriptParser getParser() {
        if (instance == null) {
            String configValue = Config.getAttribute("script_precheator");
            if (configValue != null) {
                instance = (ScriptParser) BeanUtil.newInstanceByName(configValue);
            } 
        }
        return instance;
    }
}
