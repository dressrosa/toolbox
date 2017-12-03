/*
 *  唯有读书,不慵不扰
 */
package com.xiaoyu.maple.core.test;

import com.xiaoyu.maple.core.Mapable;

/**
 * @author hongyu
 * @date 2017-11-23 22:00
 * @description
 */
public class User extends Parent {
    @Mapable(enable = true, value = "niceage")
    private int age;
    private String name;

    private User child;

    private Dog dog;

    public Dog getDog() {
        return dog;
    }

    public User setDog(Dog dog) {
        this.dog = dog;
        return this;
    }

    public User getChild() {
        return child;
    }

    public User setChild(User child) {
        this.child = child;
        return this;
    }

    public int getAge() {
        return age;
    }

    public User setAge(int age) {
        this.age = age;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public User setName(String name) {
        this.name = name;
        return this;
    }

}
