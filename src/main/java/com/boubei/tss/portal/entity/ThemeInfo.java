package com.boubei.tss.portal.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.boubei.tss.framework.persistence.IEntity;

/** 
 * 主题信息表,包含主题下各个门户结构的布局修饰内容
 */
@Entity
@Table(name = "portal_theme_info")
public class ThemeInfo implements IEntity {
	
	/**
	 *  复合主键：主题ID ＋ 门户结构ID
	 */
	@EmbeddedId
    private ThemeInfoId id;
    
    /** 
     * 如果门户结构为portlet实例，则此项为空 
     */
	@ManyToOne
    private Component layout;      // 布局器
    
	@ManyToOne
    private Component decorator;   // 修饰器
    
    /**
     * Portlet、修饰器、布局器的实例化时自定义参数值
     */
	@Column(length = 1000)
    private String  parameters;

	public ThemeInfoId getId() {
		return id;
	}
	
    public void setId(ThemeInfoId id) {
        this.id = id;
    }
    
	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	
    public Component getLayout() {
        return layout;
    }

    public void setLayout(Component layout) {
        this.layout = layout;
    }

    public Component getDecorator() {
        return decorator;
    }

    public void setDecorator(Component decorator) {
        this.decorator = decorator;
    }
	
	@Embeddable
	public static class ThemeInfoId  implements Serializable {
		
	    private static final long serialVersionUID = -2913245080847049534L;
	    
	    private Long    themeId;     // 主题ID
	    private Long    structureId; // 门户结构ID
	    
	    public ThemeInfoId(){
	    }
	    
	    public ThemeInfoId(Long themeId, Long structureId){
	        this.setThemeId(themeId);
	        this.setStructureId(structureId);
	    }
	  
	    public Long getThemeId() {
			return themeId;
		}

		public Long getStructureId() {
			return structureId;
		}

		public void setThemeId(Long themeId) {
			this.themeId = themeId;
		}

		public void setStructureId(Long structureId) {
			this.structureId = structureId;
		}

		public boolean equals(Object obj) {
	        if(obj instanceof ThemeInfoId){
	            ThemeInfoId object = (ThemeInfoId) obj;
	            return this.themeId.equals(object.getThemeId())
	                    && this.structureId.equals(object.getStructureId());
	        }
	        return false;
	    }

	    public int hashCode() {
	        int hash = 31 + this.themeId.hashCode();
	        hash = hash * 31 + this.structureId.hashCode();
	        return hash;
	    }
	}
	
	public Serializable getPK() {
		return this.id;
	}
}