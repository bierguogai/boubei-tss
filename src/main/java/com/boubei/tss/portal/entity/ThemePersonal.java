package com.boubei.tss.portal.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.boubei.tss.framework.persistence.IEntity;

/** 
 * 门户自定义主题信息实体
 */
@Entity
@Table(name = "portal_theme_personal")
@SequenceGenerator(name = "theme_personal_sequence", sequenceName = "theme_personal_sequence", initialValue = 1)
public class ThemePersonal implements IEntity {
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "theme_personal_sequence")
    private Long id;            //主键
	
    private Long portalId;      //定制页面所在门户ID
    private Long themeId;       //定制的主题ID
    private Long userId;        //定制人ID
    
    public ThemePersonal(){
    }
    
    public ThemePersonal(Long portalId, Long userId, Long themeId) {
        this.setPortalId(portalId);
        this.setThemeId(themeId);
        this.setUserId(userId);
    }
 
    public Long getId() {
        return id;
    }
 
    public Long getPortalId() {
        return portalId;
    }
 
    public Long getThemeId() {
        return themeId;
    }
 
    public Long getUserId() {
        return userId;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public void setPortalId(Long portalId) {
        this.portalId = portalId;
    }
 
    public void setThemeId(Long themeId) {
        this.themeId = themeId;
    }
 
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
	public Serializable getPK() {
		return this.id;
	}
}