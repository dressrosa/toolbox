/*
 *  唯有读书,不慵不扰
 */
package com.xiaoyu.maple.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hongyu
 * @date 2017-11-22 22:20
 * @description 用于实体类-map的转化
 */
class Maple {

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
        Class<?> cls = target.getClass();
        Map<String, Object> initMap = new HashMap<>(cls.getDeclaredFields().length * 2);
        if (isObjectParams(wood)) {
            this.cycleKeys(cls, target, initMap);
        } else {
            initMap.put(String.valueOf(wood), wood);
        }
        this.book = initMap;
    }

    /**
     * 核心转化方法<br/>
     * 主要是对wood及其成员变量递归父类转化
     * 
     * @param targetClass
     * @param wood
     * @param initMap
     */
    @SuppressWarnings("all")
    private void cycleKeys(Class<?> targetClass, Object wood, Map<String, Object> initMap) {
        final Object target = wood;
        final Class<?> cls = targetClass;
        // 存在父类的话,对父类进行转化
        if (checkClass(cls)) {
            this.cycleKeys(cls.getSuperclass(), target, initMap);
        }
        final Field[] fields = cls.getDeclaredFields();
        if (fields.length == 0)
            return;
        final Method[] methods = cls.getDeclaredMethods();

        Map<String, Method> methodMap = new HashMap<>(methods.length);
        for (final Method m : methods) {
            // 放入get方法
            if (m.getParameterCount() == 0 && !"void".equals(m.getReturnType())) {
                methodMap.put(m.getName().toLowerCase(), m);
            }
        }
        // 变量的名,对应转化后map的key
        String fieldName = null;
        String mapleValue = null;
        Method m = null;
        Object ret = null;
        try {
            for (final Field f : fields) {
                fieldName = f.getName();
                // 判断是否有get方法Ø
                m = methodMap.get("get".concat(fieldName.toLowerCase()));
                if (m == null) {
                    continue;
                }
                // 检查注解@Mapable,有注解返回注解的值
                Mapable anno = f.getAnnotation(Mapable.class);
                mapleValue = mapableValue(anno);
                if (!(mapleValue == null || mapleValue.length() == 0)) {
                    fieldName = mapleValue;
                }
                ret = m.invoke(target, null);
                if (isTransfer(anno) && isObjectParams(ret)) {
                    initMap.put(fieldName, this.convertObject2Map(ret));
                } else {
                    initMap.put(fieldName, ret);
                }
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    /**
     * 过滤对象自带的方法 "finalize", "wait", "toString", "hashCode", "getClass", "clone",
     * "registerNatives", "notify", "notifyAll"
     */
    private static final String METHODS = "finalizewaittoStringhashCodegetClasscloneregisterNativesnotifyAll";

    @SuppressWarnings("unused")
    private static boolean isNativeMethod(String methodName) {
        if (METHODS.indexOf(methodName) != -1) {
            return true;
        }
        return false;
    }

    private static boolean checkClass(final Class<?> cls) {
        // Date会造成栈溢出
        // if ("java.util.Date".equals(cls.getName())
        // || "java.lang.Object".equals(cls.getName())
        // || "sun.util.calendar.Gregorian".equals(cls.getName())) {
        // return false;
        // }
        if (cls.getSuperclass() == null) {
            return false;
        }
        // jdk里面的类
        if (cls.getSuperclass().getName().startsWith("java")) {
            return false;
        }
        return true;
    }

    /**
     * 成员变量是对象需要转ma
     * 
     * @param variable
     * @return
     */
    private Map<String, Object> convertObject2Map(Object variable) {
        final Object target = variable;
        Class<?> cls = target.getClass();
        Map<String, Object> variableMap = new HashMap<>(cls.getDeclaredFields().length * 2);
        this.cycleKeys(cls, target, variableMap);
        return variableMap;
    }

    /**
     * 成员变量是基本类型不做转化 只有在map list 或者 类对象(非null)不转化 boolean, byte, char, short, int,
     * long, float, double.
     * 
     * @param param
     * @return
     */
    private static boolean isObjectParams(Object param) {
        if (param == null || param.getClass().isPrimitive()
                || param instanceof String || param instanceof Integer
                || param instanceof Double || param instanceof Date
                || param instanceof Long || param instanceof Float
                || param instanceof Boolean || param instanceof Short
                || param instanceof Character || param instanceof Byte
                || param instanceof Class) {
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
    private static String mapableValue(Mapable anno) {
        if (anno == null || anno.skip()) {
            return null;
        }
        return anno.value();
    }

    private static boolean isTransfer(Mapable anno) {
        if (anno == null) {
            return true;
        }
        return anno.transfer();
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
        if (isObjectParams(value)) {
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
        final Map<String, Object> map = new HashMap<>(fields.length * 2);
        Map<String, Method> methodMap = new HashMap<>(methods.length * 2);
        for (Method m : methods) {
            if (m.getParameterCount() == 0) {
                methodMap.put(m.getName().toLowerCase(), m);
            }
        }
        try {
            for (final Field f : fields) {
                if (methodMap.get("get".concat(f.getName().toLowerCase())) == null) {
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
