/* ==================================================================   
 * Created [2017-10-22] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:boubei@163.com
 * Copyright (c) boubei.com, 2015-2018 
 * ================================================================== 
 */
package com.boubei.tss;

import java.util.HashMap;
import java.util.Map;

import com.boubei.tss.util.EasyUtils;

/**
 * 异常集、消息集
 * 
 * EX.parse(EX.U_40, x1, x2)
 * 
 */
public class EX {
	
	public static String parse(String msg, Object...values) {
		Map<String, Object> data = new HashMap<String, Object>();
		for(Object val : values) {
			data.put("x" + (data.size() + 1), val);
		}
		return EasyUtils.fmParse(msg, data);
	}
	
	public static String DEFAULT_SUCCESS_MSG = "操作成功！";
	public static String REG_SUCCESS_MSG = "用户注册成功！";
	public static String REG_TIMEOUT_MSG = "注册失败, 您在注册页停留时间过长，请刷新页面后重新注册。";
	
	public static String _ERROR_TAG = "【失败!!!】";
	public static String TIMER_REPORT = "定时报表";
	public static String CACHE_CONFIG = "缓存池配置";
	
	public static String CACHE_1 = "当前应用服务器资源紧张，请稍后再查询。";
	public static String CACHE_2 = "您当前点击的查询正在处理中，请您耐心等待，不要反复查询。";
	public static String CACHE_3 = "本次请求执行缓慢，请稍后再查询。";
	public static String CACHE_4 = "当前您查询的数据服务${x1}响应缓慢，前面还有${x2}个人在等待，请稍后再查询。";
	public static String CACHE_5 = "该缓存项已经不存在，已经被清空或是已经被刷新！";
	
	public static String MODULE_1 = "您不是域管理员，无法执行此操作。" ;
	public static String MODULE_2 = "导入成功，如模块带有自定义HTML页面等资源文件，其相应目录及数据服务需另外修改";
	
	public static String CMS_0 = "未找到文章！";
	public static String CMS_1 = "栏目IDs为空";
	public static String CMS_2 = "ID为：${x1} 的站点/栏目不存在，可能已被删除";
	public static String CMS_22= "ID为：${x1} 的站点不存在，无法进行全文检索";
	public static String CMS_3 = "指定类型的附件路径不存在";
	public static String CMS_4 = "您没有发布本栏目（站点）【id=${x1}， name=${x1}】的权限！";
	public static String CMS_5 = "您对正尝试访问的栏目【${x1}】没有浏览文章权限。";
	public static String CMS_6 = "文章不能移动到站点目录下，请重新选择一个目标栏目";
	public static String CMS_7 = "您没有删除该栏目的足够权限！";
	public static String CMS_8 = "您对当前栏目没有停用权限！";
	public static String CMS_9 = "选择节点不存在！id = ${x1}";
	public static String CMS_10 = "年度或月份不能为空!";
	public static String CMS_11 = "栏目不存在!";
	public static String CMS_12 = "站点的发布路径填写错误，不能生成相应的发布文件路径！";
	public static String CMS_13 = "站点的附件上传根路径填写错误，不能生成相应的附件上传根路径路径！";
	public static String CMS_14 = "站点的图片根路径填写错误，不能生成相应的图片根路径路径！";
	
	public static String DM_01 = "日期【${x1}】格式有误，请检查。";
	public static String DM_02 = "数据源【${x1}】不存在。";
	public static String DM_03 = "参数个数不对。";
	public static String DM_04 = "已经人为停止，最后更新人为";
	public static String DM_05 = "您对此数据记录【${x1}】没有维护权限，无法修改或删除。";
	public static String DM_06 = "该附件不存在，可能已被删除!";
	public static String DM_07 = "您对此附件没有查看权限";
	public static String DM_08 = "您对此记录没有浏览权限";
	public static String DM_09 = "对数据表【${x1}】的权限不足，【${x2}】。";
	public static String DM_10 = "该数据表已被停用，无法再录入数据！";
	public static String DM_11 = "令牌验证未获通过，调用接口失败。";
	public static String DM_12 = "您对此数据表没有维护权限";
	public static String DM_13 = "ID=${x1} 的数据表不存在。";
	public static String DM_13_2 = "ID=${x1} 的数据记录不存在。";
	public static String DM_14 = "没有找到名为【${x1}】的数据表";
	public static String DM_15 = "【${x1}】的参数配置有误，JSON格式存在错误，请检查修正后再保存。具体原因：${x2}";
	public static String DM_16 = "修改出错，该记录不存在，可能已经被删除。";
	public static String DM_17 = "修改异常，该记录在你修改期间已经被其它人修改过了，请关闭后刷新列表，再重新进行修改。";
	public static String DM_18 = "【${x1}】数据服务不存在。";
	public static String DM_19 = "此报表没有包含任何查询脚本，无法被订阅。";
	public static String DM_20 = "参数【${x1}】不能为空。";
	public static String DM_21 = "点击链接可以看到更详细的图表：";
	public static String DM_22 = "报表【${x1}】的内容详细请参见附件。";
	public static String DM_23 = "数据异常，位置第${x1}行。请用文本编辑器（记事本等）打开导入文件，检查此行字段内容里是否存在换行符。";
	public static String DM_24 = "没有查询到数据";
	
	public static String F_01 = "【${x1}】参数有误，第 ${x2} 个参数值为Null！";
	public static String F_02 = "排序节点和目标节点不属于同一层的节点（父节点不一致），不能排序。";
	public static String F_03 = "创建数据库连接出错了, ${x1}, ${x2}, ${x3}";
	public static String F_04 = "数据源【${x1}】正在维护中，已被停用，请稍后再访问";
	public static String F_05 = "系统【${x1}】中没有应用【${x2}】的相关访问配置信息";
	public static String F_06 = "参数管理模块中没有 ${x1} 的应用服务配置信息";
	public static String F_07 = "名为：${x1}的字段在XFORM模板里不存在，设置属性失败！";
	public static String F_08 = "当前您查询的服务【${x1}@${x2}响应缓慢，前面还有${x3}个人在等待，请稍后再查询。";
	public static String F_09 = "取消进度成功";
	public static String F_10 = " code = ${x1} 的参数没有被创建";
	public static String F_11 = "获取【${x1}】参数信息失败!";
	public static String F_12 = "您不能执行当前操作，请联系系统管理员！";
	public static String F_13 = "相同参数名称（指CODE）已经存在，请更改参数名称后再保存!";
	public static String F_14 = "参数已存在，不要重复创建! checked by :";
	
	public static String U_00 = "账号或密码错误";
	public static String U_01 = "验证码输入有误，请重新输入。";
	public static String U_02 = "账号或密码不能为空，请重新登录。";
	public static String U_03 = "登陆失败，可能是您操作时间过长，请重新输入账户及密码。";
	public static String U_04 = "登陆令牌已失效，请刷新页面后重新登录。";
	public static String U_05 = "导入组的对应外部应用组的ID（fromGroupId）为空";
	public static String U_06 = "请选择授权级别";
	public static String U_07 = "请选择应用系统";
	public static String U_08 = "请选择资源类型";
	public static String U_09 = "未找到 ${x1} 系统中，资源类型为 ${x2} 的资源的root对象";
	public static String U_10 = "未找到 ${x1} 系统中，资源类型为 ${x2} 的资源类型";
	public static String U_11 = "权限补齐时找不到资源对象，请检查资源视图（${x1}）中是否包含该对象？是否过滤掉了？";
	public static String U_12 = "操作失败，您对被操作节点的子节点（或父节点）至少有一个没有相应操作权限！";
	public static String U_13 = "检查资源权限失败，您对 ID=${x1} 的目标资源没有当前操作所需的权限！";
	public static String U_14 = "新增失败，您没有在节点 ID=${x1} 下新增资源的权限！";
	public static String U_15 = "移动失败，您对移动到的 ID=${x1} 的目标节点下没有新增资源的权限！";
	public static String U_16 = "移动失败，您对移动的 ID=${x1} 的节点没有删除权限！";
	public static String U_17 = "排序失败，您对进行拖动的 ID=${x1} 的节点没有排序权限！";
	public static String U_18 = "保存失败，您对保存的【${x1】节点没有修改权限！";
	public static String U_19 = "名称【${x1}】已被注册，请更换一个名称";
	public static String U_20 = "当前用户在要删除的组中，删除失败！";
	public static String U_21 = "没有删除用户组权限，不能删除此节点！";
	public static String U_22 = "您对停用节点下的某些资源（用户组）没有停用操作权限，不能停用此节点！";
	public static String U_23 = "对节点的父节点没有启用权限，不能启用此节点！";
	public static String U_24 = "不能移动到不同类型的组织下面";
	public static String U_25 = "您的账号已被锁定，因连续输错密码超过10次，请在10分钟后再尝试登陆。";
	public static String U_26 = "此帐号处于停用状态";
	public static String U_27 = "此帐号已过期";
	public static String U_28 = "不能向自己里面的枝节点移动";
	public static String U_29 = "账号已被注册,请更换账号.";
	public static String U_30 = "邮箱已被注册,请更换邮箱.";
	public static String U_31 = "手机号码已被注册,请更换手机号码.";
	public static String U_32 = "当前用户无法删除，可以尝试停用！";
	public static String U_321= "您无法自己停（启）用您自己，请联系管理员！";
	public static String U_33 = "初始化密码不能为空";
	public static String U_34 = "修改密码时找不到用户ID=${x1}用户，请联系管理员";
	public static String U_35 = "旧密码输入不正确";
	public static String U_36 = "新密码和旧密码没有区别，修改失败";
	public static String U_37 = "您的密码强度不够，请重新设置一个强度更强的密码！";
	public static String U_38 = "设置新密码成功！";
	public static String U_39 = "密码错误，您已累积输错了10次，账号已被锁定，请在10分钟后再尝试登录。";
	public static String U_40 = "密码错误，连续输错10次，账号将被锁定。您还可以尝试${x1}次。";
	public static String U_41 = "用户【${x1}】不存在";
	public static String U_42 = "【${x1}】没有设置密码保护问题，请用其它方法重置密码";
	public static String U_43 = "邮箱信息有误或者您没有设置邮箱信息，无法通过邮箱重置密码";
	public static String U_44 = "密码保护问题或答案不正确";
	public static String U_45 = "修改密码失败";
	
	public static String P_01 = "组件不存在，可能已被删除，读取失败！ID = ";
	public static String P_02 = "同名文件(夹)已经存在，重命名失败！";
	public static String P_03 = "节点不能移动到自身节点下";
	public static String P_04 = "没有设定一个默认的修饰器！";
	public static String P_05 = "没有设定一个默认的布局器！";
	public static String P_07 = "组装门户时根节点必须是portal根节点， 【${x1}】不是根节点!";
	public static String P_08 = "导入XML文件不是规范的门户组件，根节点名称不匹配！";
	public static String P_09 = "导入文件不是规范的门户组件";
	public static String P_10 = "您对当前门户【${x1}】没有浏览访问权限！";
	public static String P_11 = "删除组件为默认的修饰器或布局器，删除失败！";
	public static String P_12 = "停用组件为默认的修饰器或布局器，停用失败！";
	public static String P_13 = "访问地址有误，找不到相应的门户发布消息。";
	public static String P_14 = "相同的映射地址已经存在，请更换。";
	public static String P_15 = "该主题为门户的默认主题或者当前主题，正在使用中，删除失败！";
	public static String P_16 = "主题名称不能为空";
	public static String P_17 = "解压文件【${x1}】到【${x2}】目录失败!!!";
	public static String P_18 = "文件导入错误,可能不是规范的${x1}导入文件!!!";

}
