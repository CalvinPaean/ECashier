package cuny.hackthon.webapi.controller;

import spark.Request;
import spark.Response;

public class UserController {

	
	public static String NewUser(Request req, Response resp) {
		String feature = req.body();
		System.out.println(feature);
		return "OK";
	}
}
