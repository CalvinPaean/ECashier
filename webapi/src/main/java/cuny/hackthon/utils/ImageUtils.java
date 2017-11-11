package cuny.hackthon.utils;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public final class ImageUtils {

	public static void showImage(Image image) {
		JFrame frame = new JFrame("My Picture");
		JLabel pic = new JLabel(new ImageIcon(image));
		frame.setContentPane(pic);
		frame.setResizable(false);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getPreferredSize();
		double x = (screenSize.getWidth()-frameSize.getWidth())*(0.392);
		double y = (screenSize.getHeight()-frameSize.getHeight())*(0.392);
		frame.setLocation((int)x, (int)y);
		SwingUtilities.invokeLater(()->frame.setVisible(true));
	}
	
	public static BufferedImage generateQRCode(String text, int size) {
		BitMatrix qrResult;
		try {
			qrResult = new MultiFormatWriter().encode(text , BarcodeFormat.QR_CODE, size, size, null);
		} catch (WriterException e) {
			throw new RuntimeException(e);
		}
		int w = qrResult.getWidth(), h = qrResult.getHeight();
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
		int white = 0xFFFFFF, black = 0;
		for(int i=0; i<w; i++) {
			for(int j=0; j<h; j++) {
				image.setRGB(i, j, qrResult.get(i, j) ? black : white);
			}
		}
		return image;
	}
}
