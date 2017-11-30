/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.distributor.connection;

import br.com.distributor.control.ControllerDistributor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

/**
 *
 * @author Antonio Raian
 */
//Classe (thread) responsável por todas as ações do servior, responsável por decifrar informações vindas dos clientes
public class ActionsDistributor extends Thread{
    private Socket clientTCP;//Variavel responsável por receber a conexão do servidor TCP
    private DatagramSocket clientUDP;//Variavel responsável por receber a conexão do servidor UDP
    private ObjectInputStream inputTCP;//Objetos que armazena informações advindas do cliente
    private ObjectOutputStream outputTCP; // Objeto usado para enviar mensagens aos clientes TCP
    private DatagramPacket inputUDP; //Objeto usado para enviar mensagens aos clientes UDP;
    private byte[] outputUDP;
    private final ControllerDistributor ctrl;//Nosso objeto que contem as listas e informações salvas do sistema
    private final String str; //String usada pra receber as requisições dos clientes
    
    //Construtor que permite conexão TCP
    public ActionsDistributor(Socket socket, ControllerDistributor ctrl) throws IOException, ClassNotFoundException {
        clientTCP = socket;//Recebe a conexão
        this.ctrl = ctrl;//Seta o objeto que contem as informações do Sistema
        
        inputTCP = new ObjectInputStream(clientTCP.getInputStream());//Decifra as informações vindas do cliente
        System.out.println("Cliente TCP: "+clientTCP.getInetAddress()+":"+clientTCP.getPort());
        str = (String) inputTCP.readObject();//Transforma o objeto passado em String
        System.out.println("Recebido: "+str);
    }
    //Construtor que permite conexão UDP
    public ActionsDistributor(DatagramSocket socket, DatagramPacket packet, ControllerDistributor ctrl) throws IOException{
        this.ctrl = ctrl;//Seta o objeto que contem as informações do Sistema
        this.inputUDP = packet; //Recebe o pacote enviado ao servidor
        clientUDP = socket;//Recebe o meio de comunicação com o cliente
        
        System.out.println("Conectou");
        
        System.out.println("Cliente UDP: "+inputUDP.getAddress()+":"+inputUDP.getPort());
        str = new String(inputUDP.getData(),0,inputUDP.getLength());//Transforma o objeto passado em String
        System.out.println("Recebido: "+str);
    }
    
    //Metodo responsável pelas atividades da Thread
    @Override
    public void run(){
        String[] array = str.split("#");
        try{
            switch (array[0]) { //Testa o comando
                case "SERVER":
                    switch(array[1]){
                        case "CONNECT":
                            newServerConnection(array[2]);
                        break;
                        case "DISCONNECT":
                            disconnect(array[2]);
                        break;
                        case "GETSTORAGES":
                            getStorages();
                        break;
                    }                    
                break;
                case "CLIENT":
                    switch (array[1]){
                        case "CONNECT":
                            newClientConnection();
                        break;
                    }
                break;
            }
            ActionsDistributor.currentThread().interrupt();
        }catch (IOException e){
            System.out.println("Error to connect!");
        }
    }

    private void newClientConnection() throws IOException {
        outputTCP = new ObjectOutputStream(clientTCP.getOutputStream());
        String s = ctrl.newClientConnection();
        System.out.println(s);
        outputTCP.writeObject(s);
        outputTCP.flush();
        outputTCP.close();
    }

    private void newServerConnection(String data) throws IOException {
        String resp = ctrl.newServerConnection(data);
        outputTCP = new ObjectOutputStream(clientTCP.getOutputStream());
        System.out.println(resp);
        outputTCP.writeObject(resp);
        outputTCP.flush();
        outputTCP.close();        
    }
    
    private void getStorages() throws IOException {
        String resp = ctrl.getStoragesAddress();
        outputTCP = new ObjectOutputStream(clientTCP.getOutputStream());
        System.out.println(resp);
        outputTCP.writeObject(resp);
        outputTCP.flush();
        outputTCP.close();        
    }

    private void disconnect(String data) throws IOException {
        String resp;
        if(ctrl.serverDisconnect(data)){
            resp = "SUCESS";
        }else{
            resp  = "FAIL";
        }
        outputTCP = new ObjectOutputStream(clientTCP.getOutputStream());
        System.out.println(resp);
        outputTCP.writeObject(resp);
        outputTCP.flush();
        outputTCP.close();
    }
}