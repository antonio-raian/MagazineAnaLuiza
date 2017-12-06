/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.distributor.connection;

import br.com.distributor.control.ControllerDistributor;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 *
 * @author Antonio Raian
 */
//Classe (thread) responsável por todas as ações do servior, responsável por decifrar informações vindas dos clientes
public class ActionsDistributor extends Thread{
    private DatagramSocket clientUDP;//Variavel responsável por receber a conexão do servidor UDP
    private DatagramPacket inputUDP; //Objeto usado para enviar mensagens aos clientes UDP;
    private final ControllerDistributor ctrl;//Nosso objeto que contem as listas e informações salvas do sistema
    private final String str; //String usada pra receber as requisições dos clientes
    
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
        String resp = ctrl.newClientConnection();
        if(resp==null){
            resp = "null";
        }
        //Cria um pacote e concatena a String de retorno
        DatagramPacket sendResult = new DatagramPacket(resp.getBytes(), resp.getBytes().length, inputUDP.getAddress(),inputUDP.getPort());
        clientUDP.send(sendResult);//Envia o pacote criado para o cliente
    }

    private void newServerConnection(String data) throws IOException {
        String resp = ctrl.newServerConnection(data);
        //Cria um pacote e concatena a String de retorno
        DatagramPacket sendResult = new DatagramPacket(resp.getBytes(), resp.getBytes().length, inputUDP.getAddress(),inputUDP.getPort());
        clientUDP.send(sendResult);//Envia o pacote criado para o cliente
    }
    
    private void getStorages() throws IOException {
        String resp = ctrl.getStoragesAddress();
        //Cria um pacote e concatena a String de retorno
        DatagramPacket sendResult = new DatagramPacket(resp.getBytes(), resp.getBytes().length, inputUDP.getAddress(),inputUDP.getPort());
        clientUDP.send(sendResult);//Envia o pacote criado para o cliente
    }

    private void disconnect(String data) throws IOException {
        String resp;
        if(ctrl.serverDisconnect(data)){
            resp = "SUCESS";
        }else{
            resp  = "FAIL";
        }
        //Cria um pacote e concatena a String de retorno
        DatagramPacket sendResult = new DatagramPacket(resp.getBytes(), resp.getBytes().length, inputUDP.getAddress(),inputUDP.getPort());
        clientUDP.send(sendResult);//Envia o pacote criado para o cliente
    }
}