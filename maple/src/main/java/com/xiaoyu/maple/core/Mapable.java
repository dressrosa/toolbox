/*
 *  唯有读书,不慵不扰
 */
package com.xiaoyu.maple.core;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**对实体类加注解,表示是否转化等特点
 * @author hongyu
 * @date 2017-11-28 23:13
 * @description
 */
@Documented
@Retention(RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface Mapable {

    /**
     * 变量或类转化的名称
     * 
     * @return
     */
    String value();

    /**
     * 是否转化
     * 
     * @return
     */
    boolean enable() default true;

}
