package com.xiaoyu.maple.core.test;

public class Parent {

//    public Parent() {
//        Object ob = this;
//        if(ob instanceof User) {
//            System.out.println("yes");
//        }
//        else {
//            System.out.println("no");
//        }
//    }
    private int page;
    private String pname;
    private String name;
    
    public String getName() {
        return name;
    }

    public Parent setName(String name) {
        this.name = name;
        return this;
    }

    public int getPage() {
        return page;
    }

    public Parent setPage(int page) {
        this.page = page;
        return this;
    }

    public String getPname() {
        return pname;
    }

    public Parent setPname(String pname) {
        this.pname = pname;
        return this;
    }

}
