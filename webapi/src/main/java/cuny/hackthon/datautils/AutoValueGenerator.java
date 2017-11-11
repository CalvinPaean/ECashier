package cuny.hackthon.datautils;

import java.util.Random;
import java.util.function.Supplier;

public final class AutoValueGenerator {

	public static class NullSuplier implements Supplier<Object> {
		@Override
		public Object get() { return null; }
	}
	
	public static class TimestampSuplier implements Supplier<Long> {
		@Override
		public Long get() {
			return System.currentTimeMillis();
		}
	}
	
	public static class RandomDoubleSuplier implements Supplier<Double> {
		static Random random = new Random();
		@Override
		public Double get() {
			return (double)Math.round((500 + random.nextDouble()*(3000))*100)/100;
		}
	}

}
