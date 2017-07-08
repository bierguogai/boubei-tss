package com.boubei.cloud;

import com.boubei.tss.um.entity.User;

public interface IRegiterService {

	boolean register(User user, String domain, String roles);

}
