/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.server.connection;

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
public class ConnectionServer {
    private Socket server;
    private InetAddress addressDistributor;
    private InetAddress addressStorages;
    private InetAddress addressServers;
    private int portDistributor;
    private int portStorages;
    private int portServers;
    private ObjectOutputStream saida;
    private ObjectInputStream entrada;

    public ConnectionServer(){
        
    }
    
    public void setInfos(String address, String port) throws UnknownHostException {
        this.addressDistributor = InetAddress.getByName(address);
        this.portDistributor = Integer.parseInt(port);
    }
    
    public void connectTCP() throws IOException{
        server = new Socket(addressDistributor, portDistributor);
        saida = new ObjectOutputStream(server.getOutputStream());
    }
    
    public void connectStorages() throws IOException{
        server = new Socket(addressStorages, portStorages);
        saida = new ObjectOutputStream(server.getOutputStream());
    }
    
    public void connectServers() throws IOException{
        server = new Socket(addressServers, portServers);
        saida = new ObjectOutputStream(server.getOutputStream());
    }
    
    public void disconnect() throws IOException{
        entrada.close();
        saida.close();
        server.close();
    }

    public String newServerConnection(String address, String port) throws UnknownHostException, IOException, ClassNotFoundException{
        connectTCP();
        saida.writeObject("SERVER#CONNECT#"+address+";"+port);//Concatena as informações e as envia para o servidor
        entrada = new ObjectInputStream(server.getInputStream());//recebe informação advinda do servidor
        String s = (String) entrada.readObject();//"quebra" a informação do servidor
        if(s!=null){
            String[] aux = s.split(";");
            addressServers = InetAddress.getByName(aux[0]);
            portServers = Integer.parseInt(aux[1]);
            saida.writeObject("SERVER#GETSTORAGES#");
            s = (String) entrada.readObject();
            if(s!=null){
                aux = s.split(";");
                addressStorages = InetAddress.getByName(aux[0]);
                portStorages = Integer.parseInt(aux[1]);
                return "SUCCESS";
            }
        }
        disconnect();//Desconecta
        return "FAIL";
    }

    public void serverDisconnect(String address, String port) throws IOException, ClassNotFoundException {
        connectTCP();
        saida.writeObject("SERVER#DISCONNECT#"+address+";"+port);//Concatena as informações e as envia para o servidor
        entrada = new ObjectInputStream(server.getInputStream());//recebe informação advinda do servidor
        String s = (String) entrada.readObject();//"quebra" a informação do servidor
        disconnect();//Desconecta
    }

    public LinkedList getProducts() throws IOException, ClassNotFoundException{
        connectStorages();
        saida.writeObject("SERVER#GETPRODUCTS#");
        entrada = new ObjectInputStream(server.getInputStream());//recebe informação advinda do servidor
        LinkedList list = (LinkedList) entrada.readObject();//"quebra" a informação do servidor
        disconnect();//Desconecta
        return list;
    }
}
