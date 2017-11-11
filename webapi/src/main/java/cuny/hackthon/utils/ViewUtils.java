package cuny.hackthon.utils;

import java.util.Map;

import de.neuland.jade4j.JadeConfiguration;
import spark.ModelAndView;
import spark.template.jade.JadeTemplateEngine;

public final class ViewUtils {

	private static JadeConfiguration jadeConfig = new JadeConfiguration();
	
	static {
		jadeConfig.setCaching(false);
	}
	
	public static String render(Map<String, Object> model, String viewName) {
		return new JadeTemplateEngine(jadeConfig).render(new ModelAndView(model, viewName));
	}
}
