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
import java.net.UnknownHostException;

/**
 *
 * @author Antonio Raian
 */
public class Connection {
    private final int serversPort = 8090;
    private final String serversGroup = "235.2.2.2";
    private MulticastSocket serversSocket;
    private final int storagePort = 8080;
    private final String storageGroup = "235.1.1.1";
    private MulticastSocket storageSocket;
    
    private byte[] receive = new byte[2048];

    public Connection() throws IOException {
        serversSocket = new MulticastSocket();
        storageSocket = new MulticastSocket(storagePort);
        storageSocket.joinGroup(InetAddress.getByName(storageGroup));
    }
    
    public void newItem(String item) throws IOException{
        String s = "STORAGE#NEWITEM#"+item;
        DatagramPacket sendDP = new DatagramPacket(s.getBytes(), s.getBytes().length, InetAddress.getByName(serversGroup), serversPort);
        serversSocket.send(sendDP);
        sendDP.setAddress(InetAddress.getByName(storageGroup));
        sendDP.setPort(storagePort);
        storageSocket.send(sendDP);
    }

    public void updateItem(String item) throws IOException {
        String s = "STORAGE#UPDATEITEM#"+item;
        DatagramPacket sendDP = new DatagramPacket(s.getBytes(), s.getBytes().length, InetAddress.getByName(serversGroup), serversPort);
        serversSocket.send(sendDP);
        sendDP.setAddress(InetAddress.getByName(storageGroup));
        sendDP.setPort(storagePort);
        storageSocket.send(sendDP);
    }

    public void deleteItem(String cod) throws IOException {
        String s = "STORAGE#DELETEITEM#"+cod+";";
        DatagramPacket sendDP = new DatagramPacket(s.getBytes(), s.getBytes().length, InetAddress.getByName(serversGroup), serversPort);
        serversSocket.send(sendDP);
        sendDP.setAddress(InetAddress.getByName(storageGroup));
        sendDP.setPort(storagePort);
        storageSocket.send(sendDP);
    }

    public String getLog(int logSize) throws IOException {
        String s = "STORAGE#GETLOG#"+logSize;
        DatagramPacket sendDP = new DatagramPacket(s.getBytes(), s.getBytes().length, InetAddress.getByName(storageGroup), storagePort);
        serversSocket.send(sendDP);
        DatagramPacket receiveDP = new DatagramPacket(this.receive, this.receive.length);
        serversSocket.receive(receiveDP);
        System.out.println("recebeu");
        return new String(receiveDP.getData());
    }
}