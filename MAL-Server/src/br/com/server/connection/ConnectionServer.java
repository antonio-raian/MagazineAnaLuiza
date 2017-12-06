/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.server.connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 *
 * @author Antonio Raian
 */
public class ConnectionServer {
    private DatagramSocket serverUDP; //Objeto responsável pela conexão com o servidor
    private InetAddress addressDistributor;
    private InetAddress addressStorages;
    private InetAddress addressServers;
    private String address;
    private int portDistributor;
    private int portStorages;
    private int portServers;
    private byte[] outUDP; //Objeto que envia informação para o servidor
    private final byte[] inUDP = new byte[2048]; //Objeto que recebe informação do servidor

    public ConnectionServer() throws UnknownHostException{
        address = InetAddress.getLocalHost().getHostAddress();
    }
    
    public void setInfos(String address, String port) throws UnknownHostException {
        this.addressDistributor = InetAddress.getByName(address);
        this.portDistributor = Integer.parseInt(port);
    }
    
    public void connect() throws IOException{
        serverUDP = new DatagramSocket();
    }

    public String newServerConnection(String address, String port) throws UnknownHostException, IOException, ClassNotFoundException{
        connect();
        //Concatena todas as infomações numa String e a transforma em uma cadeia de bytes
        outUDP = ("SERVER#CONNECT#"+address+";"+port).getBytes();//Concatena as informações e as envia para o servidor
        //Cria um pacote para envio ao servidor
        DatagramPacket sendInfo = new DatagramPacket(outUDP, outUDP.length, addressDistributor, portDistributor);
        serverUDP.send(sendInfo);//Envia o pacote ao servidor
        sendInfo = new DatagramPacket(inUDP, inUDP.length);
        serverUDP.receive(sendInfo);//Espera uma resposta
        String s = new String(sendInfo.getData());//"quebra" a informação do servidor
        if(s!=null){
            String[] aux = s.split(";");
            addressServers = InetAddress.getByName(aux[0]);
            String x = aux[1];
            System.out.println("/"+x+"/");
            portServers = Integer.parseInt(x);
            return "SUCCESS";
        }
        return "FAIL";
    }

    public String getStorages() throws IOException, ClassNotFoundException{
        connect();//Concatena todas as infomações numa String e a transforma em uma cadeia de bytes
        outUDP = ("SERVER#GETSTORAGES#"+address).getBytes();//Concatena as informações e as envia para o servidor
        //Cria um pacote para envio ao servidor
        DatagramPacket sendInfo = new DatagramPacket(outUDP, outUDP.length, addressDistributor, portDistributor);
        serverUDP.send(sendInfo);//Envia o pacote ao servidor
        sendInfo = new DatagramPacket(inUDP, inUDP.length);
        serverUDP.receive(sendInfo);//Espera uma resposta
        
        String s = new String(sendInfo.getData());//"quebra" a informação do servidor
        if(s!=null){
            String aux[] = s.split(";");
            addressStorages = InetAddress.getByName(aux[0]);
            portStorages = Integer.parseInt(aux[1]);
            return "SUCCESS";
        }
        return s;
    }

    public void registerClient(String user, String login, String passwd) throws IOException {
        connect();//Concatena todas as infomações numa String e a transforma em uma cadeia de bytes
        outUDP = ("SERVER#REGISTER#"+user+";"+login+";"+passwd).getBytes();//Concatena as informações e as envia para o servidor
        //Cria um pacote para envio ao servidor
        DatagramPacket sendInfo = new DatagramPacket(outUDP, outUDP.length, addressServers, portServers);
        serverUDP.send(sendInfo);//Envia o pacote ao servidor
    }
    
    public void serverDisconnect(String address, String port) throws IOException, ClassNotFoundException {
        connect();
        outUDP = ("SERVER#DISCONNECT#"+address+";"+port).getBytes();//Concatena as informações e as envia para o servidor
        //Cria um pacote para envio ao servidor
        DatagramPacket sendInfo = new DatagramPacket(outUDP, outUDP.length, addressDistributor, portDistributor);
        serverUDP.send(sendInfo);//Envia o pacote ao servidor
        sendInfo = new DatagramPacket(inUDP, inUDP.length);
        serverUDP.receive(sendInfo);//Espera uma resposta
        
        String s = new String(sendInfo.getData());//"quebra" a informação do servidor
    }
    
    public String getLog(int logSize) throws IOException, ClassNotFoundException {
        connect();
        String s = ("SERVER#GETLOG#"+logSize+"#");//Concatena as informações e as envia para o servidor
        //Cria um pacote para envio ao servidor
        DatagramPacket sendDP = new DatagramPacket(s.getBytes(), s.getBytes().length, addressServers, portServers);
        serverUDP.send(sendDP);
        DatagramPacket receiveDP = new DatagramPacket(this.inUDP, this.inUDP.length);
        serverUDP.setSoTimeout(3000);
        int i =0;
        s = null;
        while(s==null && i<2){
            try{            
                serverUDP.receive(receiveDP);
                s = new String(receiveDP.getData());
                String aux[] = s.split("#");
                if(aux[3].equals(address)){
                    s=null;
                }
            }catch (SocketException | SocketTimeoutException ex){                
            }
            i++;
        }
        if(s==null){
            return "Socket TimeOut";
        }
        System.out.println("recebeu");
        return s;
    }

    public String sale(String[] prods) {
        //Efetua a venda no depósito
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
