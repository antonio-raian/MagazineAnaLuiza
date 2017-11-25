/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.storehouse.controller;

import br.com.storehouse.model.Product;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author Antonio Raian
 */
public class Controller {

    private LinkedList<Product> products;
    private LinkedList<String> log;
    private File path = new File("Files");
    private final String fileName, fileNameLog;
    private int coordinateX, coordinateY;
    
    public Controller(String number, String coordinateX, String coordinateY) throws ClassNotFoundException, IOException {
        products = new LinkedList<>();
        log = new LinkedList<>();
        this.coordinateX = Integer.parseInt(coordinateX);
        this.coordinateY = Integer.parseInt(coordinateY);
        
        path.mkdir();
        fileName = "Products_"+number+".txt";
        fileNameLog = "LOG_STORE_"+number+".txt";
        File arqProducts = new File(path,fileName);      
        
        File arqLog = new File(path, fileNameLog);
        System.out.println(arqProducts.length());
        if(!arqProducts.exists()){
            arqProducts.createNewFile();
            saveData(fileName, this.coordinateX+":"+this.coordinateY);
        }
        products = readFile(arqProducts, 0);
            
        if(!arqLog.exists()){
            arqLog.createNewFile();
            saveData(fileNameLog, this.coordinateX+":"+this.coordinateY);
        }
        log = readFile(arqLog, 1);
         
    }
    
    public String addProduct(String cod, String name, String details, String producer, String kind, int quantity, double value) throws IOException{
        if(verifyData(cod, name, details, producer, kind, quantity, value)){
            Product prod = findProduct(cod);
            if(prod==null){
                prod = new Product(cod, name, details, producer, kind, quantity, value);
                products.add(prod);
                saveData(fileName, prod.toString());
                return prod.toString();
            }else
                return "0";
        }
        return null;
    }
    
    //Metodo de update de produto que mantem a quantidade de cada depósito
    public boolean updateProduct(String cod, String name, String details, String producer, String kind, double value) throws IOException{
        Product prod = findProduct(cod);
        if(prod!=null){
            prod.setCod(cod);
            prod.setDetails(details);
            prod.setKind(kind);
            prod.setName(name);
            prod.setProducer(producer);
            prod.setValue(value);
            saveList(fileName, products);
            return true;
        }
        return false;
    }
    
    //Metodo de update de produto para o depósito
    public boolean updateProduct(String cod, String name, String details, String producer, String kind,int quantity, double value) throws IOException{
        Product prod = findProduct(cod);
        if(prod!=null){
            prod.setCod(cod);
            prod.setDetails(details);
            prod.setKind(kind);
            prod.setName(name);
            prod.setProducer(producer);
            prod.setQuantity(quantity);
            prod.setValue(value);
            saveList(fileName, products);
            return true;
        }
        return false;
    }
    
    public boolean removeProduct(String cod) throws IOException{
        Product prod = findProduct(cod);
        if(prod!=null){
            products.remove(prod);
            saveList(fileName, products);
            return true;
        }
        return false;
    }
    
    public boolean saleProduct(String cod, int quantity) throws IOException{
        Product prod = findProduct(cod);
        if(prod!=null){
            updateProduct(prod.getCod(), prod.getName(), prod.getDetails(), prod.getProducer(), prod.getKind(), prod.getQuantiy()-quantity, prod.getValue());
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
        if(cod.equals("")||name.equals("")||details.equals("")||producer.equals("")||kind.equals("")){
            return false;
        }
        return true;
    }
    //Metodo que lê as informação dos arquivos da ROM para a RAM
    private LinkedList readFile(File arq, int typeFile) throws IOException, ClassNotFoundException {
        LinkedList list = new LinkedList();
        BufferedReader buffRead = new BufferedReader(new FileReader(arq));
        String line = buffRead.readLine();
        int i = 0;
        while (line!=null) {
            if(typeFile==0&&i>0){
                String[]aux = line.split(";");
                Product p = new Product(aux[0], aux[1], aux[2], aux[3], aux[4], Integer.parseInt(aux[5]), Double.parseDouble(aux[6]));
                list.add(p);
            }else if(typeFile==1&&i>0){
                list.add(line);
            }
            line = buffRead.readLine();
            i++;
        }
        return list;
    }
    //Método que armazena as informações da memoria Ram em arquivos da memória rom
    private void saveData(String name, String data) throws IOException{
        File arq = new File(path,name);
        BufferedWriter buffWrite = new BufferedWriter(new FileWriter(arq,true));        
        buffWrite.append(data+"\n");
        buffWrite.close();
    }
    
    //Método que armazena as informações de uma lista da memoria Ram em arquivos da memória rom
    private void saveList(String name, LinkedList data) throws IOException{
        File arq = new File(path,name);
        BufferedWriter buffWrite = new BufferedWriter(new FileWriter(arq,false));
        saveData(name, this.coordinateX+":"+this.coordinateY);
//        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(arq)));			
//        oos.writeObject(data);
//        oos.close();        
        for(Object ob:data){
            buffWrite.append(ob.toString()+"\n");
        }
        buffWrite.close();
    }
    
    //Metodo que salva o log
    public void saveLog(String mensage) throws IOException{    
        String str = log.size()+"!"+mensage;
        log.add(str);
        saveData(fileNameLog, str);
    }
    public int getLogSize(){
        return log.size();
    }
    public String getLog (int last){
        String list = "";
        for(String s:log){
            String[] aux = s.split("!");
            int num = Integer.parseInt(aux[0]);
            if(num>last){
                list+=s;
            }
        }
        return list;
    }
    
    public boolean verifyLog(LinkedList<String> log) throws IOException{        
        int size = this.log.size();
            for(String s:log){
                String[] aux = s.split("!");
                if(size>Integer.parseInt(aux[0])){
                    log.remove(s);
                }else{
                    break;
                }
            }
        return becomeConsistent(log);
    }

    private boolean becomeConsistent(LinkedList<String> log) throws IOException {
        for(String str:log){
            String[] array = str.split("#");
            switch (array[0]) { //Testa o comando
                case "STORAGE":
                    switch(array[1]){
                        case "NEWITEM":
                            String[] aux = array[2].split(";");
                            addProduct(aux[0], aux[1], aux[2], aux[3], aux[4], 0, Double.parseDouble(aux[6]));
                        break;
                        case "UPDATEITEM":
                            aux = array[2].split(";");
                            updateProduct(aux[0], aux[1], aux[2], aux[3], aux[4], Double.parseDouble(aux[6]));
                        break;
                        case "DELETEITEM":
                            removeProduct(array[2].substring(0, array[2].charAt(';')));
                        break;
                    }
                break;                
            }
            saveLog(str);
        }
        return true;
    }
}