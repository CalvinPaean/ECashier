package cuny.hackthon.webp;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Base64;

import javax.imageio.ImageIO;

import cuny.hackthon.utils.CmdUtils;
import cuny.hackthon.utils.ImageUtils;

public class WebpJNI {

	private static WebpJNI instance = new WebpJNI();
	
	private WebpJNI() {}
	
	public static WebpJNI getInstance() {
		return instance;
	}
	
	static {
		System.loadLibrary("WebpJNI");
	}
	
	private native byte[] decode(Rectangle rect, byte[] image);
	
	private native byte[] encode(int width, int height, byte[] raw);
	
	public BufferedImage decodeImage(byte[] webpImageBytes) {
		return decodeImage(webpImageBytes, 0, 0);
	}
	
	public BufferedImage decodeImage(byte[] webpImageBytes, int scaleWidth, int scaleHeight) {
		Rectangle rect = new Rectangle();
		rect.setBounds(0, 0, scaleWidth, scaleHeight);
		byte[] rawBytes = decode(rect, webpImageBytes);
		BufferedImage image = new BufferedImage((int)rect.getWidth(), (int)rect.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		DataBufferByte byteBuffer = (DataBufferByte)image.getRaster().getDataBuffer();
		for(int i=0; i<rawBytes.length; i++) 
			byteBuffer.setElem(i, rawBytes[i]);
		return image;
	}
	
	public BufferedImage decodeImage(String dataURL) {
		return decodeImage(dataURL, 0, 0);
	}
	
	public BufferedImage decodeImage(String dataURL, int scaleWidth, int scaleHeight) {
		int comma = dataURL.indexOf(',');
		if(!dataURL.substring(0, comma).contains("webp"))
			return null;
		String base64Data = dataURL.substring(comma+1);
		byte[] rawBytes = Base64.getDecoder().decode(base64Data);
		return decodeImage(rawBytes, scaleWidth, scaleHeight);
	}
	
	public byte[] encodeImage(BufferedImage image) {
		return encodeImage(image, image.getWidth(), image.getHeight());
	}
	
	public byte[] encodeImage(BufferedImage image, int scaleWidth, int scaleHeight) {
		Image temp = image.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_SMOOTH);
		BufferedImage tobeEncoded = new BufferedImage(scaleWidth, scaleHeight, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = (Graphics2D) tobeEncoded.getGraphics();
		g.drawImage(temp, 0, 0, scaleWidth, scaleHeight, null);
		g.dispose();
		DataBufferByte buffer = (DataBufferByte) tobeEncoded.getData().getDataBuffer();
		byte[] rawBytes = buffer.getData();
		return encode(scaleWidth, scaleHeight, rawBytes);
	}
	
	public String encodeImageDataURL(BufferedImage image) {
		return encodeImageDataURL(image, image.getWidth(), image.getHeight());
	}
	
	public String encodeImageDataURL(BufferedImage image, int scaleWidth, int scaleHeight) {
		byte[] bytes = encodeImage(image, scaleWidth, scaleHeight);
		StringWriter writer = new StringWriter();
		writer.write("data:image/webp;base64,");
		String content = Base64.getEncoder().encodeToString(bytes);
		writer.write(content);
		return writer.toString();
	}
	
	public static void main(String[] args) throws IOException {
		try(FileInputStream input = new FileInputStream("D:\\Desktop\\Q2.txt")) {
			StringWriter writer = new StringWriter();
			CmdUtils.ioFlow(new InputStreamReader(input), writer);
			BufferedImage image = getInstance().decodeImage(writer.toString());
			ImageIO.write(image, "jpg", new File("D:\\desktop\\q2.jpg"));
			ImageUtils.showImage(image);
		}
	}
}
