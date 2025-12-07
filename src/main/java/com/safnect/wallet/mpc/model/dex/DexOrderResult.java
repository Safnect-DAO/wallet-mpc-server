package com.safnect.wallet.mpc.model.dex;

import javax.persistence.Id;

public class DexOrderResult {
	
	public DexOrderResult() {
		super();
	}

	public DexOrderResult(String id, String resultInfo) {
		super();
		this.id = id;
		this.resultInfo = resultInfo;
	}

	@Id
	String id;
	
	String resultInfo;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getResultInfo() {
		return resultInfo;
	}

	public void setResultInfo(String resultInfo) {
		this.resultInfo = resultInfo;
	}
}
