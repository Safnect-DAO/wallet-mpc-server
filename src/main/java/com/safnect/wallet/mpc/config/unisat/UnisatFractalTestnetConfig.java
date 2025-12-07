package com.safnect.wallet.mpc.config.unisat;

public class UnisatFractalTestnetConfig {
	
	public static final String ENDPOINT = "https://open-api-fractal-testnet.unisat.io";

	static final String[] KEY_ARR = new String[] {
		"c6b667f7f10282215c1eb8034261759796d47039bf5e589859c2ff4f8b36532f",
		"78a013186d1abecac41da582018f30c19fd0a8e6aafac8e7a03aa2f6bebe88d4",
		"e151fe72c38f49247a934e022d272ced0c8c80aba7a46e8424957bab5433b783",
		"17d877d25b611039568b2cc043f5f81fdfc1ecd7e0b8cffcc45e4cd75a21db43",
		"19df2144822bd6adfb2dceb5725ac4afa0308d5546f40bdcdac878b0f7749e8b",
		"3a3e8b4ceda57e2f1a9af8704f5b9589bfe09f3f58c6f7d0865d4ec45e92a72e",
		"e21ab653ba05233656c6ff69d06d1572211214cda8c1f5ee4d6cec491f004e5c",
		"1e76c4c58bb38a12bc88d461520ad821e8329d5a2dde64e36cee76a14860b695",
		"bd38be6e54725ae217e3c3fc52d18df3c53d409b3158db4f239420718a40d45d",
		"5723b2e5e25f80f27277904e82d7f1d339753a8023ed82fc15cfca8ac5e7e052",
	};
	
	static int index = 0;
	
	public static String getKey() {
		if (index >= KEY_ARR.length) {
			index = 0;
		}
		return KEY_ARR[index ++];
	}
}
