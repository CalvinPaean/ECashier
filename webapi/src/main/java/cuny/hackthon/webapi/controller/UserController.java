package cuny.hackthon.webapi.controller;

import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import cuny.hackthon.model.Models.User;
import cuny.hackthon.model.UserDAO;
import cuny.hackthon.utils.ViewUtils;
import cuny.hackthon.webapi.Server;
import spark.Request;
import spark.Response;

public class UserController {

	
	public static String ViewPhoto(Request req, Response resp) {
		return ViewUtils.render(new HashMap<>(), "templates/takephoto");
	}
	
	public static String TakePhoto(Request req, Response resp) {
		UserDAO userDao = Server.getDAO(UserDAO.class);
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
		resp.type("application/json");
		try {
			HashMap<String, Object> map = mapper.readValue(req.body(), typeRef);
			System.out.println(map);
			userDao.takePhoto(Integer.parseInt(map.get("id").toString()), map.get("photo").toString());
		} catch (Exception e) {
			e.printStackTrace();
			return "{ok:0}";
		}
		return "{ok:1}";
	}
	
	public static String VerifyUser(Request req, Response resp) {
		UserDAO userDao = Server.getDAO(UserDAO.class);
		String feature = req.body();
		User user = userDao.getUserByFeature(feature);
		resp.type("application/json");
		if(user == null) return "{}";
		try {
			return new ObjectMapper().writeValueAsString(user);
		} catch (Exception e) {
			return "{}";
		}
	}
	
	public static String NewUser(Request req, Response resp) {
		String feature = req.body();
		System.out.println(feature);
		return "OK";
	}
}
