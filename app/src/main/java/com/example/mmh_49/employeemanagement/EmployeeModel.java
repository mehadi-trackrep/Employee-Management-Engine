package com.example.mmh_49.employeemanagement;

import java.sql.Blob;

/**
 * Created by MMH-49 on 7/8/2020.
 */

public class EmployeeModel {
    private int id;
    private String name, age;
    private String img;
    private String gender; /// Male, Female, Other)
    //Constructor

    public EmployeeModel(String name, String age, String gender, String img) {
        this.name = name;
        this.age = age;
        this.img = img;
        this.gender = gender;
    }

    public EmployeeModel(int id, String name, String age, String gender, String img) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.img = img;
        this.gender = gender;
    }

    public EmployeeModel(){
        this.id = -1;
        this.name = null;
        this.age = null;
        this.img = null;
        this.gender = null;
    }
    ///getters & setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}