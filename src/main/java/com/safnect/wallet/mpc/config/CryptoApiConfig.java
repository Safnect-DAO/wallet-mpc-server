package com.safnect.wallet.mpc.config;

public class CryptoApiConfig {
	
	public static final String ENDPOINT = "https://rest.cryptoapis.io";

	static final String[] KEY_ARR = new String[] {
		"0cabb380f2f6f2ac96870f4f4a4f5dee1c84a369",
		"485060592cb97c325ceb14c1df25ff9038299fb4",
		"d64d3df01c1e2bd6e7c420c721b253681fa26703",
	};
	
	static int index = 0;
	
	public static String getKey() {
		if (index >= KEY_ARR.length) {
			index = 0;
		}
		return KEY_ARR[index ++];
	}
}