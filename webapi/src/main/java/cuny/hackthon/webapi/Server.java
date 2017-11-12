package cuny.hackthon.webapi;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.staticFiles;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javafx.css.SelectorPartitioning;

import cuny.hackthon.datautils.Db;
import cuny.hackthon.model.AbstractDAO;
import cuny.hackthon.model.JournalDAO;
import cuny.hackthon.model.OrderProductDAO;
import cuny.hackthon.model.ProductDAO;
import cuny.hackthon.model.UserDAO;
import cuny.hackthon.utils.ViewUtils;
import cuny.hackthon.webapi.controller.IndexController;
import cuny.hackthon.webapi.controller.ProductController;
import cuny.hackthon.webapi.controller.UserController;
import spark.Spark;
import spark.debug.DebugScreen;

public class Server {

	public static final Properties CONFIG = new Properties();
	
	private static final Map<Class<?>, AbstractDAO<?, ?>> DAO_SUPLIER;
	
	private static Logger logger = LoggerFactory.getLogger(Server.class);
	
	public static final Set<Session> LiveStreamSessions = ConcurrentHashMap.newKeySet(); 
	
	@SuppressWarnings("unchecked")
	public static <T> T getDAO(Class<T> daoClass) {
		return (T) DAO_SUPLIER.get(daoClass);
	}
	
	static {
		InputStream input = Thread.currentThread().getContextClassLoader()
								  .getResourceAsStream("config.properties");
		try {
			CONFIG.load(input);
		} catch (IOException e) {
			logger.warn("config file did not load properly.");
		}
		try {
			Db.init();
			Map<Class<?>, AbstractDAO<?, ?>> register = new HashMap<>();
			register.put(UserDAO.class, new UserDAO(Db.getDs()));
			register.put(ProductDAO.class, new ProductDAO(Db.getDs()));
			register.put(JournalDAO.class, new JournalDAO(Db.getDs()));
			register.put(OrderProductDAO.class, new OrderProductDAO(Db.getDs()));
			DAO_SUPLIER = Collections.unmodifiableMap(register);
		} catch (IOException e) {
			logger.error("database did not initialize properly.");
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args)  {
		port(3000);
		
//		String projectDir = System.getProperty("user.dir");
//	    String staticDir = "/src/main/ resources/public";
//	    staticFiles.externalLocation(projectDir + staticDir);
		staticFiles.location("/public");
	
//		staticFiles.expireTime(600L);
		DebugScreen.enableDebugScreen();
		
		Spark.webSocket("/livestream", LiveStreamSocketHandler.class);
		
		get(WebAPI.Path.index, IndexController::WelcomePage);
		get(WebAPI.Path.SHOW_QRCODE, ProductController::ShowQRCode);
		get(WebAPI.Path.SHOW_ONE_ITEM, ProductController::FetchItem);
		get(WebAPI.Path.TAKE_PHOTO, UserController::ViewPhoto);
		post(WebAPI.Path.TAKE_PHOTO, UserController::TakePhoto);
		put(WebAPI.AuthPath.VERIFY_USER, UserController::VerifyUser);
		post(WebAPI.AuthPath.newUser, UserController::NewUser);
		get("/livestream/", (req, res)->ViewUtils.render(new HashMap<>(), "templates/livestream"));
		
		Spark.after("*", (req, res)->{
			res.header("Content-Encoding", "gzip");
		});

	}

	static ExecutorService service = Executors.newFixedThreadPool(8);
	
	public static void broadcastStream(Session sender, String image) {
		
		service.submit(new Runnable() {
			
			@Override
			public void run() {
				LiveStreamSessions.stream().filter(s->!s.equals(sender) && s.isOpen()).forEach(s->{
					try {
						s.getRemote().sendString(image);
					} catch (IOException e) {
						logger.warn("{} write unsuccessfully", s.getRemoteAddress().toString());
					}
				});
			}
		});

	}
}
