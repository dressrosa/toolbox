/*
 *  唯有读书,不慵不扰
 */
package com.xiaoyu.maple.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hongyu
 * @date 2017-11-22 22:20
 * @description 主封装类,用于实体类-map的转化
 */
public class Maple {

    private static ThreadLocal<Map<String, Object>> local = new ThreadLocal<Map<String, Object>>() {
        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<>();
        }
    };

    private static final Maple INSTANCE = new Maple();

    /**初始化封装
     * @param pakchoi
     * @return
     */
    public static Maple wrap(Object pakchoi) {
        final Object target = pakchoi;
        final Class<?> cls = target.getClass();
        final Field[] fields = cls.getDeclaredFields();
        final Method[] methods = cls.getMethods();

        final Map<String, Object> map = Maple.local.get();
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
            e.printStackTrace();
        }
        Maple.local.set(map);
        return Maple.INSTANCE;
    }

    public Map<String, Object> map() {
        return Maple.local.get();
    }

    /**
     * 忽略对某个字段的转化
     * 
     * @param key
     * @return
     */
    public Maple skip(String key) {
        final Map<String, Object> map = Maple.local.get();
        map.remove(key);
        Maple.local.set(map);
        return Maple.INSTANCE;
    }

    /**
     * 添加新的字段封装或者赋新值
     * 
     * @param key
     * @param value
     * @return
     */
    public Maple stick(String key, Object value) {
        final Map<String, Object> map = Maple.local.get();
        map.put(key, value);
        Maple.local.set(map);
        return Maple.INSTANCE;
    }

    /**
     * 对某个字段进行重命名
     * 
     * @param key
     * @param replace
     * @return
     */
    public Maple rename(String key, String replace) {
        final Map<String, Object> map = Maple.local.get();
        if (map.containsKey(key)) {
            final Object value = map.remove(key);
            map.put(replace, value);
            Maple.local.set(map);
        }
        return Maple.INSTANCE;
    }

    public Map<String, Object> test() {
        final Map<String, Object> map = Maple.local.get();
        if (map.isEmpty()) { 
            return this.map(); 
        }
        return map;

    }

}
