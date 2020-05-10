package com.yunzhi.service;
import com.yunzhi.entity.InviteCodeEntity;
import org.jeecgframework.core.common.service.CommonService;

import java.io.Serializable;

public interface InviteCodeServiceI extends CommonService{
	
 	public void delete(InviteCodeEntity entity) throws Exception;
 	
 	public Serializable save(InviteCodeEntity entity) throws Exception;
 	
 	public void saveOrUpdate(InviteCodeEntity entity) throws Exception;
 	
}
