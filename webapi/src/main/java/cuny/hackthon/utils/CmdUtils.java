package cuny.hackthon.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CmdUtils {
	
	static final Logger logger = LoggerFactory.getLogger(CmdUtils.class);  

	public static void ioFlow(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[1024];
		int len = -1;
		while((len = input.read(buffer)) != -1)
			output.write(buffer, 0, len);
	}
	
	public static void ioFlow(Reader reader, Writer writer) throws IOException {
		char[] buffer = new char[1024];
		int len = -1;
		while((len = reader.read(buffer)) != -1)
			writer.write(buffer, 0, len);
	}
	
	public static int executeCmd(OutputStream output, String... cmds) 
			throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder(cmds)
									.redirectErrorStream(true);
		Process process = builder.start();
		process.getOutputStream().flush();
		process.getOutputStream().close();
		ioFlow(process.getInputStream(), output);
		process.waitFor();
		int retVal = process.exitValue();
		process.destroy();
		return retVal;
	}
	
	public static String windowsShell(String cmd, Object... args) throws IOException, InterruptedException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int exitVal = executeCmd(baos, "cmd", "/c", String.format(cmd, args));
		StringWriter output = new StringWriter();
		ioFlow(new InputStreamReader(new ByteArrayInputStream(baos.toByteArray())), output);
		if(exitVal != 0) {
			logger.warn(output.toString());
			return null;
		}
		return output.toString();
	}
	
	public static double[] featureDoubleArray(String features) {
		List<Double> result = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new StringReader(features));
		String line = null;
		try {
			while((line = reader.readLine()) != null) {
				if(!line.isEmpty()) {
					result.add(Double.parseDouble((line)));
				}
			}
		} catch (IOException e) {
			//swallow io exception
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result.stream().mapToDouble(d->(double)d).toArray();
	}
	
	public static List<BigDecimal> featuresOutput(String features) {
		List<BigDecimal> result = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new StringReader(features));
		String line = null;
		try {
			while((line = reader.readLine()) != null) {
				if(!line.isEmpty()) {
					result.add(new BigDecimal(line));
				}
			}
		} catch (IOException e) {
			//swallow io exception
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
		
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		long start = System.currentTimeMillis();
		

//		String features1 = windowsShell("echo d:\\desktop\\q1.jpg | anapy face_recog.py 1");
		String features2 = windowsShell("echo d:\\desktop\\img_avatar1.jpg | anapy face_recog.py 1");
		System.out.println(features2.length());
//		List<BigDecimal> f1 = featuresOutput(features1);
//		List<BigDecimal> f2 = featuresOutput(features2);
//		System.out.println(MathUtils.euclideanDist(f1, f2));
		System.out.printf("it takes %d ms", System.currentTimeMillis()-start);
	}
}
