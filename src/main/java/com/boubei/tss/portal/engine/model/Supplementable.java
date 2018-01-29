package com.boubei.tss.portal.engine.model;

import java.util.List;

/**
 * 可外挂js、css等文件的节点。
 * 现有PortalNode、PageNode可外挂配置文件，同时也可以直接在上面定义公用js、css脚本。
 */
public interface Supplementable {

    /**
     * 获取公用的js脚本
     */
    String getScriptCode();

    /**
     * 获取公用的css脚本
     */
    String getStyleCode();

    /**
     * 获取公用的外挂js文件列表
     */
    List<String> getScriptFiles();

    /**
     * 获取公用的外挂css文件列表
     */
    List<String> getStyleFiles();

    /**
     * 设置公用的js脚本
     */
    void setScriptCode(String scriptCode);

    /**
     * 设置公用的css脚本
     */
    void setStyleCode(String styleCode);

}
