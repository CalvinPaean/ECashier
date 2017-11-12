package cuny.hackthon.model;

import static cuny.hackthon.utils.CmdUtils.windowsShell;

import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sql.DataSource;

import org.apache.commons.dbutils.handlers.BeanListHandler;

import cuny.hackthon.model.Models.User;
import cuny.hackthon.utils.CmdUtils;
import cuny.hackthon.utils.MathUtils;
import cuny.hackthon.webp.WebpJNI;

public class UserDAO extends AbstractDAO<User, Integer> {

	public UserDAO(DataSource ds) {
		super(ds);
	}
	
	public List<User> getFeatures() {
		String sql = String.format("select * from %s where feature is not null", getBeanTableName());
		try {
			logger.debug("SQL: {}", sql);
			return runner.query(sql, new BeanListHandler<>(User.class));
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
	
	public User getUserByFeature(String image) {
		BufferedImage decoded = WebpJNI.getInstance().decodeImage(image, 320, 240);
		try {
			File file = File.createTempFile("___fea", "ture__");
			ImageIO.write(decoded, "jpg", file);
			String result = windowsShell(String.format("echo %s | anapy face_recog.py 1", file.getAbsolutePath()));
			if(result.length() == 0) return null;
			List<User> features = getFeatures();
			List<BigDecimal> target = CmdUtils.featuresOutput(result);
			BigDecimal threshold = new BigDecimal("0.42");
			for(User user : features) {
				List<BigDecimal> candidate = CmdUtils.featuresOutput(user.getFeature());
				BigDecimal dist = MathUtils.euclideanDist(target, candidate);
				if(dist.compareTo(threshold) < 0) {
					logger.debug("Dist: {}", dist);
					return user;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	public void takePhoto(int id, String photo) {
		User user = findOne(id);
		BufferedImage decoded = WebpJNI.getInstance().decodeImage(photo, 320, 240);
		try {
			File file = File.createTempFile("__pho", "too__");
			ImageIO.write(decoded, "jpg", file);
			String result = windowsShell(String.format("echo %s | anapy face_recog.py 1", file.getAbsolutePath()));
			user.setFeature(result);
			user.setPhoto(photo);
			String sql = String.format("update %s set feature=?, photo=? where id = ?", getBeanTableName());
			logger.debug("SQL : {}", sql);
			runner.update(sql, user.getFeature(), photo, id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
}
