package com.boubei.tss.dm.report;

import java.util.Map;
 
/**
 * 允许不同的BI项目对其下的脚本做统一的预处理，比如数据权限过滤等，可根据登录信息来获取权限情况，放入dataMap后织入到脚本里。
 */
public interface ScriptParser {
 
    String parse(String script, Map<String, Object> dataMap);

}
