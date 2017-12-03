/*
 *  唯有读书,不慵不扰
 */
package com.xiaoyu.maple.core.test;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.xiaoyu.maple.core.MapleUtil;

/**
 * @author hongyu
 * @date 2017-11-23 22:00
 * @description
 */
public class Test {

    public static void main(String[] args) {
        User u = new User();
        Dog dog = new Dog();
        Cat cat = new Cat();
        cat.setEye("ce").setNose("cn");
        dog.setEye("e").setNose("n").setCat(cat);
        u.setAge(88)
                .setChild(new User())
                .setDog(dog)
                .setPage(4)
                .setPname("tom")
                .setName("fdf");

        Map<String, Object> map = MapleUtil.wrap(u)
                .rename("child", "catty")
                .stick("tom", new Object())
                .stick("小猫", "hellokitty")
                .rename("catty", "bigBoy")
                .map();

        System.out.println(map);
        System.out.println(JSON.toJSONString(u));

    }
}
