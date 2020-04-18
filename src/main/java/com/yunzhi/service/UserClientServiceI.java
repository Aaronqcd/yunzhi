package com.yunzhi.service;
import com.yunzhi.entity.UserClientEntity;
import org.jeecgframework.core.common.service.CommonService;

import java.io.Serializable;

public interface UserClientServiceI extends CommonService{
	
 	public void delete(UserClientEntity entity) throws Exception;
 	
 	public Serializable save(UserClientEntity entity) throws Exception;
 	
 	public void saveOrUpdate(UserClientEntity entity) throws Exception;
 	
}
