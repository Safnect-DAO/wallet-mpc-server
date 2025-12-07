package com.safnect.wallet.mpc.model;

import javax.persistence.Id;

public class WalletCard {
	
	public WalletCard() {
		super();
	}

	public WalletCard(String id, String walletId, String cardSn) {
		super();
		this.id = id;
		this.walletId = walletId;
		this.cardSn = cardSn;
	}

	@Id
	String id;
	
	String walletId, cardSn;

	public String getWalletId() {
		return walletId;
	}

	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}

	public String getCardSn() {
		return cardSn;
	}

	public void setCardSn(String cardSn) {
		this.cardSn = cardSn;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
