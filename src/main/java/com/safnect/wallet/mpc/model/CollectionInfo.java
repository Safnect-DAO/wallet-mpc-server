package com.safnect.wallet.mpc.model;

import java.util.Date;

import javax.persistence.Id;

public class CollectionInfo {

	@Id
	String id;
	
	String chain, network, name, symbol, description, walletId, bcCollId, address;

	Date createDatetime;
	
	Boolean ismint;
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBcCollId() {
		return bcCollId;
	}

	public void setBcCollId(String bcCollId) {
		this.bcCollId = bcCollId;
	}
	
	public Boolean getIsmint() {
		return ismint;
	}

	public void setIsmint(Boolean ismint) {
		this.ismint = ismint;
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

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWalletId() {
		return walletId;
	}

	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}

	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}
}
