package com.boubei.tss.um.service;

import java.util.Map;

import com.boubei.tss.framework.persistence.pagequery.PageInfo;
import com.boubei.tss.modules.log.Logable;
import com.boubei.tss.um.entity.User;
 
public interface IUserService {
    
    /**
     * <p>
     * 新建用户的页面需要的初始化数据
     * </p>
     * @param groupId
     * @return
     */
    Map<String, Object> getInfo4CreateNewUser(Long groupId);

    /**
     * <p>
     * 编辑用户的页面需要的初始化数据
     * </p>
     * @param userId
     * @return
     */
    Map<String, Object> getInfo4UpdateExsitUser(Long userId);
    
    /**
     * 更改用户
     * @param user
     */
    @Logable(operateObject="用户", operateInfo="修改用户 ${args[0]}")
    void updateUser(User user);

    /**
     * 根据ID查询用户
     * @param id
     * @return Object
     */
    User getUserById(Long id);

    /**
     * <p>
     * 新建/修改一个User对象的明细信息、用户对用户组信息、用户对角色的信息
     * </p>
     * @param user
     * @param groupIdsStr
     * @param roleIdsStr
     */
    @Logable(operateObject="用户", 
            operateInfo="新建/修改了 ${args[0]} , groups: ${args[1]}, roles: ${args[2]?default(\"\")}）"
        )
    void createOrUpdateUser(User user, String groupIdsStr, String roleIdsStr);

    /**
     * <p>
     * 删除用户 辅助组用户删除用户只删除对应关系； 
     * 主用户组和其他用户组的则完全删除用户及和和组的对应关系。
     * </p>
     * @param groupId
     * @param userId
     */
    @Logable(operateObject="用户",  
            operateInfo="删除了 (ID:${args[1]}) 用户"
        )
    void deleteUser(Long groupId, Long userId);

    /**
     * <p>
     * 用户密码统一初始化。如果指定了单独用户，则只初始化该用户的密码；否则初始化整个用户组的密码。
     * </p>
     * 
     * @param groupId
     * @param userId
     * @param initPassword
     */
    @Logable(operateObject="用户",  
            operateInfo="初始化（组ID:${args[0]}）下（用户ID:${args[1]}）的密码为：${args[2]}"
        )
    void initPasswordByGroupId(Long groupId, Long userId, String initPassword);

    /**
     * <p>
     * 统一认证方式
     * </p>
     * @param groupId
     * @param authMethod
     */
    void uniteAuthenticateMethod(Long groupId, String authMethod);

    /**
     * <p>
     * 根据用户登录名获取用户实体
     * </p>
     * @param loginName
     *            登录名
     * @return User 用户实体对象
     */
    User getUserByLoginName(String loginName);

    /**
     * <p>
     * 启用停用用户
     * </p>
     * @param loginUserId
     * @param userId
     * @param disabled
     * @param groupId
     */
    @Logable(operateObject="用户", operateInfo=" 启用/停用用户 (ID: ${args[0]}, disabled: ${args[1]}) ")
    void startOrStopUser(Long userId, Integer disabled, Long groupId);

    /**
     * <p>
     * 根据用户组ID获取所有的用户
     * </p>
     * @param groupId
     * @param pageNum 当前页数
     * @param orderBy
     * @return
     */
    PageInfo getUsersByGroupId(Long groupId, Integer pageNum, String orderBy);
    
    /**
     * <p>
     * 根据条件搜索用户(分页)
     * </p>
     * @param groupId
     * @param searchStr
     * @param page
     * @return
     */
    PageInfo searchUser(Long groupId, String searchStr, int page);
    
    /**
     * 处理过期的用户、角色、转授策略等
     */
    void overdue();

    /**
     * 用户自注册
     */
    @Logable(operateObject="用户注册", operateInfo=" 用户（${args[0]}）完成注册。")
    void regUser(User user);

    /**
     * 域账号自注册
     */
    @Logable(operateObject="用户注册", operateInfo=" 域用户（${args[1]}, ${args[0]}）完成注册。")
	void regBusiness(User user, String domain);
	
    /**
     * 开发者自注册
     */
    @Logable(operateObject="用户注册", operateInfo=" 开发者（${args[0]}）完成注册。")
	void regDeveloper(User user);
}
