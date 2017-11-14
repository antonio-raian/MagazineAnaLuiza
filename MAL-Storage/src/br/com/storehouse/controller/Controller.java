/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.storehouse.controller;

import br.com.storehouse.model.Product;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

/**
 *
 * @author Antonio Raian
 */
public class Controller {

    private LinkedList<Product> products;
    private File path = new File("Files");
    
    public Controller() throws ClassNotFoundException, IOException {
        products = new LinkedList<>();
        path.mkdir();
        
        File arq = new File(path,"Products.txt");
        System.out.println(arq.length());
        if(arq.exists()&&arq.length()>0){
            products = readFile(arq);
        }        
    }
    
    public boolean addProduct(String cod, String name, String details, String producer, String kind, int quantity, double value) throws IOException{
        if(verifyData(cod, name, details, producer, kind, quantity, value)){
            Product prod = findProduct(cod);
            if(prod==null){
                prod = new Product(cod, name, details, producer, kind, quantity, value);
                products.add(prod);
            }else
                prod.addQuantiy(quantity);
            saveList("Products.txt", products);
            return true;
        }
        return false;
    }
    
    public boolean updateProduct(String cod, String name, String details, String producer, String kind, int quantity, double value) throws IOException{
        Product prod = findProduct(cod);
        if(prod!=null){
            prod.setCod(cod);
            prod.setDetails(details);
            prod.setKind(kind);
            prod.setName(name);
            prod.setProducer(producer);
            prod.setQuantity(quantity);
            //products.add(prod);
            saveList("Products.txt", products);
            return true;
        }
        return false;
    }
    
    public boolean removeProduct(String cod) throws IOException{
        Product prod = findProduct(cod);
        if(prod!=null){
            products.remove(prod);
            saveList("Products.txt", products);
            return true;
        }
        return false;
    }
    
    public boolean saleProduct(String cod, int quantity) throws IOException{
        Product prod = findProduct(cod);
        if(prod!=null){
            prod.removeQuantiy(quantity);
            saveList("Products.txt", products);
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
    //Metodo que lê as informação dos arquivos da ROM para a RAM
    private LinkedList readFile(File arq) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arq));//Usa o Stream pra ler os arquivos
        LinkedList list =  (LinkedList) ois.readObject();//Lê e armazena a lista na memória flash
        ois.close();
        return list;
    }
    //Método que armazena as informaç~es da memoria Ram em arquivos da memória rom
    private void saveList(String name, LinkedList data) throws IOException{
        File arq = new File(path,name);
        arq.createNewFile();
        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(arq)));			
        oos.writeObject(data);
        oos.close();
    }
//    public void persist() throws IOException{
//        saveList("Products", products);
//    }
}
