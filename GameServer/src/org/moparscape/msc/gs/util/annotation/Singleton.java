package org.moparscape.msc.gs.util.annotation;

import java.lang.annotation.*;

/**
 * 
 * This annotation is used to denote that a Java class is a singleton. Scala
 * objects do not need this annotation, because they are singletons by
 * definition.
 * 
 * @author CodeForFame
 * 
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Singleton {

}
