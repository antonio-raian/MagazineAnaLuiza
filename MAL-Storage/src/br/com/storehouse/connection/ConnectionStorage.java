/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.storehouse.connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 *
 * @author Antonio Raian
 */
public class ConnectionStorage {
    private String address;
    private final int serversPort = 8090;
    private final String serversGroup = "235.2.2.2";
    private MulticastSocket serversSocket;
    private final int storagePort = 8080;
    private final String storageGroup = "235.1.1.1";
    private MulticastSocket storageSocket;
    
    private byte[] receive = new byte[2048];

    public ConnectionStorage() throws IOException {
        address = InetAddress.getLocalHost().getHostAddress();
        serversSocket = new MulticastSocket();
        storageSocket = new MulticastSocket(storagePort);
        storageSocket.joinGroup(InetAddress.getByName(storageGroup));
    }
    
    public void newItem(String item) throws IOException{
        String s = "STORAGE#NEWITEM#"+item+"#"+address;
        DatagramPacket sendDP = new DatagramPacket(s.getBytes(), s.getBytes().length, InetAddress.getByName(serversGroup), serversPort);
        serversSocket.send(sendDP);
        sendDP.setAddress(InetAddress.getByName(storageGroup));
        sendDP.setPort(storagePort);
        storageSocket.send(sendDP);
    }

    public void updateItem(String item) throws IOException {
        String s = "STORAGE#UPDATEITEM#"+item+"#"+address;
        DatagramPacket sendDP = new DatagramPacket(s.getBytes(), s.getBytes().length, InetAddress.getByName(serversGroup), serversPort);
        serversSocket.send(sendDP);
        sendDP.setAddress(InetAddress.getByName(storageGroup));
        sendDP.setPort(storagePort);
        storageSocket.send(sendDP);
    }

    public void deleteItem(String cod) throws IOException {
        String s = "STORAGE#DELETEITEM#"+cod+"#"+address;
        DatagramPacket sendDP = new DatagramPacket(s.getBytes(), s.getBytes().length, InetAddress.getByName(serversGroup), serversPort);
        serversSocket.send(sendDP);
        sendDP.setAddress(InetAddress.getByName(storageGroup));
        sendDP.setPort(storagePort);
        storageSocket.send(sendDP);
    }

    public String getLog(int logSize) throws IOException {
        String s = "STORAGE#GETLOG#"+logSize+"#"+address+"#";
        DatagramPacket sendDP = new DatagramPacket(s.getBytes(), s.getBytes().length, InetAddress.getByName(storageGroup), storagePort);
        storageSocket.send(sendDP);
        DatagramPacket receiveDP = new DatagramPacket(this.receive, this.receive.length);
        storageSocket.setSoTimeout(3000);
        int i =0;
        s = null;
        while(s==null && i<3){
            try{            
                storageSocket.receive(receiveDP);
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
}