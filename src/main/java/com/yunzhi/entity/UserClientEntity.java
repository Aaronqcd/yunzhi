package com.yunzhi.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.lang.String;
import java.lang.Double;
import java.lang.Integer;
import java.math.BigDecimal;
import javax.persistence.*;
import javax.xml.soap.Text;
import java.sql.Blob;

import org.hibernate.annotations.GenericGenerator;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.web.system.pojo.base.TSUser;

/**   
 * @Title: Entity
 * @Description: 员工客户关联表
 * @author onlineGenerator
 * @date 2020-04-09 20:59:43
 * @version V1.0   
 *
 */
@Entity
@Table(name = "tb_user_client", schema = "")
@SuppressWarnings("serial")
public class UserClientEntity implements java.io.Serializable {
	/**主键*/
	private String id;
	/**员工id*/
	@Excel(name="员工id",width=15)
	private TSUser user;
	/**客户id*/
	@Excel(name="客户id",width=15)
	private ClientEntity client;
	/**类型*/
	@Excel(name="类型",width=15)
	private String type;
	
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

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "USER_ID")
	public TSUser getUser() {
		return user;
	}

	public void setUser(TSUser user) {
		this.user = user;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CLIENT_ID")
	public ClientEntity getClient() {
		return client;
	}

	public void setClient(ClientEntity client) {
		this.client = client;
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
}