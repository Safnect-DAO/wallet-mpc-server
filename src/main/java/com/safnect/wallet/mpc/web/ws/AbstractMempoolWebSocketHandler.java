package com.safnect.wallet.mpc.web.ws;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.safnect.wallet.mpc.util.JsonUtil;

public abstract class AbstractMempoolWebSocketHandler extends TextWebSocketHandler {

	static final int MAX_SIZE = 3 * 1024 * 1024;
	
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    
    public abstract void sendSubscribeMessage(WebSocketSession session) throws Exception;
    
    public abstract void handleMessage(String message) throws Exception;
    
    public abstract void reconnect();
	
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    	session.setTextMessageSizeLimit(MAX_SIZE);
    	session.setBinaryMessageSizeLimit(MAX_SIZE);
        
    	// 发送订阅命令
    	this.sendSubscribeMessage(session);
        
        // 启动心跳机制
        startHeartbeat(session);
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    	String textMessage = message.getPayload();
    	this.handleMessage(textMessage);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    	while (true) {
    		if (session.isOpen()) {
    			System.out.println("ws is reconnected");
    			break;
    		}
    		this.reconnect();
    		Thread.sleep(10000);
    	}
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("Web socket error: ");
        exception.printStackTrace();
    }
    
    private void startHeartbeat(WebSocketSession session) {
        scheduler.scheduleAtFixedRate(() -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(JsonUtil.toJson("0"))); // 发送心跳包（根据服务器要求调整）
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 25, TimeUnit.SECONDS); // 每25秒发送一次心跳
    }
}