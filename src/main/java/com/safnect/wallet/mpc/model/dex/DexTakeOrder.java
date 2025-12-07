package com.safnect.wallet.mpc.model.dex;

import java.util.Date;

import javax.persistence.Id;

public class DexTakeOrder {
	
	public DexTakeOrder() {
		super();
	}
	
	@Id
	String id;
	
	String address, requestId, orderId, usdFb;

	Date createDatetime;
	
	Boolean successed;
	
	Integer feePerByte;

	public String getUsdFb() {
		return usdFb;
	}

	public void setUsdFb(String usdFb) {
		this.usdFb = usdFb;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
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
