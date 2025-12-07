package com.safnect.wallet.mpc.config.chain;

import org.apache.commons.lang3.StringUtils;

import com.safnect.wallet.mpc.util.Constants;
import com.safnect.wallet.mpc.util.SpringBeanUtil;

public abstract class ChainApiConfig {

	public abstract String getKey();
	
	public abstract String getTestnetEndpoint();
	
	public abstract String getMainnetEndpoint();
	
	public String getEndpoint(String network) {
		if (StringUtils.equals(network, Constants.NETWORK_MAINNET)) {
			return this.getMainnetEndpoint();
		}
		return this.getTestnetEndpoint();
	}
	
	public static ChainApiConfig getApiConfig(Class<? extends ChainApiConfig> clazz) {
		return SpringBeanUtil.getBean(clazz);
	}
}