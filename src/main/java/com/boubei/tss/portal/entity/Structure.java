package com.boubei.tss.portal.entity;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.boubei.tss.framework.persistence.entityaop.IDecodable;
import com.boubei.tss.framework.persistence.entityaop.OperateInfo;
import com.boubei.tss.framework.web.display.tree.TreeAttributesMap;
import com.boubei.tss.framework.web.display.xform.IXForm;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.portal.PortalConstants;
import com.boubei.tss.portal.entity.permission.PortalResource;
import com.boubei.tss.um.permission.IResource;
import com.boubei.tss.util.BeanUtil;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.URLUtil;

/**
 * 门户结构实体：自引用结构，不同的节点分别代表Portal、页面、版面、Portlet实例等
 * <br>
 * |-门户_1 <br>
 * ........|- 页面_1 <br>
 * ................|- portlet应用_1_1 <br>
 * ................|- 版面_1_1 <br>
 * ..........................|- 版面_1_1_2 <br>
 * ......................................|- portlet应用_1_1_2_1 <br>
 * ..........................|- portlet应用_1_1_2 <br>
 * ........|- 页面_1 <br>
 * ........|- 页面_2 <br>
 */
@Entity
@Table(name = "portal_structure", uniqueConstraints = { 
        @UniqueConstraint(name="MULTI_NAME_Structure", columnNames = { "parentId", "name" })
})
@SequenceGenerator(name = "structure_sequence", sequenceName = "structure_sequence", initialValue = 1)
public class Structure extends OperateInfo implements IXForm, IResource, IDecodable {
    
    public static final int TYPE_PORTAL           = 0; //Portal节点
    public static final int TYPE_PAGE             = 1; //页面节点
    public static final int TYPE_SECTION          = 2; //版面节点
    public static final int TYPE_PORTLET_INSTANCE = 3; //Portlet实例节点
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "structure_sequence")
    private Long id;  
	
	@Column(nullable = false)
    private Long parentId; // 父节点编号
	
	private Long portalId; // 门户根节点ID，如果是根节点自身，则和id值一致
    
    /**
     * 节点类型：
     * <li>0－结构根节点（相当于门户，但也是一个版面）；
     * <li>1－页面：decorator表示修饰器、definer表示布局器
     * <li>2－版面：decorator表示修饰器、definer表示布局器
     * <li>3－Portlet实例：decorator表示修饰器、definer表示Portlet
     */
    @Column(nullable = false)
    private Integer type;
    
    @Column(nullable = false)
    private String  name;   // 节点名称：门户名称/页面名称/版面名称/Portlet实例名称
    private String  code;   // 门户结构节点的代码
    private String  description; // 描述信息
    
    @Column(length = 4000)
    private String supplement;  // Portal和页面全局附加脚本和样式表定义信息
    
    /**
     * 门户根节点信息：主题信息等  ------------------------------------------------------------------------------
     */
    @ManyToOne
    private Theme theme;   // 默认主题
	
    @ManyToOne
    private Theme currentTheme; // 当前主题
   
    /**
     * 门户结构（页面、版面、portlet实例）信息：具体的布局和portlet实例  ---------------------------------------------
     */
    @ManyToOne
    private Component definer; // 布局器/Portlet
    
    /**
     * 修饰器信息不需要对应数据库，修饰器信息在主题信息表中
     */
    @Transient 
    private Component decorator; // 修饰器

    @Column(length = 1000)
    private String parameters;  // Portlet、修饰器实例化时自定义参数值
    
    @Column(nullable = false)
    private Integer seqNo;   // 顺序号
    private String  decode;  // 层码
    private Integer levelNo; // 层次值

    private Integer disabled = ParamConstants.FALSE;  // 是否停用：0－启用；1－停用
    
    public String toString(){
        return "(id:" + this.id + ", name:" + this.name + 
                ", code:" + this.code +  
        		", parentId:" + this.parentId + ")"; 
    }
    
    @Transient Collection<Structure> children = new LinkedHashSet<Structure>();

    public Collection<Structure> getChildren() {
        return children;
    }

    public void addChild(Structure ps) {
        children.add(ps);
    }
 
    public boolean isRootPortal() { return TYPE_PORTAL == this.type.intValue(); }
    public boolean isPage()       { return TYPE_PAGE == this.type.intValue(); }
    public boolean isSection()    { return TYPE_SECTION == this.type.intValue(); }
    public boolean isPortletInstanse() { return TYPE_PORTLET_INSTANCE == this.type.intValue(); }

    public File getPortalResourceFileDir(){
        URL url = URLUtil.getWebFileUrl(PortalConstants.PORTAL_MODEL_DIR);
        return new File(url.getPath() + "/" + code);
    }
 
    public static File getPortalResourceFileDir(String path){
        URL url = URLUtil.getWebFileUrl(PortalConstants.PORTAL_MODEL_DIR);
        return new File(url.getPath() + "/" + path);
    }
    
    public Map<String, Object> getAttributes4XForm() {
        Map<String, Object> map = new HashMap<String, Object>();
        BeanUtil.addBeanProperties2Map(this, map, "children,theme,currentTheme,menus,definer,decorator".split(","));
        
        if(theme != null) {
            map.put("theme.id", theme.getId());
            map.put("theme.name", theme.getName());
        }
        if(currentTheme != null) {
            map.put("currentTheme.id", currentTheme.getId());
            map.put("currentTheme.name", currentTheme.getName());
        }
        if(definer != null) {
            map.put("definer.id", definer.getId());
            map.put("definer.name", definer.getName());
        }
        if(decorator != null) {
            map.put("decorator.id", decorator.getId());
            map.put("decorator.name", decorator.getName());
        }
        return map;
    }

    public TreeAttributesMap getAttributes() {
        TreeAttributesMap map = new TreeAttributesMap(id, name);
        map.put("code", code);
        map.put("type", type);
        map.put("portalId", portalId);
        map.put("disabled", disabled);
        map.put("resourceTypeId", getResourceType());

        switch (type) {
        case 0:
            map.put("icon", "images/portal" + "_" + disabled + ".gif");
            break;
        case 1:
            map.put("icon", "images/page" + "_" + disabled + ".gif");
            break;
        case 2:
            map.put("icon", "images/section" + "_" + disabled + ".gif");
            break;
        case 3:
            map.put("icon", "images/portlet_instance" + "_" + disabled + ".gif");
            break;
        }
        map.put("_open", String.valueOf( (this.description+"").indexOf("open") >= 0 || this.levelNo <= 2) );
        
        return map;
    }
    
    public String getResourceType() {
        return PortalConstants.PORTAL_RESOURCE_TYPE;
    }
    
    public Class<?> getParentClass() {
        if(this.parentId.equals(PortalConstants.ROOT_ID)) {
            return PortalResource.class;
        }
        return getClass();
    }
 
    public String getDecode() {
        return decode;
    }
 
    public Integer getDisabled() {
        return disabled;
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
 
    public void setLevelNo(Integer levelNo) {
        this.levelNo = levelNo;
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
 
    public Long getParentId() {
        return parentId;
    }
 
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
 
    public Integer getSeqNo() {
        return seqNo;
    }
 
    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }
 
    public Integer getType() {
        return type;
    }
 
    public void setType(Integer type) {
        this.type = type;
    }
 
    public String getDescription() {
        return description;
    }
 
    public void setDescription(String description) {
        this.description = description;
    }
 
    public Long getPortalId() {
        return portalId;
    }
 
    public void setPortalId(Long portalId) {
        this.portalId = portalId;
    }
 
    public String getParameters() {
        return parameters;
    }
 
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
 
    public String getCode() {
        return code;
    }
 
    public void setCode(String code) {
        this.code = code;
    }
 
    public String getSupplement() {
        return supplement;
    }
 
    public void setSupplement(String supplement) {
        this.supplement = supplement;
    }
    
    public String getDefaultKey() {
		return PortalConstants.PORTAL_CACHE + "-"
				+ EasyUtils.checkTrue(isRootPortal(), this.id, this.portalId) + "-"
				+ theme.getId();
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public void setCurrentTheme(Theme currentTheme) {
        this.currentTheme = currentTheme;
    }

    public Component getDefiner() {
        return definer;
    }

    public void setDefiner(Component definer) {
        this.definer = definer;
    }

    public Component getDecorator() {
        return decorator;
    }

    public void setDecorator(Component decorator) {
        this.decorator = decorator;
    }
    
	public Serializable getPK() {
		return this.id;
	}
}