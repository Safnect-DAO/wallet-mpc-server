package com.safnect.wallet.mpc.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public class Constants {
	
	public static final String OKLINK_ENDPOINT = "https://www.oklink.com/api/v5/explorer/";
	
	public static final String OKLINE_API_KEY = "07015457-9aac-4f22-bf09-83c92af62a75";
	
	public static final String NETWORK_MAINNET = "mainnet";
	
	public static final String NETWORK_TESTNET = "testnet";
	
	public static final String TOKEN_ID_OPCAT = "45ee725c2c5993b3e4d308842d87e973bf1951f5f7a804b21e4dd964ecd12d6b_0";
	
	public static final String SOURCE_APP_EXTENSION = "E";
	
	public static final String SOURCE_APP_ANDROID = "A";
	
	public static final String SOURCE_APP_IOS = "I";
	
	public static final String BITCOIN_MEMPOOL_SPACE = "https://mempool.space";
	public static final String LITECOIN_MEMPOOL_SPACE = "https://litecoinspace.org";
	public static final String FRACTALBITCOIN_MEMPOOL_SPACE = "https://mempool%s.fractalbitcoin.io";
	public static final String OPCAT_LAYER_TX_STATUS_ENDPOINT = "https://testnet-openapi.opcatlabs.io/api/v1/tx/%s/status";
	
	public static final String TATUM_DOGECOIN_RPC = "https://doge-mainnet.gateway.tatum.io";
	public static final String TATUM_APIKEY = "t-67a99cfa5e8016861a9fcf3e-5790b677d5b34641a33ccec6";
	
	public static final String TATUM_API_ENDPOINT = "https://api.tatum.io";
	
	public static final String APTOS_API_ENDPOINT = "https://api.mainnet.aptoslabs.com";
	
	public static final String CHAIN_UTXO_KEY_FORMAT = "%s_%s_%s_%s";
	
	public static final String DEFAULT_WALLET_ID = "S00000000000-000000";
	
	public static final String EVM_METHOD_ID = "0xa9059cbb";
	
	public static final class ChainName {
		
		public static final String BITCOIN = "btc";
		public static final String FRACTAL_BITCOIN = "fb";
		public static final String BELLCOIN = "BELL";
		public static final String SOLANA = "SOLANA";
		public static final String SIDRA = "SIDRA";
		public static final String BitcoinSV = "BSV";
		public static final String CATCOIN = "CAT";
	}
	
	public static final class Unisat {
		
		public static final String FB_KEY_UNISAT_MAINNET = "d7a197808d86c252d944a7b3e1b84dc93c6e82a47ec37192b452a6acedfc41ee";
		public static final String FB_KEY_UNISAT_TESTNET = "c6b667f7f10282215c1eb8034261759796d47039bf5e589859c2ff4f8b36532f";
		
		public static final String BTC_KEY_UNISAT_MAINNET = "937fc3e56a7d264cfd5a32ab4a757416701737f23d20470ff84fd7f4696caa80";
		public static final String BTC_KEY_UNISAT_TESTNET = "84fea2f58c916158001db6cafc9c91124ab0c98c45fe365785685e26e45bdb7a";
		
		public static final Header[] FB_MAINNET_HEADER_ARR = new Header[] { new BasicHeader("Authorization", "Bearer " + FB_KEY_UNISAT_MAINNET) };
		public static final Header[] FB_TESTNET_HEADER_ARR = new Header[] { new BasicHeader("Authorization", "Bearer " + FB_KEY_UNISAT_TESTNET) };

		public static final Header[] BTC_MAINNET_HEADER_ARR = new Header[] { new BasicHeader("Authorization", "Bearer " + BTC_KEY_UNISAT_MAINNET) };
		public static final Header[] BTC_TESTNET_HEADER_ARR = new Header[] { new BasicHeader("Authorization", "Bearer " + BTC_KEY_UNISAT_TESTNET) };
		
		public static final String FB_MAIN_ENDPOINT_PREFIX = "https://open-api-fractal.unisat.io"; // 主网
		public static final String FB_TEST_ENDPOINT_PREFIX = "https://open-api-fractal-testnet.unisat.io";
		
		public static final String BTC_MAIN_ENDPOINT_PREFIX = "https://open-api.unisat.io"; // 主网
		public static final String BTC_TEST_ENDPOINT_PREFIX = "https://open-api-testnet.unisat.io";
	}
	
	
	
	public static final String UNCONFIRMED_TXS_LIST = "UNCONFIRMED_TXS_LIST";
	
	public static final class EVM_RPC_ENDPOINT {
		
		public static String getEndpoint(String chain, String network) {
			if (StringUtils.equals(chain, ChainName.SOLANA) && StringUtils.equals(network, NETWORK_MAINNET)) {
				return SOLANA_MAINNET;
			} else if (StringUtils.equals(chain, ChainName.SOLANA) && StringUtils.equals(network, NETWORK_TESTNET)) {
				return SOLANA_TESTNET;
			} else if (StringUtils.equals(chain, ChainName.SIDRA) && StringUtils.equals(network, NETWORK_MAINNET)) {
				return SIDRA_MAINNET;
			}
			return null;
		}
		
		public static final String SOLANA_TESTNET = "https://api.devnet.solana.com";
		public static final String SOLANA_MAINNET = "https://api.mainnet-beta.solana.com";
		
		public static final String SIDRA_MAINNET = "https://node.sidrachain.com/";
	}
	
	public static class Cardano {
		
		public static final String API_ENDPOINT = "https://cardano-mainnet.blockfrost.io/api/v0";
		
		public static final String PROJECT_ID = "mainnetDAipWjlNYeSJ2zFBrtQy9CIZw8KgZOAt";
		
		public static final String CHAIN_NAME = "CARDANO";
		
		public static final String SYMBOL = "ADA";
	}
}
