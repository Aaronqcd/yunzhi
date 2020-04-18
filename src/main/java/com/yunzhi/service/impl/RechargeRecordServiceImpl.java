package com.yunzhi.service.impl;
import com.yunzhi.entity.AccountEntity;
import com.yunzhi.service.AccountServiceI;
import com.yunzhi.service.RechargeRecordServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import com.yunzhi.entity.RechargeRecordEntity;
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

@Service("rechargeRecordService")
@Transactional
public class RechargeRecordServiceImpl extends CommonServiceImpl implements RechargeRecordServiceI {
	@Autowired
	private SystemService systemService;
	@Autowired
	private AccountServiceI accountService;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
 	public void delete(RechargeRecordEntity entity) throws Exception{
 		super.delete(entity);
 	}
 	
 	public Serializable save(RechargeRecordEntity entity) throws Exception{
 		Serializable t = super.save(entity);
 		return t;
 	}
 	
 	public void saveOrUpdate(RechargeRecordEntity entity) throws Exception{
 		super.saveOrUpdate(entity);
 	}

	@Override
	@Transactional
	public void saveRechargeRecord(String userId, RechargeRecordEntity rechargeRecord) throws Exception {
		//充值，添加充值记录并更新账户表的余额
		Map<String, Object> account = systemService.findOneForJdbc("select id from tb_account where user_id=?", userId);
		String id = (String) account.get("id");
		AccountEntity accountEntity = accountService.get(AccountEntity.class, id);
		Double balance = accountEntity.getBalance() + rechargeRecord.getMoney();
		//设置充值前金额
		rechargeRecord.setBeforeMoney(accountEntity.getBalance());
		//设置充值后金额
		rechargeRecord.setAfterMoney(balance);
		accountEntity.setBalance(balance);
		accountService.saveOrUpdate(accountEntity);
		rechargeRecord.setStatus("1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		Date now = new Date();
		String time = sdf.format(now);
		String comment = time + " 充值" + rechargeRecord.getMoney() + "人民币";
		rechargeRecord.setComment(comment);
		rechargeRecord.setAccount(accountEntity);
		save(rechargeRecord);
	}

}