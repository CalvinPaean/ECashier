package cuny.hackthon.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageTest {

	public void anything() throws IOException {
		BufferedImage rawImage = ImageIO.read(new File("D:\\Pictures\\beauty.jpg"));
//		System.out.println(rawImage.getType());
		Image image = rawImage.getScaledInstance(320, 320, Image.SCALE_SMOOTH);
		BufferedImage scaled = new BufferedImage(320, 320, BufferedImage.TYPE_3BYTE_BGR);
		DataBufferByte bytes = new DataBufferByte(new byte[0], 200);
		Graphics2D g = (Graphics2D) scaled.getGraphics();
		g.drawImage(image, 0, 0, null);
		DataBufferByte rawBuffer = (DataBufferByte) scaled.getRaster().getDataBuffer();


//		try(FileOutputStream file = new FileOutputStream("D:\\desktop\\beauty.ppm", false)) {
//			PrintStream out = new PrintStream(file);
//			byte[] bytes = rawBuffer.getData();
//			out.println("P3");
//			out.println("320 320");
//			out.println("255");
//			for(int i=0; i<bytes.length; i+=3) {
//				if(i != 0 && i % 24 == 0) out.println();
//				byte B = bytes[i], G = bytes[i+1], R = bytes[i+2];
//				out.printf("%3d %3d %3d ", R&0xFF, G&0xFF, B&0xFF);
//			}
//			out.println();
//		}
		
//		ImageIO.write(im, formatName, output)
//		for(String name : writer.getFormatNames())
//			System.out.println(name);
//		JFrame frame = new JFrame("My Beauty");
//		JLabel label = new JLabel(new ImageIcon(image));
//		frame.setContentPane(label);
//		frame.pack();
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setVisible(true);
//		new WebpJNI().encode(320, 320, rawBuffer.getData());
	}
}
