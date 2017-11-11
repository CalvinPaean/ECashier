package cuny.hackthon.webapi.controller;

import java.util.HashMap;

import cuny.hackthon.utils.ViewUtils;
import spark.Request;
import spark.Response;

public class IndexController {

	public static String WelcomePage(Request req, Response resp) {
		
		return ViewUtils.render(new HashMap<>(), "templates/index");
	}
	
	
}
