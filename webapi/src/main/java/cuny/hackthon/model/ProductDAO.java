package cuny.hackthon.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import cuny.hackthon.model.Models.Product;
import cuny.hackthon.utils.ImageUtils;
import cuny.hackthon.webapi.Server;
import cuny.hackthon.webp.WebpJNI;

public class ProductDAO extends AbstractDAO<Product, Integer> {

	private final static String QR_TEXT_TEMPLATE = "%s/product/%s"; 

	public ProductDAO(DataSource ds) {
		super(ds);
	}
	
	public Map<Integer, String> fectchQrCode(int... itemsID) {
		String placeHolder = Arrays.stream(itemsID).mapToObj(id->"?").collect(Collectors.joining(", "));
		String sql = String.format("select id, qrcode from %s where id in (%s)", getBeanTableName(), placeHolder);
		logger.debug("SQL: {}, PARAMS: {}", sql, Arrays.toString(itemsID));
		try {
			ResultSetHandler<Map<Integer, String>> handler = (rs)->{
				Map<Integer, String> result = new HashMap<>();
				while(rs.next()) {
					result.put(rs.getInt(1), rs.getString(2));
				}
				return result;
			};
			return runner.query(sql, handler, Arrays.stream(itemsID).mapToObj(i->(Integer)i).toArray());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int[] generateQRCode() {
		String sql = buildLoadTemplate("qrcode is null");
		final String host = Server.CONFIG.getProperty("host");
		String updateStatement = "update " + getBeanTableName() + " set qrcode = ? where id = ?";
		Connection conn = null;
		try {
			conn = runner.getDataSource().getConnection();
			conn.setAutoCommit(false);
			logger.debug("SQL: {}", sql);
			List<Product> items = runner.query(conn, sql, new BeanListHandler<Product>(Product.class));
			Object[][] params = new Object[items.size()][2];
			for(int i=0; i<items.size(); i++) {
				Product item = items.get(i);
				String text = String.format(QR_TEXT_TEMPLATE, host, item.getCode());
				String qrcodeDataURL = WebpJNI.getInstance().encodeImageDataURL(ImageUtils.generateQRCode(text, 220));
				params[i][0] = qrcodeDataURL;
				params[i][1] = item.getId();
			}
			logger.debug("BATCH SQL: {}", updateStatement);
			int[] result = runner.batch(conn, updateStatement, params);
			conn.commit();
			return result;
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.warn("connection rollback faild.");
			}
			throw new RuntimeException(e);
		} finally {
			if(conn != null) {
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (SQLException e) {
					
				}
			}
		}
	}
}
