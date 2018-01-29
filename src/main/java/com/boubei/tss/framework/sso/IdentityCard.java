package com.boubei.tss.framework.sso;

import java.io.Serializable;

/**
 * <P>
 * 用户身份证书对象，包含用户相关信息对象 和 用户令牌
 * </P>
 */
public class IdentityCard implements Serializable{

	private static final long serialVersionUID = 7946643134139485776L;

    /** 用户信息 */
    private IOperator operator;
    
    /** 用户令牌: sessionId + "," + System.currentTimeMillis() + "," + userId  */
    private String token;

    /**
     * 根据用户信息创建身份对象
     * @param token    用户令牌
     * @param operator      用户对象，不能为Null
     */
    public IdentityCard(String token, IOperator operator) {
        this.token = token;
        this.operator = operator;
    }

    /**
     * 获取用户详细信息对象
     * @return Returns the operator.
     */
    public IOperator getOperator() {
        return operator;
    }

    /**
     * 获取用户令牌
     * @return Returns the token.
     */
    public String getToken() {
        return token;
    }

    /**
     * 获取身份对象ID（用户ID）
     * @return Returns the id.
     */
    public Long getId() {
        return operator.getId();
    }

    /**
     * 获取用户登录名
     * @return Returns the loginName.
     */
    public String getLoginName() {
        return operator.getLoginName();
    }

    /**
     * 获取用户名
     * @return Returns the userName.
     */
    public String getUserName() {
        return operator.getUserName();
    }

    /**
     * 判断用户是否匿名用户
     * @return
     */
    public boolean isAnonymous() {
        return operator.isAnonymous();
    }

    public String toString() {
        return "IdentityCard LoginName: " + getLoginName();
    }
}
