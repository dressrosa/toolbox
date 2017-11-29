/*
 *  唯有读书,不慵不扰
 */
package com.xiaoyu.maple.core;

import java.util.Map;

/**
 * @author hongyu
 * @date 2017-11-29 22:59
 * @description 工具类
 */
public class MapleUtil {

    private static ThreadLocal<Maple> local = new ThreadLocal<Maple>() {
        @Override
        protected Maple initialValue() {
            return new Maple();
        }
    };

    private static final MapleUtil INSTANCE = new MapleUtil();

    /**
     * 初始化封装
     * 
     * @param pakchoi
     * @return
     */
    public static MapleUtil wrap(Object pakchoi) {
        local.get().wrap(pakchoi);
        return MapleUtil.INSTANCE;
    }

    public Map<String, Object> map() {
        return local.get().map();
    }

    /**
     * 忽略对某个字段的转化
     * 
     * @param key
     * @return
     */
    public MapleUtil skip(String key) {
        local.get().skip(key);
        return MapleUtil.INSTANCE;
    }

    /**
     * 添加新的字段封装或者赋新值
     * 
     * @param key
     * @param value
     * @return
     */
    public MapleUtil stick(String key, Object value) {
        local.get().stick(key, value);
        return MapleUtil.INSTANCE;
    }

    /**
     * 简单的转map,不考虑任何因素
     * 
     * @param littlePakchoi
     * @return
     */
    public static Map<String, Object> simpleMap(Object littlePakchoi) {
        return local.get().simpleMap(littlePakchoi);

    }

    /**
     * 对某个字段进行重命名
     * 
     * @param key
     * @param replace
     * @return
     */
    public MapleUtil rename(String key, String replace) {
        local.get().rename(key, replace);
        return MapleUtil.INSTANCE;
    }

}