package com.safnect.wallet.mpc.model;

import javax.persistence.Id;
import javax.persistence.Transient;

public class TokenInfo {
	
	public TokenInfo(String contractAddress, String network, String chainName) {
		super();
		this.contractAddress = contractAddress;
		this.network = network;
		this.chainName = chainName;
	}

	public TokenInfo() {
		super();
	}

	public TokenInfo(String network, String chainName) {
		super();
		this.network = network;
		this.chainName = chainName;
	}

	@Id
	String contractAddress;
	
	String fullName, symbol, img, network, totalSupply, chainName;
	
	Integer decimals, sno, chain;
	
	Boolean invisable;
	
	@Transient
	String amount, priceUsd, valueUsd;
	
	public String getChainName() {
		return chainName;
	}

	public void setChainName(String chainName) {
		this.chainName = chainName;
	}

	public String getPriceUsd() {
		return priceUsd;
	}

	public void setPriceUsd(String priceUsd) {
		this.priceUsd = priceUsd;
	}

	public String getValueUsd() {
		return valueUsd;
	}

	public void setValueUsd(String valueUsd) {
		this.valueUsd = valueUsd;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public Boolean getInvisable() {
		return invisable;
	}

	public void setInvisable(Boolean invisable) {
		this.invisable = invisable;
	}

	public String getContractAddress() {
		return contractAddress;
	}

	public void setContractAddress(String contractAddress) {
		this.contractAddress = contractAddress;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public Integer getDecimals() {
		return decimals;
	}

	public void setDecimals(Integer decimals) {
		this.decimals = decimals;
	}

	public Integer getSno() {
		return sno;
	}

	public void setSno(Integer sno) {
		this.sno = sno;
	}

	public Integer getChain() {
		return chain;
	}

	public void setChain(Integer chain) {
		this.chain = chain;
	}
	
	public String getTotalSupply() {
		return totalSupply;
	}

	public void setTotalSupply(String totalSupply) {
		this.totalSupply = totalSupply;
	}
}
