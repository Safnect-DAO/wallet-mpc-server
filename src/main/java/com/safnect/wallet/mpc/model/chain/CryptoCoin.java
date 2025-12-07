package com.safnect.wallet.mpc.model.chain;

import javax.persistence.Id;
import javax.persistence.Transient;

public class CryptoCoin {

	public CryptoCoin() {
		super();
	}

	public CryptoCoin(Long coinId, String chainId) {
		super();
		this.coinId = coinId;
		this.chainId = chainId;
	}

	@Id
	Long coinId;
	
	@Id
	String chainId;
	
	String name, symbol, address, icon, tokenType;
	
	Long baseCoinId;
	
	Boolean defaultDisplay, enabled;
	
	Integer sno;
	
	@Transient
	String chainName;

	public Long getCoinId() {
		return coinId;
	}

	public void setCoinId(Long coinId) {
		this.coinId = coinId;
	}

	public String getChainId() {
		return chainId;
	}

	public void setChainId(String chainId) {
		this.chainId = chainId;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public Long getBaseCoinId() {
		return baseCoinId;
	}

	public void setBaseCoinId(Long baseCoinId) {
		this.baseCoinId = baseCoinId;
	}

	public Boolean getDefaultDisplay() {
		return defaultDisplay;
	}

	public void setDefaultDisplay(Boolean defaultDisplay) {
		this.defaultDisplay = defaultDisplay;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Integer getSno() {
		return sno;
	}

	public void setSno(Integer sno) {
		this.sno = sno;
	}

	public String getChainName() {
		return chainName;
	}

	public void setChainName(String chainName) {
		this.chainName = chainName;
	}
}
