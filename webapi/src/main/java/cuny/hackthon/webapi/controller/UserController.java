package cuny.hackthon.webapi.controller;

import cuny.hackthon.model.Models.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cuny.hackthon.model.UserDAO;
import cuny.hackthon.webapi.Server;
import spark.Request;
import spark.Response;

public class UserController {

	
	public static String VerifyUser(Request req, Response resp) {
		UserDAO userDao = Server.getDAO(UserDAO.class);
		String feature = req.body();
		User user = userDao.getUserByFeature(feature);
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
