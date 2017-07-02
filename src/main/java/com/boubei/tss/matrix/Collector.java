package com.boubei.tss.matrix;

/**
 * 定时收集使用情况：创建多少报表、录入表资源、登陆次数、登录用户数、分时段访问统计、异常信息等，
 * 
 * 集中发往boubei.com，方式：
 * 1、通过前端 JS动态挂载 发送
 * 2、通过httpproxy代理转发，内置一个 BBI 的Appserver，指向www.boubei.com/tss
 * 3、后台JOB定时转发、通过Recorder的API、不要用远程接口
 */
public class Collector {

}
