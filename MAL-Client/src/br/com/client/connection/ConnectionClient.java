/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.client.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

/**
 *
 * @author Antonio Raian
 */
public class ConnectionClient {
    private Socket server;
    private InetAddress addressDistributor;
    private InetAddress addressServer;
    private int portDistributor;
    private int portServer;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    public void setDistributor(String address, String port) throws UnknownHostException {
        this.addressDistributor = InetAddress.getByName(address);
        this.portDistributor = Integer.parseInt(port);
    }
    
    public void connectDistributor() throws IOException{
        server = new Socket(addressDistributor, portDistributor);
        out = new ObjectOutputStream(server.getOutputStream());
    }
    
    public void connectServer() throws IOException{
        server = new Socket(addressServer, portServer);
        out = new ObjectOutputStream(server.getOutputStream());
    }
    
    public void disconnect() throws IOException{
        in.close();
        out.close();
        server.close();
    }

    public String newConnection() throws UnknownHostException, IOException, ClassNotFoundException{
        connectDistributor();
        out.writeObject("CLIENT#CONNECT#");//Concatena as informações e as envia para o servidor
        in = new ObjectInputStream(server.getInputStream());//recebe informação advinda do servidor
        String s = (String) in.readObject();//"quebra" a informação do servidor
        disconnect();//Desconecta
        if(s!=null){
            String[] aux = s.split(";");
            addressServer = InetAddress.getByName(aux[0]);
            portServer = Integer.parseInt(aux[1]);
            return "SUCCESS";
        }else{
            return "FALHA";
        }
    }

    public String register(String name, String login, String passwd) throws IOException, ClassNotFoundException {
        connectServer();
        out.writeObject("CLIENT#REGISTER#"+name+";"+login+";"+passwd);
        in = new ObjectInputStream(server.getInputStream());//recebe informação advinda do servidor
        String s = (String) in.readObject();
        if(s!=null){
            return "SUCCESS";
        }else{
            return "FALHA";
        }
    }  
    
    public String login(String login, String passworld) throws IOException, ClassNotFoundException {
        connectServer();
        out.writeObject("CLIENT#LOGIN#"+login+";"+passworld);//Concatena as informações e as envia para o servidor
        in = new ObjectInputStream(server.getInputStream());//recebe informação advinda do servidor
        String s = (String) in.readObject();//"quebra" a informação do servidor
        disconnect();//Desconecta
        if(s!=null){
            return "SUCCESS";
        }else{
            return "FALHA";
        }
    }
    
    public LinkedList<String> getProducts() throws IOException, ClassNotFoundException {
        connectServer();
        out.writeObject("CLIENT#GETPRODUCTS#");
        in = new ObjectInputStream(server.getInputStream());//recebe informação advinda do servidor
        LinkedList s = (LinkedList) in.readObject();//"quebra" a informação do servidor
        disconnect();//Desconecta
        return s;
    }  
}