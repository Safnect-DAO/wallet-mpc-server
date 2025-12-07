package com.safnect.wallet.mpc.model.activity;

import java.util.Date;

import javax.persistence.Id;

public class TwitterApiKey {

	@Id
	String id;
	
	String bearerToken, remark;
	
	Boolean valid;
	
	Date lastUseDatetime;

	public TwitterApiKey() {
		super();
	}

	public TwitterApiKey(String id, Boolean valid) {
		super();
		this.id = id;
		this.valid = valid;
	}

	public TwitterApiKey(String id, Date lastUseDatetime) {
		super();
		this.id = id;
		this.lastUseDatetime = lastUseDatetime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBearerToken() {
		return bearerToken;
	}

	public void setBearerToken(String bearerToken) {
		this.bearerToken = bearerToken;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public Date getLastUseDatetime() {
		return lastUseDatetime;
	}

	public void setLastUseDatetime(Date lastUseDatetime) {
		this.lastUseDatetime = lastUseDatetime;
	}
}
