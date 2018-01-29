package com.boubei.tss.portal.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.boubei.tss.framework.persistence.IEntity;
import com.boubei.tss.framework.persistence.entityaop.IDecodable;
import com.boubei.tss.framework.persistence.entityaop.OperateInfo;
import com.boubei.tss.framework.web.display.tree.ILevelTreeNode;
import com.boubei.tss.framework.web.display.tree.TreeAttributesMap;
import com.boubei.tss.framework.web.display.xform.IXForm;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.portal.PortalConstants;
import com.boubei.tss.util.BeanUtil;

/**
 * 门户组件：定义门户组件的基本信息及内容。
 * 包括布局器、修饰器、portlet。
 */
@Entity
@Table(name = "portal_component", uniqueConstraints = { 
        @UniqueConstraint(name="MULTI_NAME_COMPONENT", columnNames = { "parentId", "type", "name" })
})
@SequenceGenerator(name = "component_sequence", sequenceName = "component_sequence", initialValue = 1)
public class Component extends OperateInfo implements IEntity, ILevelTreeNode, IXForm, IDecodable {
	
	public final static int LAYOUT_TYPE    = 1;
    public final static int DECORATOR_TYPE = 2;
    public final static int PORTLET_TYPE   = 3;
    
    public final static String LAYOUT    = "layout";
    public final static String DECORATOR = "decorator";
    public final static String PORTLET   = "portlet";
    public final static String[] TYPE_NAMES = new String[]{LAYOUT, DECORATOR, PORTLET};
    
    public final static String PARAM_FILE = "paramsXForm.xml";
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "component_sequence")
    private Long    id;
	
	@Column(nullable = false)
    private String  name; // 元素名称
	
	/** 
	 * 元素类别： 1-布局器(组), 2-修饰器(组), 3-Portlet(组)
	 */
	private Integer type;
	
	@Column(nullable = false)
	private Long    parentId; // 父组的编码：根节点的parentId = 0;
	private boolean isGroup = false;  // 是否为元素组
	
    @Column(length = 4000)
    private String  definition;  // 元素内容：元素关于展现方式的具体定义信息
    
    @Column(length = 1000)
    private String  description; // 元素的描述信息
    private String  version;     // 版本号
    
    @Column(nullable = false)
    private Integer seqNo;    // 顺序号：用于排序
    private String  decode;   // 层码
    private Integer levelNo;  // 层次值
    
    private Integer isDefault = ParamConstants.FALSE; // 是否为默认（修饰器/布局器）
    private Integer disabled  = ParamConstants.FALSE; // 是否停用
    
    
    public String getResourceBaseDir() { 
        return PortalConstants.MODEL_DIR + getComponentType() + "/"; 
    }
    
    public String getResourcePath()    { 
        return getResourceBaseDir() + getCode(); 
    }
    
    public String getTemplatePath()    { 
        return "template/portal/C-" + getComponentType() + ".xml";
    }
    
    public String getComponentType()     { 
        return TYPE_NAMES[type - 1]; 
    }
    
    public boolean isLayout() {
        return this.type == LAYOUT_TYPE;
    }
    
    public boolean isDecorator() {
        return this.type == DECORATOR_TYPE;
    }
    
    public boolean isportlet() {
        return this.type == PORTLET_TYPE;
    }
    
    // 元素代码：用于生成元素资源文件目录及访问相对路径
    public String getCode() {
        return this.getComponentType() + "-" + this.getId();
    }
    
    /**
     * 布局器中可以显示区域数量
     * <li> n > 0 － 此布局器有多少个区域可以填充子节点，用于判断是否适用版面
     * <li> -n    － 子节点循环填充每个区域；
     * 
     * 注：当portNumber > ${port*}数量时有问题，创建布局器的时候portNumber和html中${port*}数量必须相等。
     */
    @Column(nullable = false)
    private Integer portNumber = 0;
    
    public TreeAttributesMap getAttributes() {
        TreeAttributesMap map = new TreeAttributesMap(id, name);
        map.put("type", type);
        map.put("parentId", parentId);
        map.put("isGroup", isGroup);
        map.put("disabled", disabled);
        
        if(isGroup) {
        	map.put("icon","images/folder.gif");
        } else {
        	map.put("code", this.getCode());
            map.put("icon", "images/" 
            		+ (ParamConstants.TRUE.equals(isDefault) ? "default_" : "") + getComponentType() 
            		+ "_" + disabled + ".gif");
        }
        map.put("_open", String.valueOf( (this.description+"").indexOf("open") >= 0) );
        
        return map;
    }

    public Map<String, Object> getAttributes4XForm() {
        Map<String, Object> map = new HashMap<String, Object>();
        BeanUtil.addBeanProperties2Map(this, map);

        return map;
    }

    public Integer getType() {
		return type;
	}
    
	public void setType(Integer type) {
		this.type = type;
	}
	
	public Long getParentId() {
		return parentId;
	}
	
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
	public boolean isGroup() {
		return isGroup;
	}
	
   public boolean getIsGroup() {
        return isGroup;
    }
	
	public void setIsGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}
	
	public Class<Component> getParentClass() { return Component.class; }
    
    public String toString(){
        return "(id:" + this.id + ", name:" + this.name + ")"; 
    }
 
    public String getVersion() {
        return version;
    }
 
    public void setVersion(String version) {
        this.version = version;
    }
 
    public String getDefinition() {
        return definition;
    }
 
    public void setDefinition(String definition) {
        this.definition = definition;
    }
 
    public String getDescription() {
        return description;
    }
 
    public void setDescription(String description) {
        this.description = description;
    }
 
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public Integer getSeqNo() {
        return seqNo;
    }
 
    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }
 
    public String getDecode() {
        return decode;
    }
  
    public Integer getDisabled() {
        return disabled;
    }
 
    public Integer getIsDefault() {
        return isDefault;
    }
 
    public Integer getLevelNo() {
        return levelNo;
    }
 
    public void setDecode(String decode) {
        this.decode = decode;
    }
 
    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }
 
    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }
 
    public void setLevelNo(Integer levelNo) {
        this.levelNo = levelNo;
    }
    
    public Integer getPortNumber() {
        return portNumber;
    }
 
    public void setPortNumber(Integer portNumber) {
        this.portNumber = portNumber;
    }
    
	public Serializable getPK() {
		return this.id;
	}
}
