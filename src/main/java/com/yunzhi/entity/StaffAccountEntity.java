package com.yunzhi.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.lang.String;
import java.lang.Double;
import java.lang.Integer;
import java.math.BigDecimal;
import javax.xml.soap.Text;
import java.sql.Blob;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.SequenceGenerator;
import org.jeecgframework.poi.excel.annotation.Excel;

/**   
 * @Title: Entity
 * @Description: 员工账户表
 * @author onlineGenerator
 * @date 2020-04-18 17:06:00
 * @version V1.0   
 *
 */
@Entity
@Table(name = "tb_staff_account", schema = "")
@SuppressWarnings("serial")
public class StaffAccountEntity implements java.io.Serializable {
	/**主键*/
	private String id;
	/**创建人名称*/
	private String createName;
	/**创建人登录名称*/
	private String createBy;
	/**创建日期*/
	private Date createDate;
	/**更新人名称*/
	private String updateName;
	/**更新人登录名称*/
	private String updateBy;
	/**更新日期*/
	private Date updateDate;
	/**用户id*/
	@Excel(name="用户id",width=15)
	private String userId;
	/**银行卡号*/
	@Excel(name="银行卡号",width=15)
	private String cardNo;
	/**收款账户*/
	@Excel(name="收款账户",width=15)
	private String accountName;
	/**开户行*/
	@Excel(name="开户行",width=15)
	private String bankName;
	/**余额*/
	@Excel(name="余额",width=15)
	private Double balance;
	/**类型*/
	@Excel(name="类型",width=15)
	private String type;
	/**状态*/
	@Excel(name="状态",width=15)
	private String status;
	
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  主键
	 */
	@Id
	@GeneratedValue(generator = "paymentableGenerator")
	@GenericGenerator(name = "paymentableGenerator", strategy = "uuid")

	@Column(name ="ID",nullable=false,length=36)
	public String getId(){
		return this.id;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  主键
	 */
	public void setId(String id){
		this.id = id;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  创建人名称
	 */

	@Column(name ="CREATE_NAME",nullable=true,length=50)
	public String getCreateName(){
		return this.createName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  创建人名称
	 */
	public void setCreateName(String createName){
		this.createName = createName;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  创建人登录名称
	 */

	@Column(name ="CREATE_BY",nullable=true,length=50)
	public String getCreateBy(){
		return this.createBy;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  创建人登录名称
	 */
	public void setCreateBy(String createBy){
		this.createBy = createBy;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  创建日期
	 */

	@Column(name ="CREATE_DATE",nullable=true,length=20)
	public Date getCreateDate(){
		return this.createDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  创建日期
	 */
	public void setCreateDate(Date createDate){
		this.createDate = createDate;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  更新人名称
	 */

	@Column(name ="UPDATE_NAME",nullable=true,length=50)
	public String getUpdateName(){
		return this.updateName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  更新人名称
	 */
	public void setUpdateName(String updateName){
		this.updateName = updateName;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  更新人登录名称
	 */

	@Column(name ="UPDATE_BY",nullable=true,length=50)
	public String getUpdateBy(){
		return this.updateBy;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  更新人登录名称
	 */
	public void setUpdateBy(String updateBy){
		this.updateBy = updateBy;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  更新日期
	 */

	@Column(name ="UPDATE_DATE",nullable=true,length=20)
	public Date getUpdateDate(){
		return this.updateDate;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  更新日期
	 */
	public void setUpdateDate(Date updateDate){
		this.updateDate = updateDate;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  用户id
	 */

	@Column(name ="USER_ID",nullable=true,length=36)
	public String getUserId(){
		return this.userId;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  用户id
	 */
	public void setUserId(String userId){
		this.userId = userId;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  银行卡号
	 */

	@Column(name ="CARD_NO",nullable=true,length=36)
	public String getCardNo(){
		return this.cardNo;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  银行卡号
	 */
	public void setCardNo(String cardNo){
		this.cardNo = cardNo;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  收款账户
	 */

	@Column(name ="ACCOUNT_NAME",nullable=true,length=50)
	public String getAccountName(){
		return this.accountName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  收款账户
	 */
	public void setAccountName(String accountName){
		this.accountName = accountName;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  开户行
	 */

	@Column(name ="BANK_NAME",nullable=true,length=100)
	public String getBankName(){
		return this.bankName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  开户行
	 */
	public void setBankName(String bankName){
		this.bankName = bankName;
	}
	/**
	 *方法: 取得java.lang.Double
	 *@return: java.lang.Double  余额
	 */

	@Column(name ="BALANCE",nullable=true,scale=2,length=10)
	public Double getBalance(){
		return this.balance;
	}

	/**
	 *方法: 设置java.lang.Double
	 *@param: java.lang.Double  余额
	 */
	public void setBalance(Double balance){
		this.balance = balance;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  类型
	 */

	@Column(name ="TYPE",nullable=true,length=1)
	public String getType(){
		return this.type;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  类型
	 */
	public void setType(String type){
		this.type = type;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  状态
	 */

	@Column(name ="STATUS",nullable=true,length=1)
	public String getStatus(){
		return this.status;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  状态
	 */
	public void setStatus(String status){
		this.status = status;
	}
}