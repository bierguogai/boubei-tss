package com.boubei.tss.framework.sso;

import com.boubei.tss.framework.exception.BusinessException;

/**
 * for test
 */
public class DoErrorLoginCustomizer implements ILoginCustomizer {
	
	static int count = 1;
 
    public void execute() {
    	if(count++ == 1) {
    		throw new BusinessException("just throw one exception");
    	}
    	throw new NullPointerException();
    }
}

	