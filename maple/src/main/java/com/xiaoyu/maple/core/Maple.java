/*
 *  唯有读书,不慵不扰
 */
package com.xiaoyu.maple.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoyu.ribbon.core.StringUtil;

/**
 * @author hongyu
 * @date 2017-11-22 22:20
 * @description 用于实体类-map的转化
 */
public class Maple {

    private static final Logger log = LoggerFactory.getLogger(Maple.class);

    /**
     * 待转化的类
     */
    private Object pakchoi;

    /**
     * 用户封装类变量的map
     */
    private Map<String, Object> map = null;

    public Maple() {

    }

    public Maple(Object pakchoi) {
        init(pakchoi);
    }

    private void init(Object pakchoi) {
        this.pakchoi = pakchoi;
    }

    public void wrap(Object pakchoi) {
        this.init(pakchoi);
        final Object target = this.pakchoi;
        Map<String, Object> initMap = new HashMap<>();
        cycleKeys(target.getClass(), target, initMap);
        this.map = initMap;
    }

    /**
     * 递归父类转化
     * 
     * @param parent
     * @param pakchoi
     * @param initMap
     */
    private void cycleKeys(Class<?> parent, Object pakchoi, Map<String, Object> initMap) {
        final Object target = pakchoi;
        final Class<?> cls = parent;
        if (cls.getSuperclass() != null) {
            cycleKeys(cls.getSuperclass(), target, initMap);
        }
        final Field[] fields = cls.getDeclaredFields();
        final Method[] methods = cls.getDeclaredMethods();
        try {
            boolean skip = true;
            String fieldName = null;
            for (final Field f : fields) {
                skip = true;
                fieldName = f.getName();
                for (final Method m : methods) {
                    if (m.getReturnType() != f.getType() || m.getParameterCount() > 0) {
                        continue;
                    }
                    // 检查注解@Mapable
                    String value = getMapable(f);
                    if (!StringUtil.isBlank(value)) {
                        fieldName = value;
                    }
                    skip = false;
                }
                if (skip) {
                    continue;
                }
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                }
                initMap.put(fieldName, f.get(target));
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error(e.toString());
        }
    }

    /**
     * 如果加了注解,获取注解的value
     * 
     * @param f
     * @return
     */
    private String getMapable(final Field f) {
        Mapable anno = f.getAnnotation(Mapable.class);
        if (anno == null || !anno.enable()) {
            return null;
        }
        return anno.value();

    }

    public Map<String, Object> map() {
        return this.map;
    }

    /**
     * 忽略对某个字段的转化
     * 
     * @param key
     * @return
     */
    public Maple skip(String key) {
        this.map.remove(key);
        return this;
    }

    /**
     * 添加新的字段封装或者赋新值
     * 
     * @param key
     * @param value
     * @return
     */
    public Maple stick(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

    /**
     * 简单的转map,不考虑任何因素
     * 
     * @param littlePakchoi
     * @return
     */
    public Map<String, Object> simpleMap(Object littlePakchoi) {
        final Object target = littlePakchoi;
        final Class<?> cls = target.getClass();
        final Field[] fields = cls.getDeclaredFields();
        final Method[] methods = cls.getDeclaredMethods();
        final Map<String, Object> map = new HashMap<>();
        try {
            boolean skip = true;
            for (final Field f : fields) {
                skip = true;
                for (final Method m : methods) {
                    if (m.getReturnType() != f.getType() || m.getParameterCount() > 0) {
                        continue;
                    }
                    skip = false;
                }
                if (skip) {
                    continue;
                }
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                }
                map.put(f.getName(), f.get(target));
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error(e.toString());
        }
        return map;
    }

    /**
     * 对某个字段进行重命名
     * 
     * @param key
     * @param replace
     * @return
     */
    public Maple rename(String key, String replace) {
        final Map<String, Object> tmap = this.map;
        if (tmap.containsKey(key)) {
            final Object value = map.remove(key);
            tmap.put(replace, value);
            this.map = tmap;
        }
        return this;
    }

}
