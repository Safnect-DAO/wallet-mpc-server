package com.safnect.wallet.mpc.config.chain;

import org.springframework.stereotype.Component;

@Component
public class EthApiConfig extends ChainApiConfig {

	static final String[] KEY_ARR = new String[] {
		"HWQTV4Y2GQA8F27ATMTYAR7PN6YXP57I97"
	};
	
	static int index = 0;
	
	@Override
	public String getKey() {
		if (index >= KEY_ARR.length) {
			index = 0;
		}
		return KEY_ARR[index ++];
	}

	@Override
	public String getTestnetEndpoint() {
		return "https://api-sepolia.etherscan.io/api";
	}

	@Override
	public String getMainnetEndpoint() {
		return "https://api.etherscan.io/api";
	}
}
