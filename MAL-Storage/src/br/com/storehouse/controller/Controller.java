/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.storehouse.controller;

import br.com.storehouse.model.Product;
import java.util.LinkedList;

/**
 *
 * @author Antonio Raian
 */
public class Controller {

    private LinkedList<Product> products;
    
    public Controller() {
        products = new LinkedList<>();
    }
    
    public boolean addProduct(String cod, String name, String details, String producer, String kind, int quantity, double value){
        if(verifyData(cod, name, details, producer, kind, quantity, value)){
            Product prod = findProduct(cod);
            if(prod==null){
                prod = new Product(cod, name, details, producer, kind, quantity, value);
                products.add(prod);
                return true;
            }else
                prod.addQuantiy(quantity);
        }
        return false;
    }
    
    public boolean updateProduct(String cod, String name, String details, String producer, String kind, int quantity){
        Product prod = findProduct(cod);
        if(prod!=null){
            prod.setCod(cod);
            prod.setDetails(details);
            prod.setKind(kind);
            prod.setName(name);
            prod.setProducer(producer);
            prod.setQuantity(quantity);
            //products.add(prod);
            return true;
        }
        return false;
    }
    
    public boolean removeProduct(String cod){
        Product prod = findProduct(cod);
        if(prod!=null){
            products.remove(prod);
            return true;
        }
        return false;
    }
    
    public LinkedList<String> listProducts(){
        LinkedList<String> prods = new LinkedList<>();
        for(Product p:products){
            prods.add(p.toString());
        }
        return prods;
    }
    
    public String findByCod(String cod){
        Product p = findProduct(cod);
        if(p!=null){
            return p.toString();
        }
        return null;
    }
    //------------------------
    private Product findProduct(String cod) {
        for(Product p:products){
            if(p.getCod().equals(cod)){
                return p;
            }
        }
        return null;
    }
    private boolean verifyData(String cod, String name, String details, String producer, String kind, int quantity, double value){
        if(cod.equals("")||name.equals("")||details.equals("")||producer.equals("")||kind.equals("")||quantity==0||value==0){
            return false;
        }
        return true;
    }
}
