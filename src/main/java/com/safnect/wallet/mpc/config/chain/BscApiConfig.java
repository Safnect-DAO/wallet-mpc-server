package com.safnect.wallet.mpc.config.chain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.stereotype.Component;

@Component
public class BscApiConfig extends ChainApiConfig {
	
	static final String[] KEY_ARR = new String[] {
		"1ETWZS82IM9QCUIUPGUBQAIDX32NDN311G"
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
		return "https://api-testnet.bscscan.com/api";
	}

	@Override
	public String getMainnetEndpoint() {
		return "https://api.bscscan.com/api";
	}
	
	public static void main(String[] args) {
		String filePath = "D:\\query-log.log";  // 替换为你的 .log 文件路径

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
            	if (line.contains("Execute")) {
            		String str = line.split("Execute")[1].trim();
            		if (str.startsWith("select")) {
            			System.out.println(str + ";");
            		}
            	}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}