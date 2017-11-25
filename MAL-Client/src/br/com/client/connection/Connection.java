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
public class Connection {
    private Socket server;
    private InetAddress addressDistributor;
    private InetAddress addressServer;
    private int portDistributor;
    private int portServer;
    private ObjectOutputStream saida;
    private ObjectInputStream entrada;

    public Connection(){
        
    }
    
    public void setDistributor(String address, String port) throws UnknownHostException {
        this.addressDistributor = InetAddress.getByName(address);
        this.portDistributor = Integer.parseInt(port);
    }
    
    public void setServer(String address, String port) throws UnknownHostException {
        this.addressServer = InetAddress.getByName(address);
        this.portServer = Integer.parseInt(port);
    }
    
    public void connect() throws IOException{
        server = new Socket(addressDistributor, portDistributor);
        saida = new ObjectOutputStream(server.getOutputStream());
    }
    public void disconnect() throws IOException{
        entrada.close();
        saida.close();
        server.close();
    }

    public String newConnection() throws UnknownHostException, IOException, ClassNotFoundException{
        connect();
        saida.writeObject("CLIENT#CONNECT#");//Concatena as informações e as envia para o servidor
        entrada = new ObjectInputStream(server.getInputStream());//recebe informação advinda do servidor
        String s = (String) entrada.readObject();//"quebra" a informação do servidor
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
    
    public LinkedList<String> getProducts() throws IOException, ClassNotFoundException {
        connect();
        saida.writeObject("CLIENT#GETPRODUCTS#");
        entrada = new ObjectInputStream(server.getInputStream());//recebe informação advinda do servidor
        LinkedList s = (LinkedList) entrada.readObject();//"quebra" a informação do servidor
        disconnect();//Desconecta
        return s;
    }

    public String findByCod(String code) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String register(String text, String text0, String text1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}