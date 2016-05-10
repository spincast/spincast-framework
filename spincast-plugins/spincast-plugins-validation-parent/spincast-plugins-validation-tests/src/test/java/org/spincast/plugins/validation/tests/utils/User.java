package org.spincast.plugins.validation.tests.utils;

public class User implements IUser {

    private String name;
    private String nickName;
    private String title;
    private String email;
    private Integer age;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getNickName() {
        return this.nickName;
    }

    @Override
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Integer getAge() {
        return this.age;
    }

    @Override
    public void setAge(Integer age) {
        this.age = age;
    }

}
