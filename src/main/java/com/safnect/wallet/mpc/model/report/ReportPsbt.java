package com.safnect.wallet.mpc.model.report;

import java.util.Date;

import javax.persistence.Id;

public class ReportPsbt {
	
	@Id
	String id;
	
	String chain, address, psbtHex, sourceDomain;

	Date createDatetime;

	public String getSourceDomain() {
		return sourceDomain;
	}

	public void setSourceDomain(String sourceDomain) {
		this.sourceDomain = sourceDomain;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChain() {
		return chain;
	}

	public void setChain(String chain) {
		this.chain = chain;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPsbtHex() {
		return psbtHex;
	}

	public void setPsbtHex(String psbtHex) {
		this.psbtHex = psbtHex;
	}

	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}

}
