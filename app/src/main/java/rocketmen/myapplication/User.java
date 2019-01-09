package rocketmen.myapplication;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by luigi on 02/12/2018.
 */

public class User implements Serializable {

    String id;
    String name;
    String surname;
    String username;
    String password;
    String birthDate;
    String altezza;
    Sex sex;
    //ServicesManager servicesManager;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getSex() {
        if(sex.equals(Sex.MALE)){
            return "M";
        }
        return "F";
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getAltezza() {
        return altezza;
    }

    public void setAltezza(String altezza) {
        this.altezza = altezza;
    }
}
