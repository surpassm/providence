package com.ysy.oath.config;

import lombok.extern.slf4j.Slf4j;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author mc
 * Create date 2019/7/16 14:51
 * Version 1.0
 * Description
 */
@Slf4j
//@Component
//@ServerEndpoint("/websocket/{platformType}/{phone}")
public class WebSocketService {


	/**
	 * 记录当前websocket的连接数（保证线程安全）
	 */
	private static LongAdder connectAccount = new LongAdder();

	/**
	 * 存放每个客户端对应的websocketServer对象(需保证线程安全)
	 */
	private static CopyOnWriteArraySet<WebSocketService> webSocketSet = new CopyOnWriteArraySet<>();


	/**
	 * 与客户端的连接对象
	 */
	private Session session;
	/**
	 * 接收参数中的用户ID
	 */
	public String phone;
	/**
	 * 接收用户中的平台类型
	 */
	public Integer platformType;

	/**
	 * 连接成功调用的方法
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("platformType") Integer platformType, @PathParam("phone") String phone) {
		this.session = session;
		this.phone = phone;
		this.platformType = platformType;
		//加入set中
		webSocketSet.add(this);
		connectAccount.increment();
		try {
			sendMessage("连接成功");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 连接关闭时调用
	 */
	@OnClose
	public void onClose() {
		webSocketSet.remove(this);
		connectAccount.decrement();
	}

	/**
	 * 收到客户端消息时调用
	 */
	@OnMessage
	public void onMessage(String message) {
	}

	/**
	 * 服务端向客户端发送消息
	 */
	private void sendMessage(String message) throws IOException {
		this.session.getBasicRemote().sendText(message);
	}

	/**
	 * 单独发送
	 *
	 * @param phone   用户s
	 * @param message 内容
	 * @throws IOException s
	 */
	public static void sendInfo(String phone, String message,Integer platformType) throws IOException {
		for (WebSocketService item : webSocketSet) {
			if (item.platformType.equals(platformType) && item.phone.equals(phone)) {
				item.sendMessage(message);
			}
		}
	}

	/**
	 * 群发自定义消息
	 */
	public static void sendInfos(String message) throws IOException {
		log.info(message);
		for (WebSocketService item : webSocketSet) {
			item.sendMessage(message);
		}
	}
}
