package com.boubei.tss.portal.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.boubei.tss.framework.persistence.IEntity;
import com.boubei.tss.framework.web.display.tree.ITreeNode;
import com.boubei.tss.framework.web.display.tree.TreeAttributesMap;
import com.boubei.tss.framework.web.display.xform.IXForm;
import com.boubei.tss.util.BeanUtil;

/** 
 * 门户发布信息表
 */
@Entity
@Table(name = "portal_release_config", uniqueConstraints = { 
        @UniqueConstraint(name="MULTI_NAME_RELEASE_CONFIG", columnNames = { "name" })
})
@SequenceGenerator(name = "releaseconfig_sequence", sequenceName = "releaseconfig_sequence", initialValue = 1)
public class ReleaseConfig implements IEntity, ITreeNode, IXForm {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "releaseconfig_sequence")
    private Long   id;
	
	@Column(nullable = false)
    private String name;
	private String remark;
	
    @Column(nullable = false)
    private String visitUrl;  // 门户访问地址
	
	@ManyToOne
    private Structure portal; // 对应门户
    
	@ManyToOne
    private Theme   theme;    // 指定的主题
    
	@ManyToOne
    private Structure page;   // 对应页面
 
    public String getName() {
        return name;
    }
 
    public String getRemark() {
        return remark;
    }
 
    public Long getId() {
        return id;
    }
 
    public String getVisitUrl() {
        return visitUrl;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public void setVisitUrl(String visitUrl) {
        this.visitUrl = visitUrl;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public void setRemark(String reamrk) {
        this.remark = reamrk;
    }
    
    public Map<String, Object> getAttributes4XForm() {
        Map<String, Object> map = new HashMap<String, Object>();
        BeanUtil.addBeanProperties2Map(this, map, "portal", "theme", "page");
        
        map.put("portal.id", portal.getId());
        map.put("portal.name", portal.getName());
        
        if(theme != null) {
            map.put("theme.id", theme.getId());
        }
        
        if(page != null) {
            map.put("page.id", page.getId());
            map.put("page.name", page.getName());
        }
        return map;
    }
    
    public TreeAttributesMap getAttributes() {
        TreeAttributesMap map = new TreeAttributesMap(id, name);
        map.put("icon", "images/url.gif");
        return map;
    }

    public Structure getPortal() {
        return portal;
    }

    public void setPortal(Structure portal) {
        this.portal = portal;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public Structure getPage() {
        return page;
    }

    public void setPage(Structure page) {
        this.page = page;
    }
    
	public Serializable getPK() {
		return this.id;
	}
}

