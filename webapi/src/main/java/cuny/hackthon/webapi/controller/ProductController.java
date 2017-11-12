package cuny.hackthon.webapi.controller;

import spark.Request;
import spark.Response;
import static cuny.hackthon.webapi.Server.*;

import java.util.Arrays;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cuny.hackthon.model.Models.Product;
import cuny.hackthon.model.ProductDAO;

public class ProductController {

	
	
	public static String FetchItem(Request req, Response resp) {
		ProductDAO dao = getDAO(ProductDAO.class);
		String code = req.params("code");
		Product product = dao.findByCode(code);
		resp.type("application/json");
		if(product == null) return "{}";
		try {
			return new ObjectMapper().writeValueAsString(product);
		} catch (Exception e) {
			return "{}";
		}
	}
	
	public static String ShowQRCode(Request req, Response resp) {
		String[] values = req.queryParamsValues("id");
		ProductDAO dao = getDAO(ProductDAO.class);
		Map<Integer, String> map = dao.fectchQrCode(Arrays.stream(values).mapToInt(s->Integer.parseInt(s)).toArray());
		String imgTemplate = "<img alt=\"item %d's qrcode\" src=\"%s\"/>";
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for(Integer key : map.keySet()) {
			builder.append(String.format(imgTemplate, key, map.get(key)));
			if(++i == 3) builder.append("<br/>");
		}
		return builder.toString();
	}
}
