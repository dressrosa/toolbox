/*
 *  唯有读书,不慵不扰
 */
package com.xiaoyu.maple.core.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.xiaoyu.maple.core.MapleUtil;

/**
 * @author hongyu
 * @date 2017-11-23 22:00
 * @description
 */
public class Test {

    /**
     * 
     */
    public static final int SIZE = 30_000;

    public static void main(String[] args) throws Exception {
        User u = new User();
        Dog dog = new Dog();
        Cat cat = new Cat();
        cat.setEye("ce").setNose("cn");
        dog
                .setBirthDay(new Date())
                .setNose("n").setCat(cat);
        u.setAge(88)
                .setChild(new User())
                .setDog(dog)
                .setPage(4)
                .setPname("tom")
                .setName("fdf");
        
        doTestNomal(u);
        Thread.sleep(500);
        doTestMaple(u);
        
    }

    public static void doTestNomal(User u) {
        long start = System.currentTimeMillis();
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("tom", new Object());
            map.put("pname", u.getPname());
            map.put("name", u.getName());
            map.put("page", u.getPage());
            map.put("age", u.getAge());
            map.put("bigBoy", mapChild(u.getChild()));
            map.put("小猫", "hellokitty");
            map.put("dog1", mapDog(u.getDog()));

            list.add(map);
        }
        System.out.println("normal:" + JSON.toJSONString(list.get(50)));
        System.out.println("normal:" + (System.currentTimeMillis() - start));
    }

    private static Object mapDog(Dog dog) {
        Map<String, Object> map = new HashMap<>(8);
        map.put("birthday", dog.getBirthDay());
        map.put("eye", dog.getEye());
        map.put("nose", dog.getNose());
        map.put("cat", mapCat(dog.getCat()));
        return map;
    }

    private static Object mapCat(Cat cat) {
        Map<String, Object> map = new HashMap<>(4);
        map.put("eye", cat.getEye());
        map.put("nose", cat.getNose());
        return map;
    }

    private static Object mapChild(User u) {
        Map<String, Object> map = new HashMap<>(8);
        map.put("pname", u.getPname() == null ? "" : u.getName());
        map.put("name", u.getName() == null ? "" : u.getName());
        map.put("page", u.getPage());
        map.put("age", u.getAge());
        if (u.getDog() != null)
            map.put("dog", mapDog(u.getDog()));
        return map;
    }

    public static void doTestMaple(User u) {
        long start = System.currentTimeMillis();
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            Map<String, Object> map = MapleUtil.wrap(u)
                    .stick("tom", new Object())
                    .stick("小猫", "hellokitty")
                    .rename("child", "bigBoy")
                    .map();
            list.add(map);
        }
        System.out.println("maple11:" + JSON.toJSONString(MapleUtil.wrap(new Date()).map()));
        System.out.println("maple:" + JSON.toJSONString(list.get(50)));
        System.out.println("maple:" + (System.currentTimeMillis() - start));
    }
}
