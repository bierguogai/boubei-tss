package com.boubei.tss.framework.persistence.entityaop;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

import com.boubei.tss.framework.persistence.IDao;
import com.boubei.tss.framework.persistence.IEntity;
import com.boubei.tss.framework.sso.Environment;

/**
 * <p>
 *  对象操作者信息记录拦截器
 * </p>
 */
@Component("operateInfoInterceptor")
public class OperateInfoInterceptor extends MatchByDaoMethodNameInterceptor {
	
    protected int judgeManipulateKind(String methodName){
        if(match(methodName, Arrays.asList( (updateKind + ",refresh").split(","))))
            return UPDATE;
        
        return super.judgeManipulateKind(methodName);
    }

	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object target = invocation.getThis();
		Object[] args = invocation.getArguments();
		if(args != null) {
            for (int i = 0; i < args.length; i++) {
                int manipulateKind = judgeManipulateKind(invocation.getMethod().getName());
                if (args[i] instanceof IOperatable 
                		&& (manipulateKind == SAVE || manipulateKind == UPDATE)) {
                   
                    IOperatable operateInfo = (IOperatable) args[i];
                    Serializable pk = ((IEntity)operateInfo).getPK();
                    
					if( pk == null ) { // ID为null，说明是新建
                        operateInfo.setCreateTime(new Date());
                        operateInfo.setCreatorId(Environment.getUserId());
                        operateInfo.setCreatorName(Environment.getUserName());           
                    } 
                    else {
                        operateInfo.setUpdateTime(new Date());
                        operateInfo.setUpdatorId(Environment.getUserId());
                        operateInfo.setUpdatorName(Environment.getUserName());  
                        
                        /* 修改后，createTime的时分秒没了（日期传递到前台时截去了时分秒，保存后就没有了），
                         * update时不要前台传入的createTime，而是从DB查出来复制回去
                         */
                        @SuppressWarnings("unchecked")
                        IDao<IEntity> dao = (IDao<IEntity>) target;
                        IOperatable old = (IOperatable) dao.getEntity( operateInfo.getClass(), pk);
                        if(old != null) { // 可能修改时记录已被其它人删除
                        	operateInfo.setCreateTime(old.getCreateTime());
                        }
                    }
                }
            }
		}		
        return invocation.proceed();
	}
}

	