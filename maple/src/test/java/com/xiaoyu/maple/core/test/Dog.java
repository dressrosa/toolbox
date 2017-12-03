package com.xiaoyu.maple.core.test;

import java.util.Date;

public class Dog {

    private String eye;
    private String nose;
    private Date birthDay;

    public String getEye() {
        return eye;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public Dog setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
        return this;
    }

    private Cat cat;

    public Cat getCat() {
        return cat;
    }

    public Dog setCat(Cat cat) {
        this.cat = cat;
        return this;
    }

    public Dog setEye(String eye) {
        this.eye = eye;
        return this;
    }

    public String getNose() {
        return nose;
    }

    public Dog setNose(String nose) {
        this.nose = nose;
        return this;
    }

}
