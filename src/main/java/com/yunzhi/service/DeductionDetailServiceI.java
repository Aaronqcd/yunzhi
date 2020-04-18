package com.yunzhi.service;
import com.yunzhi.entity.DeductionDetailEntity;
import org.jeecgframework.core.common.service.CommonService;

import java.io.Serializable;

public interface DeductionDetailServiceI extends CommonService{
	
 	public void delete(DeductionDetailEntity entity) throws Exception;
 	
 	public Serializable save(DeductionDetailEntity entity) throws Exception;
 	
 	public void saveOrUpdate(DeductionDetailEntity entity) throws Exception;

	public void saveDeductionDetail(String userId, DeductionDetailEntity rechargeRecord) throws Exception;
 	
}
