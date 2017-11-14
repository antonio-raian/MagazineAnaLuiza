/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.storehouse.model;

import java.io.Serializable;

/**
 *
 * @author Antonio Raian
 */
public class Product implements Serializable{
    private String cod, name, details, producer, kind;
    private int quantiy;
    private double value;

    public Product(String cod, String name, String details, String producer, String kind, int quantiy, double value) {
        this.cod = cod;
        this.name = name;
        this.details = details;
        this.producer = producer;
        this.kind = kind;
        this.quantiy = quantiy;
        this.value = value;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getQuantiy() {
        return quantiy;
    }

    public void setQuantity(int quantity){
        this.quantiy = quantity;
    }
    
    public void addQuantiy(int quantiy) {
        this.quantiy += quantiy;
    }
    
    public void removeQuantiy(int quantiy) {
        this.quantiy -= quantiy;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return cod + ";" + name + ";" + details + ";" + producer + ";" + kind + ";" + quantiy + ";" +value;
    }
}
