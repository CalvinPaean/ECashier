package cuny.hackthon.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Arrays;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;

import cuny.hackthon.datautils.Db;
import cuny.hackthon.model.Models.User;
import cuny.hackthon.model.ProductDAO;
import cuny.hackthon.utils.CmdUtils;
import cuny.hackthon.model.UserDAO;



public class MockData {

	private static DataSource ds;
	
	private UserDAO userDao = new UserDAO(ds);
	private ProductDAO itemDao = new ProductDAO(ds);
	
	ObjectMapper mapper = new ObjectMapper();
	
	public MockData() {
		mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
	}
	
	
	@BeforeClass
	public static void init() throws IOException {
		Db.init();
		ds = Db.getDs();
	}
	
	
	@Test
	public void testInsertUsers() throws IOException {
		InputStream data = Thread.currentThread().getContextClassLoader().getResourceAsStream("user_data_for_insert.json");
		StringWriter writer = new StringWriter();
		CmdUtils.ioFlow(new InputStreamReader(data), writer);
		int[] result = userDao.batchInsert(writer.toString());
		System.out.println(Arrays.toString(result));
	}
	
	@Test
	public void testAddFeature() throws IOException {
		try(FileInputStream fileIn = new FileInputStream("D:\\desktop\\Q1.txt")) {
			StringWriter writer = new StringWriter();
			CmdUtils.ioFlow(new InputStreamReader(fileIn), writer);
			System.out.println(userDao.addFeature(1, writer.toString()));
		}
	}
	
	@Test
	public void testValidFeature() throws IOException {
		try(FileInputStream fileIn = new FileInputStream("D:\\desktop\\Y1.txt")) {
			StringWriter writer = new StringWriter();
			CmdUtils.ioFlow(new InputStreamReader(fileIn), writer);
			System.out.println(userDao.getUserByFeature(writer.toString()));
		}
	}
	
	@Test
	public void testUpdateUsers() throws IOException {
		InputStream data = Thread.currentThread().getContextClassLoader().getResourceAsStream("user_data_for_update.json");
		StringWriter writer = new StringWriter();
		CmdUtils.ioFlow(new InputStreamReader(data), writer);
//		System.out.println(writer.toString());
		System.out.println(Arrays.toString(userDao.batchUpdate(writer.toString())));
	}
	
	@Test
	public void testInsertItems() throws IOException {
		InputStream data = Thread.currentThread().getContextClassLoader().getResourceAsStream("item_data_for_insert.json");
		StringWriter writer = new StringWriter();
		CmdUtils.ioFlow(new InputStreamReader(data), writer);
		int[] result = itemDao.batchInsert(writer.toString());
		System.out.println(Arrays.toString(result));
	}
	
	@Test
	public void testDeleteUsers() {
		
	}
	
	@Test
	public void testProductQRCode() {
		int[] result = itemDao.generateQRCode();
		System.out.println(Arrays.toString(result));
	}
}
