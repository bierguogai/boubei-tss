package com.boubei.tss.framework.sso.context;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.boubei.tss.PX;
import com.boubei.tss.framework.Config;
import com.boubei.tss.framework.sso.IdentityCard;
import com.boubei.tss.framework.web.wrapper.AvoidRepeatHttpServletResponseWrapper;

/**
 * <p> 应用上下文环境静态对象 </p>
 * <pre>
 * 利用线程变量（ThreadLocal）保存用户每次请求的request、response、身份证、令牌等信息，
 * 以便其它地方程序需要获取这些信息的时候可以方便的通过Environment或者Context对象获取。
 * 这些信息在ContextFilter中完成设置，所以ContexFilter需在AutoLoginFilter、HttpProxyFilter等filter之前配置好。
 * </pre>
 */
public final class Context {
    
    static Logger log = Logger.getLogger(Context.class);

	/**
	 * 应用系统上下文信息，保存当前应用系统信息，基于应用
	 */
	private static ApplicationContext appContext = null;

	/**
	 * 当前请求上下文信息，基于request（每次请求），所以它的容器采用ThreadLocal
	 */
	private static ThreadLocal<RequestContext> requestLocal = new ThreadLocal<RequestContext>();

	/**
	 * 当前响应对象信息，基于request（每次请求）
	 */
	private static ThreadLocal<HttpServletResponse> responseLocal = new ThreadLocal<HttpServletResponse>();
	
	public static Map<String, HttpSession> sessionMap = new HashMap<String, HttpSession>();

	/**
	 * <pre>
	 * 用户令牌，基于request（每次请求）。
     * 令牌信息（基于session不变）本应该只存放与session中即可，
     * 为了满足非web情况（也就是无session时）线程可以方便的存放令牌，故有此tokenLocal，类似cardsMap作用
     * </pre>
	 */
	private static ThreadLocal<String> tokenLocal = new ThreadLocal<String>();
    
    /**
     * <pre>
     * 用来存放card，当类似定时器Job触发时，也需要Context.initIdentityInfo(card)来设置用户信息，而这种情况下
     * 没有servlet请求所以没法生成RequestContext，也就无法往session里存放card信息，故有此Map。当用户退出session销毁时，
     * 调用destroyIdentityCard(token)。
     * </pre>
     * 注：card是session级，而不是request级，所以在Context.destroy()时无需销毁。
     */
    private static Map<String, IdentityCard> cardsMap = new HashMap<String, IdentityCard>(); 

	/**
	 * <p>
	 * 获取当前系统上下文信息
	 * </p>
	 * @return
	 */
	public static ApplicationContext getApplicationContext() {
		if (appContext == null) {
			appContext = new ApplicationContext();
		}
		return appContext;
	}

    /**
     * <p>
     * 初始化应用系统上下文信息
     * </p>
     * @param context
     */
    public static void initApplicationContext(ApplicationContext context) {
        appContext = context;
    }

	/**
	 * <p>
	 * 初始化当前请求上下文信息
	 * </p>
	 * @param request
	 */
	public static void initRequestContext(HttpServletRequest request) {
		RequestContext rc = new RequestContext(request);
		requestLocal.set(rc);
        
        // 令牌token（sessionId + userId组成）生成后放在session当中，
        // 每次请求都需要将其设置token线程变量中，因为线程变量每次线程结束就会被垃圾回收掉
        IdentityCard card = rc.getIdentityCard();
        if(card != null){
            setToken(card.getToken());
        }
	}

    /**
     * <p>
     * 获取当前请求上下文信息，如果没有初始化则返回Null
     * </p>
     * @return
     */
    public static RequestContext getRequestContext() {
        return requestLocal.get();
    }

    /**
     * <pre>
     * 获取用户令牌。不管是web环境请求还是直接执行JAVA方法，
     * 前者会在ContexFilter调用到initRequestContext来设置tokenLocal(如果是首次登陆，则登陆成功后会调用initIdentityInfo)；
     * 后者需要手动创建一个IdentityCard对象，然后调用initIdentityInfo方法来设置tokenLocal。
     * 这些操作都需要在第一步执行（所以ContexFilter需在AutoLoginFilter、HttpProxyFilter等filter之前配置）。
     * 如此可保证每次需要调用Context.getToken时token都已经存在在tokenLocal中。
     * </pre>
     * @see Context.initRequestContext(HttpServletRequest request)
     * @see Context.initIdentityInfo(IdentityCard card) 
     * @return
     */
    public static String getToken() {
        return tokenLocal.get();
    }

    /**
     * <p>
     * 设置用户令牌
     * </p>
     * @param token
     */
    public static void setToken(String token) {
        tokenLocal.set(token);
    }
    
    /**
     * <pre>
     * 用户登陆成功后初始化用户的身份证信息。将身份证放入到session中，同时也放入到cardsMap中（非web情况即无session时该map会用到），
     * 最后还需将令牌信息放入到tokenLocal中。
     * </pre>
     * @param card
     */
    public static void initIdentityInfo(IdentityCard card) {
        RequestContext rc = getRequestContext();
        if (rc != null) {
            HttpSession session = rc.getSession();
            // 判断是否是注册用户（包括匿名用户）登录系统。是的话将令牌等信息放入session
            if (session != null && card != null) {
                session.setAttribute(RequestContext.IDENTITY_CARD, card);
                session.setAttribute(RequestContext.USER_TOKEN, card.getToken());
            }
        }
        if(card != null){
            cardsMap.put(card.getToken(), card); //在用户注销的时候将其去除，在Session监听器里去
            setToken(card.getToken());
        }
        log.debug("完成在应用【" + Config.getAttribute(PX.APPLICATION_CODE) + "】里设置用户（" + card + "）的身份证、令牌等信息。");
    }
   
    /**
     * 在用户注销的时候将其去除，在Session监听器里去
     */
    public static void destroyIdentityCard(String token) {
        cardsMap.remove(token);
        tokenLocal.set(null);
    }
    
    /**
     * <pre>
     * 获取用户的身份证对象。web环境下存放在session里和cardsMap里。
     * 非web环境（单元测试环境、定时器操作等）只存放与cardsMap中。
     * </pre>
     * @see Environment.getUserId()
     * @return
     */
    public static IdentityCard getIdentityCard(){
        IdentityCard card = (IdentityCard) cardsMap.get(getToken());
        if(card == null){
            RequestContext rc = getRequestContext();
            if(rc != null){
                card = rc.getIdentityCard();
            }
        }
        return card;
    }
    
    /**
     * 判断用户是否在线。根据用户的身份证信息是否为空
     * @return
     */
    public static boolean isOnline(){
        return getIdentityCard() != null;
    }

	/**
	 * <p>
	 * 销毁用户登录、访问相关上下文信息
	 * </p>
	 */
	public static void destroy() {
		RequestContext rc = getRequestContext();
		if (rc != null) {
			rc.destroy();
		}
		requestLocal.set(null);
		responseLocal.set(null);
		
		appContext = null;
	}

	/**
	 * <p>
	 * 获取response的值
	 * </p>
	 * @return 返回response的值
	 */
	public static HttpServletResponse getResponse() {
		return responseLocal.get();
	}

	/**
	 * <pre>
	 * 设置response的值。
	 * 将response封装成AvoidRepeatHttpServletResponseWrapper，可防止写入同名的cookie。
	 * </pre>
	 * @param 将response的值赋值给response
	 */
	public static void setResponse(HttpServletResponse response) {
		responseLocal.set(new AvoidRepeatHttpServletResponseWrapper(response));
	}
}