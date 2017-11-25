/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.server.connection;

import br.com.server.control.Controller;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Antonio Raian
 */
//Classe (thread) responsável por todas as ações do servior, responsável por decifrar informações vindas dos clientes
public class ActionsServer extends Thread{
    private Socket clienteTCP;//Variavel responsável por receber a conexão do servidor TCP
    private DatagramSocket clienteUDP;//Variavel responsável por receber a conexão do servidor UDP
    private ObjectInputStream entrada;//Objetos que armazena informações advindas do cliente
    private ObjectOutputStream saida; // Objeto usado para enviar mensagens aos clientes TCP
    private DatagramPacket entradaUDP; //Objeto usado para enviar mensagens aos clientes UDP;
    private byte[] saidaUDP;
    private final Controller ctrl;//Nosso objeto que contem as listas e informações salvas do sistema
    private final String str; //String usada pra receber as requisições dos clientes
    
    //Construtor que permite conexão TCP
    public ActionsServer(Socket socket, Controller ctrl) throws IOException, ClassNotFoundException {
        clienteTCP = socket;//Recebe a conexão
        this.ctrl = ctrl;//Seta o objeto que contem as informações do Sistema
        
        entrada = new ObjectInputStream(clienteTCP.getInputStream());//Decifra as informações vindas do cliente
        System.out.println("Cliente TCP: "+clienteTCP.getInetAddress()+":"+clienteTCP.getPort());
        str = (String) entrada.readObject();//Transforma o objeto passado em String
        System.out.println("Recebido: "+str);
    }
    //Construtor que permite conexão UDP
    public ActionsServer(DatagramSocket socket, DatagramPacket packet, Controller ctrl) throws IOException{
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
                    switch (array[1]){
                        case "NEWITEM":
                            newProduct(array[2]);
                        break;
                        case "UPDATEITEM":
                            updateProduct(array[2]);
                        break;
                        case "DELETEITEM":
                            deleteProduct(array[2]);
                    }
                break;
                case "CLIENT":
                    switch (array[1]){
                        case "GETPRODUCTS":
                            getProducts();
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
        LinkedList list = ctrl.getProducts();
        saida = new ObjectOutputStream(clienteTCP.getOutputStream());
        saida.writeObject(list);
        saida.flush();
        saida.close();
    }

    private void newProduct(String data) throws IOException {
        String s = ctrl.newProduct(data);
        saida = new ObjectOutputStream(clienteTCP.getOutputStream());
        saida.writeObject(s);
        saida.flush();
        saida.close();
    }

    private void updateProduct(String data) throws IOException {
        String s = ctrl.updateProduct(data);
        saida = new ObjectOutputStream(clienteTCP.getOutputStream());
        saida.writeObject(s);
        saida.flush();
        saida.close();
    }

    private void deleteProduct(String code) throws IOException {
        String s = ctrl.deleteProduct(code);
        saida = new ObjectOutputStream(clienteTCP.getOutputStream());
        saida.writeObject(s);
        saida.flush();
        saida.close();
    }
}