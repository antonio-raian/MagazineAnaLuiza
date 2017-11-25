/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.server.control;

import br.com.server.model.Product;
import java.util.LinkedList;

/**
 *
 * @author Antonio Raian
 */
public class Controller {
    private LinkedList<Product> products;

    public Controller() {
        this.products = new LinkedList<>();
    }

    public LinkedList getProducts() {
        return products;
    }

    public String newProduct(String data) {
        String[] str = data.split(";");
        Product prod = new Product(str[0], str[1], str[2], str[3], str[4], Integer.parseInt(str[5]), Double.parseDouble(str[6]));
        if(!products.contains(prod)){
            products.add(prod);
            return "SUCCESS";
        }
        return "FAIL";
    }

    public String updateProduct(String data) {
        String[] str = data.split(";");
        Product prod = findByCode(str[0]);
        if(prod!=null){
            prod.setName(str[1]);
            prod.setDetails(str[2]);
            prod.setProducer(str[3]);
            prod.setKind(str[4]);
            prod.setQuantity(Integer.parseInt(str[5]));
            prod.setValue(Double.parseDouble(str[6]));
            return "SUCCESS";
        }
        return "FAIL";
    }
    
    public Product findByCode(String code){
        for(Product p:products){
            if(p.getCod().equals(code))
                return p;
        }
        return null;
    }

    public String deleteProduct(String code) {
        Product prod = findByCode(code);
        if(products.remove(prod)){
            return "SUCCESS";
        }
        return "FAIL";
    }
    
}
