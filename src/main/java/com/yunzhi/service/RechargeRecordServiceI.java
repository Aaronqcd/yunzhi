package com.yunzhi.service;
import com.yunzhi.entity.RechargeRecordEntity;
import org.jeecgframework.core.common.service.CommonService;

import java.io.Serializable;

public interface RechargeRecordServiceI extends CommonService{
	
 	public void delete(RechargeRecordEntity entity) throws Exception;
 	
 	public Serializable save(RechargeRecordEntity entity) throws Exception;
 	
 	public void saveOrUpdate(RechargeRecordEntity entity) throws Exception;

 	public void saveRechargeRecord(String userId, RechargeRecordEntity rechargeRecord) throws Exception;
 	
}
