package com.safnect.wallet.mpc.model.activity;

import java.util.Date;

import javax.persistence.Id;

public class MarketingActivity2412 {

	public MarketingActivity2412() {
		super();
	}

	public MarketingActivity2412(String postId, String address) {
		super();
		this.postId = postId;
		this.address = address;
	}

	@Id
	String id;
	
	String address, postId, walletId, twUserId, postLink;

	Date createDatetime;
	
	Boolean verified, passed, winner;
	
	public Boolean getWinner() {
		return winner;
	}

	public void setWinner(Boolean winner) {
		this.winner = winner;
	}

	public Boolean getPassed() {
		return passed;
	}

	public void setPassed(Boolean passed) {
		this.passed = passed;
	}

	public String getPostLink() {
		return postLink;
	}

	public void setPostLink(String postLink) {
		this.postLink = postLink;
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

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getWalletId() {
		return walletId;
	}

	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}

	public String getTwUserId() {
		return twUserId;
	}

	public void setTwUserId(String twUserId) {
		this.twUserId = twUserId;
	}

	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}

	public Boolean getVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
	}
}
