package com.safnect.wallet.mpc.config.unisat;

public class UnisatBitcoinMainnetConfig {
	
	public static final String ENDPOINT = "https://open-api.unisat.io";

	static final String[] KEY_ARR = new String[] {
		"937fc3e56a7d264cfd5a32ab4a757416701737f23d20470ff84fd7f4696caa80",
		"be92d7a6655cea4eee4e402a76b4453c205fd084d67756471fb14e2d779831be",
		"792de9b9371d18885c23b719975ebe33a0f96074d0a8730e5755cb4fda3c5b67",
		"9e27a7468e5e0265ede41963be1077bd8fc44ea88ee6934e38b8533602355dc1",
		"71736ce9a64748928b063739e02dad783a7428dbd1f0f56b34d22595cbe3ac11",
		"5b511842859ded5949776d684e8b67cbd1ea373dc216e434bbf841562d8c75bf",
		"08887ea7a66d881e542b1d501fa3c44405481d664705f56a3f460ef34b6a59a7",
		"2cdf75260f58b952888b2d7effb219df5a3ef7edbf815748ef3ead64689e8790",
		"d25b6dae96e3c2f5d6deb60f6423e11d31220f1b0321af8f4be3d609e1293b32",
		"61117f3e15f9593914f0dc36af4ba5072e2e367570b5401c9fbc8770dfd6a4da"
	};
	
	static int index = 0;
	
	public static String getKey() {
		if (index >= KEY_ARR.length) {
			index = 0;
		}
		return KEY_ARR[index ++];
	}
}
