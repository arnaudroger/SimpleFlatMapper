package org.simpleflatmapper.test.junit;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LibrarySets {
    String[] librarySets();

    Class<?>[] includes();
    String[] excludes() default {"org.junit"};

    String[] names() default {};

}
