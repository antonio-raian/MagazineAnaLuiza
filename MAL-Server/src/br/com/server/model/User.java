/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.server.model;

import java.util.LinkedList;

/**
 *
 * @author Antonio Raian
 */
public class User {
    private String name, login, passworld;
    private LinkedList<String> shoppings;

    public User() {
    }
    
    public User(String name, String login, String passworld) {
        this.name = name;
        this.login = login;
        this.passworld = passworld;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassworld() {
        return passworld;
    }

    public void setPassworld(String passworld) {
        this.passworld = passworld;
    }

    public LinkedList<String> getShoppings() {
        return shoppings;
    }

    public void setShoppings(LinkedList<String> shoppings) {
        this.shoppings = shoppings;
    }

    @Override
    public String toString() {
        return name + ";" + login + ";" + passworld;
    }
    
}
