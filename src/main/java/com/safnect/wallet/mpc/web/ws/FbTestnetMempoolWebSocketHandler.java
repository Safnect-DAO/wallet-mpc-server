package com.safnect.wallet.mpc.web.ws;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.safnect.wallet.mpc.util.JsonUtil;

public class FbTestnetMempoolWebSocketHandler extends AbstractMempoolWebSocketHandler {

	@Override
	public void sendSubscribeMessage(WebSocketSession session) throws IOException {
		Map<String, Object> map = new HashMap<>();
        map.put("track-address", "bc1pzd3qdryjwcpx5sd5a8msf6xaskq0sedc6ud8tl0ruqdmwd7kqmwsadwdh4");
        session.sendMessage(new TextMessage(JsonUtil.toJson(map)));
	}

	@Override
	public void handleMessage(String message) throws Exception {
		if (StringUtils.startsWith(message, "{\"block-transactions") || StringUtils.startsWith(message, "{\"address-transactions")) {
//			Map<String, Object> map = JsonUtil.fromJson2Map(message);
			
		}
	}

	@Override
	public void reconnect() {
		// TODO Auto-generated method stub
		
	}

}
