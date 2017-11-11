package cuny.hackthon.datautils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;
import cuny.hackthon.datautils.AutoValueGenerator.NullSuplier;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Column {
	String value() default "";
	boolean primaryKey() default false;
	Class<? extends Supplier<?>> autoVaule() default NullSuplier.class;
}
