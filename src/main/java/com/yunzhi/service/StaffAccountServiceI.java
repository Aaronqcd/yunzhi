package com.yunzhi.service;
import com.yunzhi.entity.StaffAccountEntity;
import org.jeecgframework.core.common.service.CommonService;

import java.io.Serializable;

public interface StaffAccountServiceI extends CommonService{
	
 	public void delete(StaffAccountEntity entity) throws Exception;
 	
 	public Serializable save(StaffAccountEntity entity) throws Exception;
 	
 	public void saveOrUpdate(StaffAccountEntity entity) throws Exception;
 	
}
