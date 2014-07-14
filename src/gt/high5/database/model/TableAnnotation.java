package gt.high5.database.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableAnnotation {
	public String defaultValue() default "";
	//for integer or double field can be increased conveniently 
	public boolean increaseWhenUpdate() default false;
}
