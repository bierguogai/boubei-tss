package com.boubei.tss.um.permission.dispaly;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boubei.tss.framework.web.display.tree.ILevelTreeNode;
import com.boubei.tss.framework.web.display.tree.TreeAttributesMap;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.util.EasyUtils;

public class ResourceTreeNode implements ILevelTreeNode, IPermissionOption, Serializable {

    private static final long serialVersionUID = -2307491586761933842L;

    private Long resourceId;       // 资源Id
    private Long parentResourceId; // 资源父节点Id
    private String resourceName;   // 资源名称

    public ResourceTreeNode(Object[] resourceInfo) {
        this.resourceId = EasyUtils.obj2Long(resourceInfo[0]);
        this.parentResourceId = EasyUtils.obj2Long(resourceInfo[1]);
        this.resourceName = (String) resourceInfo[2];
    }

    public static List<ResourceTreeNode> genResourceTreeNodeList(List<?> resourceList) {
        List<ResourceTreeNode> result = new ArrayList<ResourceTreeNode>();
        for (Object resourceInfo : resourceList) {
            result.add(new ResourceTreeNode((Object[]) resourceInfo));
        }
        return result;
    }
    
    public TreeAttributesMap getAttributes() {
        TreeAttributesMap map = new TreeAttributesMap(resourceId, resourceName);
        map.put("parentId", this.parentResourceId);
        map.put("icon", UMConstants.RESOURCE_TREENODE_ICON);
        return map;
    }

    /**
     * 用以记录授权信息（各个权限选项的打勾情况：opt3=1。。。 和 权限维护状态：pstate = (1-仅此节点，2-该节点及所有下层节点)等）
     */
    private Map<String, Object> optionInfoMap = new HashMap<String, Object>();

    public void putOptionAttribute(String key, Object value) {
        optionInfoMap.put(key, value);
    }

    public Map<String, Object> getOptionAttributes() {
        return optionInfoMap;
    }
 
    public Long getResourceId() {
        return resourceId;
    }
 
    public String getResourceName() {
        return resourceName;
    }
 
    public Long getParentId() {
        return parentResourceId;
    }

    public Long getId() {
        return resourceId;
    }
}
