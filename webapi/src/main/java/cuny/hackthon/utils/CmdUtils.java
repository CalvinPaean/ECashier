package cuny.hackthon.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public final class CmdUtils {

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
		process.waitFor();
		int retVal = process.exitValue();
		ioFlow(process.getInputStream(), output);
		process.destroy();
		return retVal;
	}
	
	public static String windowsShell(String cmd, Object... args) throws IOException, InterruptedException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int exitVal = executeCmd(baos, "cmd", "/c", String.format(cmd, args));
		if(exitVal != 0) return null;
		StringWriter output = new StringWriter();
		ioFlow(new InputStreamReader(new ByteArrayInputStream(baos.toByteArray())), output);
		return output.toString();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		
		System.out.println(windowsShell("echo d:\\hello | python echo.py"));
	}
}
