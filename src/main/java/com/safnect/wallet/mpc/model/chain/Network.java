package com.safnect.wallet.mpc.model.chain;

import javax.persistence.Id;
import javax.persistence.Transient;

public class Network {
	
	public Network() {
		super();
	}

	public Network(String code, String icon, String name, String coinSymbol, Boolean isEvm, Boolean hasToken,
			Boolean testnetAddrSame, Boolean supportTestnet, Boolean visible, Boolean isUtxo, Double minAmount,
			Double newAccountMinAmount, Integer sno, Integer coinDecimal, String rpcUrl, String chainId, String restApi, 
			Boolean deployAccount, Double maxReserveAmount, String mainnetTxUri, String testnetTxUri) {
		super();
		this.code = code;
		this.icon = icon;
		this.name = name;
		this.coinSymbol = coinSymbol;
		this.isEvm = isEvm;
		this.hasToken = hasToken;
		this.testnetAddrSame = testnetAddrSame;
		this.supportTestnet = supportTestnet;
		this.visible = visible;
		this.isUtxo = isUtxo;
		this.minAmount = minAmount;
		this.newAccountMinAmount = newAccountMinAmount;
		this.sno = sno;
		this.coinDecimal = coinDecimal;
		this.rpcUrl = rpcUrl;
		this.chainId = chainId;
		this.restApi = restApi;
		this.deployAccount = deployAccount;
		this.maxReserveAmount = maxReserveAmount;
		this.mainnetTxUri = mainnetTxUri;
		this.testnetTxUri = testnetTxUri;
	}

	@Id
	String code;
	
	String icon, name, coinSymbol, mainnetTxUri, testnetTxUri;
	
	Boolean isEvm, hasToken, testnetAddrSame, supportTestnet, visible, isUtxo, deployAccount;
	
	Double minAmount, newAccountMinAmount, maxReserveAmount;
	
	Integer sno, coinDecimal;
	
	@Transient
	String rpcUrl, chainId, restApi;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCoinSymbol() {
		return coinSymbol;
	}

	public void setCoinSymbol(String coinSymbol) {
		this.coinSymbol = coinSymbol;
	}

	public Boolean getIsEvm() {
		return isEvm;
	}

	public void setIsEvm(Boolean isEvm) {
		this.isEvm = isEvm;
	}

	public Boolean getHasToken() {
		return hasToken;
	}

	public void setHasToken(Boolean hasToken) {
		this.hasToken = hasToken;
	}

	public Boolean getTestnetAddrSame() {
		return testnetAddrSame;
	}

	public void setTestnetAddrSame(Boolean testnetAddrSame) {
		this.testnetAddrSame = testnetAddrSame;
	}

	public Boolean getSupportTestnet() {
		return supportTestnet;
	}

	public void setSupportTestnet(Boolean supportTestnet) {
		this.supportTestnet = supportTestnet;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public Boolean getIsUtxo() {
		return isUtxo;
	}

	public void setIsUtxo(Boolean isUtxo) {
		this.isUtxo = isUtxo;
	}

	public Double getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(Double minAmount) {
		this.minAmount = minAmount;
	}

	public Double getNewAccountMinAmount() {
		return newAccountMinAmount;
	}

	public void setNewAccountMinAmount(Double newAccountMinAmount) {
		this.newAccountMinAmount = newAccountMinAmount;
	}

	public Integer getSno() {
		return sno;
	}

	public void setSno(Integer sno) {
		this.sno = sno;
	}

	public Integer getCoinDecimal() {
		return coinDecimal;
	}

	public void setCoinDecimal(Integer coinDecimal) {
		this.coinDecimal = coinDecimal;
	}

	public String getRpcUrl() {
		return rpcUrl;
	}

	public void setRpcUrl(String rpcUrl) {
		this.rpcUrl = rpcUrl;
	}

	public String getChainId() {
		return chainId;
	}

	public void setChainId(String chainId) {
		this.chainId = chainId;
	}

	public String getRestApi() {
		return restApi;
	}

	public void setRestApi(String restApi) {
		this.restApi = restApi;
	}

	public Boolean getDeployAccount() {
		return deployAccount;
	}

	public void setDeployAccount(Boolean deployAccount) {
		this.deployAccount = deployAccount;
	}

	public String getMainnetTxUri() {
		return mainnetTxUri;
	}

	public void setMainnetTxUri(String mainnetTxUri) {
		this.mainnetTxUri = mainnetTxUri;
	}

	public String getTestnetTxUri() {
		return testnetTxUri;
	}

	public void setTestnetTxUri(String testnetTxUri) {
		this.testnetTxUri = testnetTxUri;
	}

	public Double getMaxReserveAmount() {
		return maxReserveAmount;
	}

	public void setMaxReserveAmount(Double maxReserveAmount) {
		this.maxReserveAmount = maxReserveAmount;
	}
}
