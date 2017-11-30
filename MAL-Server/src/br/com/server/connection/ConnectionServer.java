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
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ConnectionServer(){
        
    }
    
    public void setInfos(String address, String port) throws UnknownHostException {
        this.addressDistributor = InetAddress.getByName(address);
        this.portDistributor = Integer.parseInt(port);
    }
    
    public void connectDistributor() throws IOException{
        server = new Socket(addressDistributor, portDistributor);
        out = new ObjectOutputStream(server.getOutputStream());
    }
    
    public void connectStorages() throws IOException{
        server = new Socket(addressStorages, portStorages);
        out = new ObjectOutputStream(server.getOutputStream());
    }
    
    public void connectServers() throws IOException{
        server = new Socket(addressServers, portServers);
        out = new ObjectOutputStream(server.getOutputStream());
    }
    
    public void disconnect() throws IOException{
        in.close();
        out.close();
        server.close();
    }

    public String newServerConnection(String address, String port) throws UnknownHostException, IOException, ClassNotFoundException{
        connectDistributor();
        out.writeObject("SERVER#CONNECT#"+address+";"+port);//Concatena as informações e as envia para o servidor
        in = new ObjectInputStream(server.getInputStream());//recebe informação advinda do servidor
        String s = (String) in.readObject();//"quebra" a informação do servidor
        if(s!=null){
            String[] aux = s.split(";");
            addressServers = InetAddress.getByName(aux[0]);
            portServers = Integer.parseInt(aux[1]);
            s=getStorages();
            return "SUCCESS";
        }
        disconnect();//Desconecta
        return "FAIL";
    }

    public String getStorages() throws IOException, ClassNotFoundException{
        connectDistributor();
        in = new ObjectInputStream(server.getInputStream());//recebe informação advinda do servidor
        out.writeObject("SERVER#GETSTORAGES#");
        String s = (String) in.readObject();
        if(s!=null){
            String aux[] = s.split(";");
            addressStorages = InetAddress.getByName(aux[0]);
            portStorages = Integer.parseInt(aux[1]);
            return "SUCCESS";
        }
        return s;
    }
    
    public void serverDisconnect(String address, String port) throws IOException, ClassNotFoundException {
        connectDistributor();
        out.writeObject("SERVER#DISCONNECT#"+address+";"+port);//Concatena as informações e as envia para o servidor
        in = new ObjectInputStream(server.getInputStream());//recebe informação advinda do servidor
        String s = (String) in.readObject();//"quebra" a informação do servidor
        disconnect();//Desconecta
    }
    
    public String getLog(int logSize) throws IOException, ClassNotFoundException {
        connectServers();
        out.writeObject("SERVER#GETLOG#"+logSize);
        in = new ObjectInputStream(server.getInputStream());//recebe informação advinda do servidor
        String s = (String) in.readObject();//"quebra" a informação do servidor
        disconnect();//Desconecta
        if(s!=null)
            return "SUCCESS";
        return "FAIL";
    }
}
