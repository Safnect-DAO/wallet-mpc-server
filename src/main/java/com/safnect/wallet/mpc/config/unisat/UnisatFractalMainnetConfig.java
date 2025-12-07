package com.safnect.wallet.mpc.config.unisat;

public class UnisatFractalMainnetConfig {
	
	public static final String ENDPOINT = "https://open-api-fractal.unisat.io";

	static final String[] KEY_ARR = new String[] {
		"d7a197808d86c252d944a7b3e1b84dc93c6e82a47ec37192b452a6acedfc41ee",
		"9261dc0a8481b09514b4cdbe693a95f2e8d8a23845fa74aa52f933f3e81c2ce8",
		"23e83553194bc4a60991ea93a656cb5165111de001bdd32ecd28b9ccca6e9087",
		"5defee50f95b3d782aca5a36a9852ed2547a65f7263c5a1d14a3320c43c845c2",
		"37f8c2506c38b6b1dc7dbe37fc5596265ae156152c6b69a45f173a5dc34020c9",
		"5d554e50edf36ab42f3dbfcab2cf38bf9594a801126e0cd7d20594a8f906f2d5",
		"580efd56c728c2b7c87747668d82b25b4e7e6548013085bec9d3bd4b482dc7f7",
		"9be93b576da71298e61c88c7e95fe943daa33af85001bac5b39e90be9426861a",
		"478f5f5975dfcba0f6d3bd4dda76157ee565151ea391cb17da9df90ce301d66d",
		"a082b7123e8b98e839f98f748456a1b77e8a7238ddbcbc2c66742bc12c9b1039"
	};
	
	static int index = 0;
	
	public static String getKey() {
		if (index >= KEY_ARR.length) {
			index = 0;
		}
		return KEY_ARR[index ++];
	}
}
