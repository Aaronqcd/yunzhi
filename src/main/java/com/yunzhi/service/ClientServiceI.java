package com.yunzhi.service;
import com.yunzhi.entity.ClientEntity;
import org.jeecgframework.core.common.service.CommonService;

import java.io.Serializable;

public interface ClientServiceI extends CommonService{
	
 	public void delete(ClientEntity entity) throws Exception;
 	
 	public Serializable save(ClientEntity entity) throws Exception;
 	
 	public void saveOrUpdate(ClientEntity entity) throws Exception;
 	
}
