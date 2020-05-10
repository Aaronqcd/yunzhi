package com.yunzhi.service.impl;
import com.yunzhi.entity.AccountEntity;
import com.yunzhi.service.AccountServiceI;
import com.yunzhi.service.DeductionDetailServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import com.yunzhi.entity.DeductionDetailEntity;
import org.jeecgframework.web.system.service.SystemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.io.Serializable;
import org.jeecgframework.core.util.ApplicationContextUtil;
import org.jeecgframework.core.util.MyClassLoader;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.web.cgform.enhance.CgformEnhanceJavaInter;

import org.jeecgframework.minidao.util.FreemarkerParseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.jeecgframework.core.util.ResourceUtil;

@Service("deductionDetailService")
@Transactional
public class DeductionDetailServiceImpl extends CommonServiceImpl implements DeductionDetailServiceI {
	@Autowired
	private SystemService systemService;
	@Autowired
	private AccountServiceI accountService;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
 	public void delete(DeductionDetailEntity entity) throws Exception{
 		super.delete(entity);
 	}
 	
 	public Serializable save(DeductionDetailEntity entity) throws Exception{
 		Serializable t = super.save(entity);
 		return t;
 	}
 	
 	public void saveOrUpdate(DeductionDetailEntity entity) throws Exception{
 		super.saveOrUpdate(entity);
 	}

	@Override
	public int saveDeductionDetail(String userId, DeductionDetailEntity deductionDetail) throws Exception {
		//扣费，添加扣费记录并更新账户表的余额
		Map<String, Object> account = systemService.findOneForJdbc("select id from tb_account where user_id=?", userId);
		String id = (String) account.get("id");
		AccountEntity accountEntity = accountService.get(AccountEntity.class, id);
		Double balance = accountEntity.getBalance() - deductionDetail.getMoney();
		if(balance < 0) {
			return 1;//表示余额不足
		}
		//设置充值前金额
		deductionDetail.setBeforeMoney(accountEntity.getBalance());
		//设置充值后金额
		deductionDetail.setAfterMoney(balance);
		accountEntity.setBalance(balance);
		accountService.saveOrUpdate(accountEntity);
		deductionDetail.setStatus("1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		Date now = new Date();
		String time = sdf.format(now);
		String type = deductionDetail.getType();
		String text = "";
		if("1".equals(type)) {
			text = "扣除电费";
		}
		String comment = time + " " + text + deductionDetail.getMoney() + "元";
		deductionDetail.setComment(comment);
		deductionDetail.setAccount(accountEntity);
		save(deductionDetail);
		return 0;//表示扣费正常
 	}

}