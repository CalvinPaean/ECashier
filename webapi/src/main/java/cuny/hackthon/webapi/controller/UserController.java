package cuny.hackthon.webapi.controller;

import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
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
		int id = Integer.parseInt(req.params("id"));
		String photo = req.params("photo");
		userDao.takePhoto(id, photo);
		return "";
	}
	
	public static String VerifyUser(Request req, Response resp) {
		UserDAO userDao = Server.getDAO(UserDAO.class);
		String feature = req.body();
		User user = userDao.getUserByFeature(feature);
		resp.type("application/json");
		if(user == null) return "{}";
		try {
			return new ObjectMapper().writeValueAsString(user);
		} catch (JsonProcessingException e) {
			return "{}";
		}
	}
	
	public static String NewUser(Request req, Response resp) {
		String feature = req.body();
		System.out.println(feature);
		return "OK";
	}
}
