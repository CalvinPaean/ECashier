package cuny.hackthon.model;

import static cuny.hackthon.utils.CmdUtils.windowsShell;

import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sql.DataSource;

import org.apache.commons.dbutils.handlers.ArrayHandler;

import cuny.hackthon.model.Models.User;
import cuny.hackthon.utils.CmdUtils;
import cuny.hackthon.utils.MathUtils;
import cuny.hackthon.webp.WebpJNI;

public class UserDAO extends AbstractDAO<User, Integer> {

	public UserDAO(DataSource ds) {
		super(ds);
	}
	
	public String[] getFeatures() {
		String sql = String.format("select feature from %s where id is not null", getBeanTableName());
		try {
			logger.debug("SQL: {}", sql);
			Object[] result = runner.query(sql, new ArrayHandler());
			return Arrays.stream(result).map(f->f.toString()).toArray(String[]::new);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int addFeature(int id, String image) {
		BufferedImage decoded = WebpJNI.getInstance().decodeImage(image, 320, 240);
		try {
			File file = File.createTempFile("___fea", "ture__");
			ImageIO.write(decoded, "jpg", file);
			String result = windowsShell(String.format("echo %s | anapy face_recog.py 1", file.getAbsolutePath()));
			String sql = String.format("update %s set feature = ? where id = ?", getBeanTableName());
			logger.debug("SQL: {}", sql);
			return runner.update(sql, result, id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean getUserByFeature(String image) {
		BufferedImage decoded = WebpJNI.getInstance().decodeImage(image, 320, 240);
		try {
			File file = File.createTempFile("___fea", "ture__");
			ImageIO.write(decoded, "jpg", file);
			String result = windowsShell(String.format("echo %s | anapy face_recog.py 1", file.getAbsolutePath()));
			String[] features = getFeatures();
			List<BigDecimal> target = CmdUtils.featuresOutput(result);
			BigDecimal threshold = new BigDecimal("0.5");
			for(String feature : features) {
				List<BigDecimal> candidate = CmdUtils.featuresOutput(feature);
				if(MathUtils.euclideanDist(target, candidate).compareTo(threshold) < 0) {
					return true;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return false;
	}
	
}
