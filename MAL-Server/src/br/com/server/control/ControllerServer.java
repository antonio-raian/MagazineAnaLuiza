/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.server.control;

import br.com.server.model.Product;
import br.com.server.model.Store;
import br.com.server.model.User;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author Antonio Raian
 */
public class ControllerServer {
    private LinkedList<Store> storages;
    private LinkedList<Product> products;
    private LinkedList<User> users;
    private LinkedList<String> log;
    private File path = new File("ServerFiles");
    private String fileName, fileNameLog;

    public ControllerServer(String number) throws IOException {
        this.storages = new LinkedList<>();
        this.products = new LinkedList<>();
        this.users = new LinkedList<>();
        this.log = new LinkedList<>();
        initLists();
    }

    public LinkedList getProducts() {
        return products;
    }

    public String newProduct(String cod, String name, String details, String producer, String kind, String quantiy, String value, String address){
        Product prod = findProduct(cod);
        if(prod==null){
            prod = new Product(cod, name, details, producer, kind, Integer.parseInt(quantiy), Double.parseDouble(value));
            products.add(prod);
            return "SUCCESS";
        }else{
            updateProduct(cod, name, details, producer, kind, quantiy, value, address);
            return "SUCCESS";
        }
    }

    public String updateProduct(String cod, String name, String details, String producer, String kind, String quantiy, String value, String address) {
        Product prod = findProduct(cod);
        if(prod!=null){
            prod.setName(name);
            prod.setDetails(details);
            prod.setProducer(producer);
            prod.setKind(kind);
            prod.setQuantity(Integer.parseInt(quantiy));
            prod.setValue(Double.parseDouble(value));
            return "SUCCESS";
        }
        return "FAIL";
    }

    public String deleteProduct(String code, String address) {
        Product prod = findProduct(code);
        if(products.remove(prod)){
            return "SUCCESS";
        }
        return "FAIL";
    }
    
    //------------------------------------------------------------
    private Product findProduct(String cod) {
        for(Product p:products){
            if(p.getCod().equals(cod)){
                return p;
            }
        }
        return null;
    }
    
    private Store findStore(String address){
        for(Store s:storages){
            if(s.getAddress().equals(address)){
                return s;
            }
        }
        return null;
    }
    //Metodo que lê as informação dos arquivos da ROM para a RAM
    private LinkedList readFile(File arq, int typeFile) throws FileNotFoundException, IOException {
        Store store = new Store();
        LinkedList list = new LinkedList();
        BufferedReader buffRead = new BufferedReader(new FileReader(arq));
        String line = buffRead.readLine();
        int i = 0;
        if(typeFile==0){
            while (line!=null) {
                if(i==0){
                    String s[] = line.split(":");
                    store.setAddress(s[0]);
                    store.setCoordinateX(Integer.parseInt(s[1]));
                    store.setCoordinateY(Integer.parseInt(s[2]));
                }else{
                    String[] aux = line.split(";");
                    Product p = new Product(aux[0], aux[1], aux[2], aux[3], aux[4], Integer.parseInt(aux[5]), Double.parseDouble(aux[6]));
                    list.add(p);
                }
                line = buffRead.readLine();
                i++;
            }
            store.setProducts(list);
            storages.add(store);
        }else if(typeFile==1){
            while (line!=null) {
                list.add(line);
                
                line = buffRead.readLine();
                i++;
            }
        }else if(typeFile==2){
            User user = new User();
            while (line!=null) {
                String[] aux = line.split(";");
                user.setName(aux[0]);
                user.setLogin(aux[1]);
                user.setPassworld(aux[2]);
                list.add(user);
                line = buffRead.readLine();
                i++;
            }
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
        //saveData(name, this.coordinateX+":"+this.coordinateY);        
        for(Object ob:data){
            buffWrite.append(ob.toString()+"\n");
        }
        buffWrite.close();
    }
    
    private void initLists() throws IOException{
        LinkedList list;
        path.mkdir();
        File[] files = path.listFiles();
        for(File f:files){
            if(f.getName().contains("Products")){
                list = readFile(f, 0);
                for(Object o:list){
                    if(o instanceof Product){
                        Product p = (Product) o;
                        Product prod = findProduct(p.getCod());
                        prod.addQuantiy(p.getQuantiy());
                    }
                }
            }else if(f.getName().contains("LOG")){
                log = readFile(f, 1);
                
            }else if (f.getName().contains("Users")){
                users = readFile(f, 2);
            }
        }
    }

    //Metodo que salva o log
    public void saveLog(String mensage) throws IOException{    
        String str = (log.size()+1)+"!"+mensage+"§";
        log.add(str);
        saveData(fileNameLog, str);
    }
    public int getLogSize(){
        return log.size();
    }
    public String getLog (int last){
        String list = "";
        for(String s:log){
            if(log.size()>last){
                list+=s;
            }
        }
        return list;
    }
    
    public boolean verifyLog(String log) throws IOException{        
        LinkedList<String> list = new LinkedList<>();
        if(!log.equals("")){
            String[] aux = log.split("§");
            int size = this.log.size();
                for(int i=0;i<aux.length-1;i++){
                    String[] aux2 = aux[i].split("!");
                    if(size<Integer.parseInt(aux2[0])){
                        list.add(aux2[1]);
                    }
                }
            return becomeConsistent(list);
        }
        return false;
    }

    private boolean becomeConsistent(LinkedList<String> log) throws IOException {
        for(String str:log){
            String[] array = str.split("#");
            switch (array[0]) { //Testa o comando
                case "STORAGE":                    
                    String[] aux = array[2].split(";");
                    switch (array[1]){
                        case "NEWITEM":
                            newProduct(aux[0],aux[1],aux[2],aux[3],aux[4],aux[5],aux[6], array[3]);
                        break;
                        case "UPDATEITEM":
                            updateProduct(aux[0],aux[1],aux[2],aux[3],aux[4],aux[5],aux[6], array[3]);
                        break;
                        case "DELETEITEM":
                            deleteProduct(array[2],array[3]);
                    }
                break;
                case "CLIENT":
                    switch (array[1]){
                        case "GETPRODUCTS":
                            getProducts();
                        break;
                    }
                break;
            }
            saveLog(str);
        }
        return true;
    }

    //Módulo de usuário-----------------------
    public String registerUser(String userName, String login, String passwd) {
        User user = findUser(login);
        if(user==null){
            user = new User(userName, login, passwd);
            users.add(user);
            return user.toString();
        }
        return null;
    }

    public String loginUser(String login, String passwd) {
        User user = findUser(login);
        if(user.getPassworld().equals(passwd)){
            return user.toString();
        }
        return null;
    }
    
    private User findUser(String login){
        for(User u:users){
            if(u.getLogin().equals(login)){
                return u;
            }
        }
        return null;
    }
}
