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
