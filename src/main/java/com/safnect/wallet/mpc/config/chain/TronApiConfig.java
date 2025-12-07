package com.safnect.wallet.mpc.config.chain;

import org.springframework.stereotype.Component;

@Component
public class TronApiConfig extends ChainApiConfig {
	
	static final String[] KEY_ARR = new String[] {
		""
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
		return "https://api.shasta.trongrid.io";
	}

	@Override
	public String getMainnetEndpoint() {
		return "https://api.trongrid.io";
	}
}