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
    private Object wood = null;

    /**
     * 用户封装类变量的map
     */
    private Map<String, Object> book = null;

    public Maple() {

    }

    public Maple(Object wood) {
        init(wood);
    }

    private void init(Object wood) {
        this.wood = wood;
    }

    /**
     * 封装对象<br/>
     * 对象转化首先都是通过调用此方法来初始化map对象
     * 
     * @param wood
     */
    public void wrap(Object wood) {
        this.init(wood);
        final Object target = this.wood;
        Map<String, Object> initMap = new HashMap<>();
        this.cycleKeys(target.getClass(), target, initMap);
        this.book = initMap;
    }

    /**
     * 核心转化方法<br/>
     * 主要是对wood及其成员变量递归父类转化
     * 
     * @param parent
     * @param wood
     * @param initMap
     */
    private void cycleKeys(Class<?> parent, Object wood, Map<String, Object> initMap) {
        final Object target = wood;
        final Class<?> cls = parent;
        // 存在父类的话,对父类进行转化
        if (cls.getSuperclass() != null) {
            cycleKeys(cls.getSuperclass(), target, initMap);
        }
        final Field[] fields = cls.getDeclaredFields();
        final Method[] methods = cls.getDeclaredMethods();
        try {
            // 标记成员变量是否跳过转化
            boolean skip = true;
            // 变量的名,对应转化后map的key
            String fieldName = null;
            for (final Field f : fields) {
                skip = true;
                fieldName = f.getName();
                for (final Method m : methods) {
                    // 判断是否有get方法
                    if (m.getReturnType() != f.getType() || m.getParameterCount() > 0) {
                        continue;
                    }
                    // 检查注解@Mapable,有注解返回注解的值
                    String value = this.mapableValue(f);
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
                if (this.checkIsObjectParams(f.get(target))) {
                    initMap.put(fieldName, this.convertObject2Map(f.get(target)));
                } else {
                    initMap.put(fieldName, f.get(target));
                }

            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error(e.toString());
        }
    }

    /**
     * 成员变量是对象需要转ma
     * 
     * @param variable
     * @return
     */
    private Map<String, Object> convertObject2Map(Object variable) {
        final Object target = variable;
        Map<String, Object> variableMap = new HashMap<>();
        this.cycleKeys(target.getClass(), target, variableMap);
        return variableMap;
    }

    /**
     * 成员变量是基本类型不做转化 只有在map list 或者 类对象(非null)不转化
     * 
     * @param param
     * @return
     */
    private boolean checkIsObjectParams(Object param) {
        if (param == null || param instanceof String || param instanceof Integer
                || param instanceof Double || param instanceof Boolean
                || param instanceof Float || param instanceof Byte
                || param instanceof Short || param instanceof Long) {
            return false;
        }
        return true;
    }

    /**
     * 如果加了注解,获取注解的value
     * 
     * @param f
     * @return
     */
    private String mapableValue(final Field f) {
        Mapable anno = f.getAnnotation(Mapable.class);
        if (anno == null || !anno.enable()) {
            return null;
        }
        return anno.value();

    }

    /**
     * 输出map<br/>
     * 所有操作的最后一步
     * 
     * @return
     */
    public Map<String, Object> map() {
        return this.book;
    }

    /**
     * 忽略对某个字段的转化
     * 
     * @param key
     * @return
     */
    public Maple skip(String key) {
        this.book.remove(key);
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
        if (this.checkIsObjectParams(value)) {
            this.book.put(key, this.convertObject2Map(value));
            return this;
        }
        this.book.put(key, value);
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
        final Map<String, Object> tmap = this.book;
        if (tmap.containsKey(key)) {
            final Object value = this.book.remove(key);
            tmap.put(replace, value);
            this.book = tmap;
        }
        return this;
    }

}
