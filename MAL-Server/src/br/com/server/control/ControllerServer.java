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
    private LinkedList<Store> storages;//Lista de depósitos
    private LinkedList<Product> products;//Lista de produtos
    private LinkedList<User> users;//Lista de usuários
    private LinkedList<String> log;//Lista de log do servidor
    private File path = new File("ServerFiles");//Diretório dos arquivos
    private String fileNameLog = "LOG_SERVER.txt";//Nome do arquivo de log

    //Construtor inicia todas as listas
    public ControllerServer() throws IOException {
        this.storages = new LinkedList<>();
        this.products = new LinkedList<>();
        this.users = new LinkedList<>();
        this.log = new LinkedList<>();
        initLists();
    }

    //Transforma a lista de produtos em um array de string e retorna
    public String[] getProducts() {
        String list[] = new String[products.size()];
        int i=0;
        for(Product p:products){
            list[i] = p.toString();
            i++;
        }
        return list;
    }

    //Adiciona um novo produto, na lista de produtos
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

    //Atualiza as informações de um produto
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

    //Remove um produto da lista
    public String deleteProduct(String code, String address) {
        Product prod = findProduct(code);
        if(products.remove(prod)){
            return "SUCCESS";
        }
        return "FAIL";
    }
    
    //------------------------------------------------------------
    //Encontra um produto na lista de produtos
    private Product findProduct(String cod) {
        for(Product p:products){
            if(p.getCod().equals(cod)){
                return p;
            }
        }
        return null;
    }
    //Encontra um deposito na lista de depositos
    private Store findStore(String address){
        for(Store s:storages){
            if(s.getAddress().equals(address)){
                return s;
            }
        }
        return null;
    }
    //Encontra um produto na lista de produtos de um deposito
    private Product findPoductStore(Store store, String cod){
        for(Product p:store.getProducts()){
            if(p.getCod().equals(cod))
                return p;
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
                    Product p = findProduct(aux[0]);
                    if(p==null){
                        p = new Product(aux[0], aux[1], aux[2], aux[3], aux[4], Integer.parseInt(aux[5]), Double.parseDouble(aux[6]));
                        list.add(p);
                        products.add(p);
                    }else{
                        p.addQuantiy(Integer.parseInt(aux[5]));
                    }                    
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
        if(!arq.exists()){
            arq.createNewFile();
        }
        BufferedWriter buffWrite = new BufferedWriter(new FileWriter(arq,true));        
        buffWrite.append(data+"\n");
        buffWrite.close();
    }
    
    //Método que armazena as informações de uma lista da memoria Ram em arquivos da memória rom
    private void saveList(String name, LinkedList data) throws IOException{
        File arq = new File(path,name);
        if(!arq.exists()){
            arq.createNewFile();
        }
        BufferedWriter buffWrite = new BufferedWriter(new FileWriter(arq,false));
        //saveData(name, this.coordinateX+":"+this.coordinateY);        
        for(Object ob:data){
            buffWrite.append(ob.toString()+"\n");
        }
        buffWrite.close();
    }
    
    //metodo que ler os arquivos e armazena nas respectivas listas
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
    //metodo que retorna o tamanho do log
    public int getLogSize(){
        return log.size();
    }
    //metodo que retorna o log como string
    public String getLog (int last){
        String list = "";
        for(String s:log){
            if(log.size()>last){
                list+=s;
            }
        }
        return list;
    }
    //metodo que varre o log removendo os que já existem no log atual
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

    //metodo que faz a consistencia dos dados de acordo com o log recebido
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
    //Metodo que faz a adição de um usuário
    public String registerUser(String userName, String login, String passwd) throws IOException {
        User user = findUser(login);
        if(user==null){
            user = new User(userName, login, passwd);
            users.add(user);
            saveData("Users.txt", user.toString());
            return user.toString();
        }
        return null;
    }

    //metodo de autenticação de usuário
    public String loginUser(String login, String passwd) {
        User user = findUser(login);
        if(user!=null&&user.getPassworld().equals(passwd)){
            return user.toString();
        }
        return null;
    }
    //metodo que encontra um usuário na lista de usuários
    private User findUser(String login){
        for(User u:users){
            if(u.getLogin().equals(login)){
                return u;
            }
        }
        return null;
    }

    //Metodo que remove a quantidade comprada por um usuário
    public String buy(String user, String[] prods, String value) {
        int i =0;
        for(String s:prods){
            String aux[] = s.split(";");
            Product p = findProduct(aux[0]);            
            if(p!=null&&aux[0].equals(p.getCod())){
                p.removeQuantiy(Integer.parseInt(aux[5]));
                i++;
            }
        }
        if(i>0)
            return "SUCCESS";
        return "FAIL";
    }
    //metodo que calcula o frete para a compra de acordo com a distancia
    public String calculateTranspotation(double value, double x, double y){
        double minor = 0;
        for(Store store:storages){
            double minoraux = Math.sqrt(Math.pow(store.getCoordinateX()-x,2)+Math.pow(store.getCoordinateY()-y,2));
            if(minor<minoraux){
                minor = minoraux;
            }
        }
        double freteValue = (minor*0.50)+(value*10/100)+value;
        return freteValue+"";
    }

    //Metodo que persiste todos os arquivos
    public void saveAll() throws IOException {
        saveList("ProductsServer.txt", products);
    }
}
