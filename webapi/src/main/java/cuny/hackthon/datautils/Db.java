package cuny.hackthon.datautils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

public final class Db {
	
	static Properties dbProp = new Properties();
	
	public static void init() throws IOException {
		InputStream dbInput = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties");
		dbProp.load(dbInput);
	}
	
	final static Object lock = new Object();
	
	private static DataSource ds;
	
	public static DataSource getDs() {
		DataSource datasource = ds;
		if(datasource == null) {
			synchronized (lock) {
				datasource = ds;
				if(datasource == null) datasource = createDs();
			}
		}
		return datasource;
	}
	
	public static DataSource createDs() {
		return createDs(null);
	}
	
	public static DataSource createDs(String database) {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUsername(dbProp.getProperty("username"));
		ds.setPassword(dbProp.getProperty("password"));
		String db = database != null ? database : dbProp.getProperty("database");
		if(db.isEmpty())
			throw new RuntimeException("no database selected.");
		ds.setUrl(dbProp.getProperty("url")+db);
		return ds;
	}
}
