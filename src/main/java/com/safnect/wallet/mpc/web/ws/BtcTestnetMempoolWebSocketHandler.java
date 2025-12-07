package com.safnect.wallet.mpc.web.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.safnect.wallet.mpc.mapper.WalletAddressMapper;
import com.safnect.wallet.mpc.model.WalletAddress;
import com.safnect.wallet.mpc.util.JsonUtil;

public class BtcTestnetMempoolWebSocketHandler extends AbstractMempoolWebSocketHandler {
	
	@Autowired
	WalletAddressMapper walletAddressMapper;

	@Override
	public void sendSubscribeMessage(WebSocketSession session) throws IOException {
		WalletAddress wa = new WalletAddress();
		wa.setChain("bitcoinTestnet");
		List<WalletAddress> waList = this.walletAddressMapper.select(wa);
		List<String> addressList = new ArrayList<>();
		waList.forEach(item -> {
			addressList.add(item.getAddress());
		});
		Map<String, Object> map = new HashMap<>();
        map.put("track-addresses", addressList);
        session.sendMessage(new TextMessage(JsonUtil.toJson(map)));
	}

	@Override
	public void handleMessage(String message) throws Exception {
		System.out.println(message);
//		if (StringUtils.startsWith(message, "{\"multi-address-transactions")) {
//			Map<String, Object> map = JsonUtil.fromJson2Map(message);
//			Map<String, Object> multiMap = (Map<String, Object>) map.get("multi-address-transactions");
//			Set<String> addressSet = multiMap.keySet();
//			for (String address : addressSet) {
//				
//			}
//    	}
	}

	@Override
	public void reconnect() {
//		BtcTestnetWebSocketConfig.reconnect();
	}

}
