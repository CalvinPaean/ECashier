package cuny.hackthon.utils;

import java.math.BigDecimal;
import java.util.List;

public final class MathUtils {

	public static BigDecimal newtonSqrt(BigDecimal val) {
		int acc = 500;
		BigDecimal result = BigDecimal.TEN;
		BigDecimal TWO = BigDecimal.valueOf(2);
		while(acc >= 0) {
			BigDecimal fx = result.pow(2).subtract(val);
			BigDecimal fprime = TWO.multiply(result);
			result = fx.divide(fprime, 50, BigDecimal.ROUND_HALF_EVEN);
			acc--;
		}
		return result;
	}
	
	public static BigDecimal euclideanDist(List<BigDecimal> vec1, List<BigDecimal> vec2) {
		if(vec1.size() != vec2.size())
			throw new IllegalArgumentException("vectors dimensions are different");
		BigDecimal sum = BigDecimal.ZERO;
		for(int i=0; i<vec1.size(); i++) {
			sum = sum.add(vec1.get(i).multiply(vec2.get(i)));
		}
		return newtonSqrt(sum);
	}
}
