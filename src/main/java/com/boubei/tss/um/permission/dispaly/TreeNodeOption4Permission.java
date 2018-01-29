package com.boubei.tss.um.permission.dispaly;

import java.util.Map;

import com.boubei.tss.framework.web.display.tree.TreeNodeOption;

/**
 * 树节点操作权限选项：新增、删除、停用、启用等，每个为一操作权限选项。
 * 本类只展示attributes里的属性值，超类TreeNodeOption的不展示。
 */
public class TreeNodeOption4Permission extends TreeNodeOption {

    Map<String, Object> optionAttributes; 

    public TreeNodeOption4Permission(IPermissionOption data) {
        optionAttributes = data.getOptionAttributes();
    }

    /* 
     * 生成的数据格式如：
     * <option>
     *      <id>3</id>
     *      <text>删除</text>
     *      <dependId>1</dependId>
     * </option>
     */
    public String toXml() {
        StringBuffer sb = new StringBuffer();
        if (optionAttributes != null) {
            sb.append("<option>");
            for (Object key : optionAttributes.keySet()) {
                Object value = optionAttributes.get(key);
                sb.append("<" + key + ">").append(null != value ? value : "").append("</" + key + ">");
            }
            sb.append("</option>");
        }
        return sb.toString();
    }

}
