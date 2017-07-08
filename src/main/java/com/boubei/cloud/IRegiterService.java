package com.boubei.cloud;

import com.boubei.tss.um.entity.User;

public interface IRegiterService {

	boolean regBusiness(User user, String domain, String roles);
	
	boolean regDeveloper(User user);

}
