/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.server.connection;

import br.com.server.control.ControllerServer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Antonio Raian
 */
//Classe (thread) responsável por todas as ações do servior, responsável por decifrar informações vindas dos clientes
public class ActionsServer extends Thread{
    private Socket clientTCP;//Variavel responsável por receber a conexão do servidor TCP
    private DatagramSocket clientUDP;//Variavel responsável por receber a conexão do servidor UDP
    private ObjectInputStream in;//Objetos que armazena informações advindas do cliente
    private ObjectOutputStream out; // Objeto usado para enviar mensagens aos clientes TCP
    private DatagramPacket inUDP; //Objeto usado para enviar mensagens aos clientes UDP;
    private byte[] outUDP;
    private final ControllerServer ctrl;//Nosso objeto que contem as listas e informações salvas do sistema
    private final ConnectionServer conncetion;
    private final String str; //String usada pra receber as requisições dos clientes
    
    //Construtor que permite conexão TCP
    public ActionsServer(Socket socket, ControllerServer ctrl, ConnectionServer connection) throws IOException, ClassNotFoundException {
        clientTCP = socket;//Recebe a conexão
        this.ctrl = ctrl;//Seta o objeto que contem as informações do Sistema
        this.conncetion = connection;
        in = new ObjectInputStream(clientTCP.getInputStream());//Decifra as informações vindas do cliente
        System.out.println("Cliente TCP: "+clientTCP.getInetAddress()+":"+clientTCP.getPort());
        str = (String) in.readObject();//Transforma o objeto passado em String
        System.out.println("Recebido: "+str);
    }
    //Construtor que permite conexão UDP
    public ActionsServer(DatagramSocket socket, DatagramPacket packet, ControllerServer ctrl, ConnectionServer connection) throws IOException{
        this.ctrl = ctrl;//Seta o objeto que contem as informações do Sistema
        this.conncetion = connection;
        this.inUDP = packet; //Recebe o pacote enviado ao servidor
        clientUDP = socket;//Recebe o meio de comunicação com o cliente
        
        System.out.println("Conectou");
        
        System.out.println("Cliente UDP: "+inUDP.getAddress()+":"+inUDP.getPort());
        str = new String(inUDP.getData(),0,inUDP.getLength());//Transforma o objeto passado em String
        System.out.println("Recebido: "+str);
    }
    
    //Metodo responsável pelas atividades da Thread
    @Override
    public void run(){
        String[] array = str.split("#");
        try{
            switch (array[0]) { //Testa o comando
                case "STORAGE":
                    switch (array[1]){
                        case "NEWITEM":
                            newProduct(array[2],array[3]);
                        break;
                        case "UPDATEITEM":
                            updateProduct(array[2],array[3]);
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
                        case "REGISTER":
                            registerUserTCP(array[2]);
                        break;
                        case "LOGIN":
                            login(array[2]);
                        break;
                        case "CALCTRANSP":
                            calculateTransportation(array[2]);
                        break;
                        case "BUY":
                            buy(array[2]);
                        break;
                    }
                break;
                case "SERVER":
                    switch (array[1]){
                        case "GETLOG":
                            getLog(array[2]);
                        break;
                        case "REGISTER":
                            registerUserUDP(array[2]);
                        break;
                    }
                break;
            }
        } catch (IOException ex) {
            Logger.getLogger(ActionsServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ActionsServer.currentThread().interrupt();
    }

    private void getProducts() throws IOException {
        String[] list = ctrl.getProducts();
        out = new ObjectOutputStream(clientTCP.getOutputStream());
        out.writeObject(list);
        out.flush();
        out.close();        
        ctrl.saveLog(str);
    }

    private void newProduct(String data, String address) throws IOException {
        String[] aux = data.split(";");
        String s = ctrl.newProduct(aux[0],aux[1],aux[2],aux[3],aux[4],aux[5],aux[6],address);
        out = new ObjectOutputStream(clientTCP.getOutputStream());
        out.writeObject(s);
        ctrl.saveLog(str);
        out.flush();
        out.close();
    }

    private void updateProduct(String data, String address) throws IOException {
        String[] aux = data.split(";");
        String s = ctrl.updateProduct(aux[0],aux[1],aux[2],aux[3],aux[4],aux[5],aux[6],address);
        out = new ObjectOutputStream(clientTCP.getOutputStream());
        out.writeObject(s);
        ctrl.saveLog(str);
        out.flush();
        out.close();
    }

    private void deleteProduct(String code, String address) throws IOException {
        String s = ctrl.deleteProduct(code, address);
        out = new ObjectOutputStream(clientTCP.getOutputStream());
        out.writeObject(s);
        ctrl.saveLog(str);
        out.flush();
        out.close();
    }

    private void registerUserTCP(String data) throws IOException {
        String aux[] = data.split(";");
        String s = ctrl.registerUser(aux[0],aux[1],aux[2]);
        out = new ObjectOutputStream(clientTCP.getOutputStream());
        conncetion.registerClient(aux[0], aux[1], aux[2]);
        out.writeObject(s);
        ctrl.saveLog(str);
        out.flush();
        out.close();
    }

    private void registerUserUDP(String data) throws IOException {
        String aux[] = data.split(";");
        ctrl.registerUser(aux[0],aux[1],aux[2]);
    }

    private void login(String data) throws IOException {
        String aux[] = data.split(";");
        String s = ctrl.loginUser(aux[0],aux[1]);
        out = new ObjectOutputStream(clientTCP.getOutputStream());
        out.writeObject(s);
        ctrl.saveLog(str);
        out.flush();
        out.close();
    }

    private void getLog(String last) throws IOException {
        String s = ctrl.getLog(Integer.parseInt(last));
        out = new ObjectOutputStream(clientTCP.getOutputStream());
        out.writeObject(s);
        ctrl.saveLog(str);
        out.flush();
        out.close();
    }

    private void buy(String data) throws IOException {
        out = new ObjectOutputStream(clientTCP.getOutputStream());
        String datas[] = data.split("@");
        String aux = datas[1].substring(1,datas[1].length()-1);
        String prods[] = aux.split(",");
        String s = ctrl.buy(datas[0], prods, datas[2]);
        out.writeObject(s);
        ctrl.saveLog(str);
        out.flush();
        out.close();
    }

    private void calculateTransportation(String data) throws IOException {
        out = new ObjectOutputStream(clientTCP.getOutputStream());
        String aux[] = data.split("@");
        String s = ctrl.calculateTranspotation(Double.parseDouble(aux[0]), Double.parseDouble(aux[1]), Double.parseDouble(aux[2]));
        out.writeObject(s);
        ctrl.saveLog(str);
        out.flush();
        out.close();
    }
}