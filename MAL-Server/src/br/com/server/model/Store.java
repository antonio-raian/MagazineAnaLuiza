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
public class Store {
    private String address;
    private int coordinateX,coordinateY;
    private LinkedList<Product> products;

    public Store(){
        
    }
    
    public Store(String address,int coordinateX, int coordinateY) {
        this.address = address;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(int coordinateX) {
        this.coordinateX = coordinateX;
    }

    public int getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(int coordinateY) {
        this.coordinateY = coordinateY;
    }

    public LinkedList<Product> getProducts() {
        return products;
    }

    public void setProducts(LinkedList<Product> products) {
        this.products = products;
    }
    
    public void addProduct(Product p){
        this.products.add(p);
    }
    
    public void removeProduct(Product p){
        this.products.remove(p);
    }
}
