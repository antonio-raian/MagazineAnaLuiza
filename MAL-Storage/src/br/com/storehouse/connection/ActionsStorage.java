/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.storehouse.connection;

import br.com.storehouse.controller.ControllerStorage;
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
public class ActionsStorage extends Thread{
    private Socket clienteTCP;//Variavel responsável por receber a conexão do servidor TCP
    private DatagramSocket clienteUDP;//Variavel responsável por receber a conexão do servidor UDP
    private ObjectInputStream entrada;//Objetos que armazena informações advindas do cliente
    private ObjectOutputStream saida; // Objeto usado para enviar mensagens aos clientes TCP
    private DatagramPacket entradaUDP; //Objeto usado para enviar mensagens aos clientes UDP;
    private byte[] saidaUDP;
    private final ControllerStorage ctrl;//Nosso objeto que contem as listas e informações salvas do sistema
    private final String str; //String usada pra receber as requisições dos clientes
    
    //Construtor que permite conexão TCP
    public ActionsStorage(Socket socket, ControllerStorage ctrl) throws IOException, ClassNotFoundException {
        clienteTCP = socket;//Recebe a conexão
        this.ctrl = ctrl;//Seta o objeto que contem as informações do Sistema
        
        entrada = new ObjectInputStream(clienteTCP.getInputStream());//Decifra as informações vindas do cliente
        System.out.println("Cliente TCP: "+clienteTCP.getInetAddress()+":"+clienteTCP.getPort());
        str = (String) entrada.readObject();//Transforma o objeto passado em String
        System.out.println("Recebido: "+str);
    }
    //Construtor que permite conexão UDP
    public ActionsStorage(DatagramSocket socket, DatagramPacket packet, ControllerStorage ctrl) throws IOException{
        this.ctrl = ctrl;//Seta o objeto que contem as informações do Sistema
        this.entradaUDP = packet; //Recebe o pacote enviado ao servidor
        clienteUDP = socket;//Recebe o meio de comunicação com o cliente
        
        System.out.println("Conectou");
        
        System.out.println("Cliente UDP: "+entradaUDP.getAddress()+":"+entradaUDP.getPort());
        str = new String(entradaUDP.getData(),0,entradaUDP.getLength());//Transforma o objeto passado em String
        System.out.println("Recebido: "+str);
    }
    
    //Metodo responsável pelas atividades da Thread
    @Override
    public void run(){
        String[] array = str.split("#");
        try{
            switch (array[0]) { //Testa o comando
                case "STORAGE":
                    switch(array[1]){
                        case "NEWITEM":
                            newItem(array[2]);
                        break;
                        case "UPDATEITEM":
                            updateItem(array[2]);
                        break;
                        case "DELETEITEM":
                            deleteItem(array[2]);
                        break;
                        case "GETLOG":
                            getLog(array[2]);
                        break;
                    }
                break;

            }
        }catch(IOException e){
            System.out.println("Erro no ActionStorage: "+e.getMessage());
        }
        ActionsStorage.currentThread().interrupt();
    }

    private void newItem(String item) throws IOException {
        String[] list = item.split(";");
        ctrl.addProduct(list[0], list[1], list[2], list[3], list[4], 0, Double.parseDouble(list[6]));
        ctrl.saveLog(str);
    }

    private void updateItem(String item) throws IOException {
        String[] list = item.split(";");
        ctrl.updateProduct(list[0], list[1], list[2], list[3], list[4], Double.parseDouble(list[6]));
        ctrl.saveLog(str);
    }

    private void deleteItem(String item) throws IOException {
        ctrl.removeProduct(item);
        ctrl.saveLog(str);
    }

    private void getLog(String lastPosition) throws IOException {
        String list = ctrl.getLog(Integer.parseInt(lastPosition));
        System.out.println("Enviando Log "+list);
        DatagramPacket sendDP = new DatagramPacket(list.getBytes(), list.getBytes().length, entradaUDP.getAddress(), entradaUDP.getPort());
        clienteUDP.send(sendDP);
    }
}