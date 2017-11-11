package cuny.hackthon.webapi.controller;

import spark.Request;
import spark.Response;
import static cuny.hackthon.webapi.Server.*;

import java.util.Arrays;
import java.util.Map;

import cuny.hackthon.model.ProductDAO;

public class ProductController {

	
	public static String ShowQRCode(Request req, Response resp) {
		String[] values = req.queryParamsValues("id");
		ProductDAO dao = getDAO(ProductDAO.class);
		Map<Integer, String> map = dao.fectchQrCode(Arrays.stream(values).mapToInt(s->Integer.parseInt(s)).toArray());
		String imgTemplate = "<img alt=\"item %d's qrcode\" src=\"%s\"/>";
		StringBuilder builder = new StringBuilder();
		for(Integer key : map.keySet()) {
			builder.append(String.format(imgTemplate, key, map.get(key)));
		}
		return builder.toString();
	}
}
