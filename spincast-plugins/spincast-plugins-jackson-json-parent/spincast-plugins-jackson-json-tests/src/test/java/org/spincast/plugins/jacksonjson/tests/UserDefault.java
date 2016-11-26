package org.spincast.plugins.jacksonjson.tests;

public class UserDefault implements User {

    private String name;
    private int age;
    private String title;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getAge() {
        return this.age;
    }

    @Override
    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String getTitle() {
        return "Title is: " + this.title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }
}
