package cuny.hackthon.webapi;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSocket
public class LiveStreamSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(LiveStreamSocketHandler.class);
	
	@OnWebSocketConnect
	public void onConnect(Session session) {
		logger.debug("client {} connected", session.getRemoteAddress().getHostString());
		if(!Server.LiveStreamSessions.contains(session)) {
			Server.LiveStreamSessions.add(session);
		}
	}
	
	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) {
		logger.debug("client {} disconnected, {}, {}", 
				session.getRemoteAddress().getHostString(), 
				statusCode, reason);
		if(Server.LiveStreamSessions.contains(session))
			Server.LiveStreamSessions.remove(session);
	}
	
	@OnWebSocketMessage
	public void onMessage(Session session, String image) {
//		System.out.println(image);
		Server.broadcastStream(session, image);
	}
}
