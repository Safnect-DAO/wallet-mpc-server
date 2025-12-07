//package com.safnect.wallet.mpc.config.ws;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.client.WebSocketConnectionManager;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import com.safnect.wallet.mpc.web.ws.BtcTestnetMempoolWebSocketHandler;
//
//@Configuration
//public class BtcTestnetWebSocketConfig {
//
//	static WebSocketConnectionManager manager;
//	
//    @Bean
//    public TextWebSocketHandler mempoolWebSocketHandler() {
//        return new BtcTestnetMempoolWebSocketHandler();
//    }
//
//    @Bean
//    public WebSocketConnectionManager webSocketConnectionManager(TextWebSocketHandler handler) {
//        StandardWebSocketClient client = new StandardWebSocketClient();
//        manager = new WebSocketConnectionManager(client, handler, "wss://mempool.space/testnet4/api/v1/ws");
//        manager.setAutoStartup(true);
//        return manager;
//    }
//    
//    public static void reconnect() {
//    	if (!manager.isConnected()) {
//    		manager.start();
//    	}
//    }
//}
