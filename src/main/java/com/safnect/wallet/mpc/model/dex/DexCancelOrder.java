package com.safnect.wallet.mpc.model.dex;

import java.util.Date;

import javax.persistence.Id;

public class DexCancelOrder {

	@Id
	String id;
	
	String orderId;
	
	Date createDatetime;
	
	Boolean successed;
	
	Integer feePerByte;

	public DexCancelOrder() {
		super();
	}

	public DexCancelOrder(String id, String orderId, Date createDatetime, Boolean successed, Integer feePerByte) {
		super();
		this.id = id;
		this.orderId = orderId;
		this.createDatetime = createDatetime;
		this.successed = successed;
		this.feePerByte = feePerByte;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}

	public Boolean getSuccessed() {
		return successed;
	}

	public void setSuccessed(Boolean successed) {
		this.successed = successed;
	}

	public Integer getFeePerByte() {
		return feePerByte;
	}

	public void setFeePerByte(Integer feePerByte) {
		this.feePerByte = feePerByte;
	}
}
