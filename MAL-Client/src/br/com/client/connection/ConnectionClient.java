/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.client.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.LinkedList;

/**
 *
 * @author Antonio Raian
 */
public class ConnectionClient {
    private Socket serverTCP; //Atributo que permite conexão com servidor TCP
    private DatagramSocket serverUDP; //Atributo que permite conexão com servidor UDP
    private InetAddress addressDistributor; //Atributo que armazena o endereço do Distribuidor de carga
    private InetAddress addressServer;//Atributo que armazena o endereço do Servidor
    private int portDistributor;//Atributo que armazena a porta de conexãi com o Distribuidor de carga
    private int portServer;//Atributo que armazena a porta de conexão com o servidor
    private ObjectOutputStream outTCP;//Atributo usado para enviar mensagens via TCP
    private ObjectInputStream inTCP;//Atributo usado para receber mensagem via TCP
    private byte[] outUDP; //Objeto que envia informação para o servidor via UDP
    private final byte[] inUDP = new byte[1024]; //Objeto que recebe informação do servidor via UDP
    
    //Metodo pra setar o endereço e a porta do distribuidor de carga, recebe como parametro o endereço e a porta
    public void setDistributor(String address, String port) throws UnknownHostException {
        this.addressDistributor = InetAddress.getByName(address);
        this.portDistributor = Integer.parseInt(port);
    }
    
    //Metodo para iniciar conexão com o Distribuidor de Carga
    public void connectDistributor() throws IOException{
        serverUDP = new DatagramSocket();
    }
    
    //Metodo para iniciar conexão com o Servidor
    public void connectServer() throws IOException{
        serverTCP = new Socket(addressServer, portServer);
        outTCP = new ObjectOutputStream(serverTCP.getOutputStream());
    }
    
    //Metodo que encerra conexão TCP
    public void disconnect() throws IOException{
        inTCP.close();
        outTCP.close();
        serverTCP.close();
    }

    //Metodo que comunica com o distribuidor de carga para entrar no sistema
    public String newConnection() throws UnknownHostException, IOException, ClassNotFoundException{
        connectDistributor();//Inicia a conexão com o distribuidor de carga
        outUDP = ("CLIENT#CONNECT#").getBytes();//Concatena as informações e as envia para o servidor
        //Cria um pacote para envio ao servidor
        DatagramPacket sendInfo = new DatagramPacket(outUDP, outUDP.length, addressDistributor, portDistributor);
        serverUDP.send(sendInfo);//Envia o pacote ao servidor
        sendInfo = new DatagramPacket(inUDP, inUDP.length);//Cria uma nova capsula pra receber dados
        serverUDP.setSoTimeout(3000);//Seta no UDP um tempo de 3 segundos de espera de resposta
        try{
            serverUDP.receive(sendInfo);//Espera uma resposta
        }catch (SocketException | SocketTimeoutException ex){
            return "Socket TimeOut";
        }
        String s = new String(sendInfo.getData());//"quebra" a informação do servidor
        //A informação recebida aq é o endereço e a porta do servidor separado por ";"
        s = s.trim();//Limpa a quantidade de espaços
        if(!s.equals("null")){
            String[] aux = s.split(";");
            addressServer = InetAddress.getByName(aux[0]);//coloca o endereço do servidor na variavel
            portServer = Integer.parseInt(aux[1]);//coloca a porta do servidor na variavel
            return "SUCCESS";//Retorna Sucesso para a tela
        }else{
            return "FAIL";//Retorna Falha para a tela
        }
    }

    //Metodo para de cadastrar no sistema
    public String register(String name, String login, String passwd) throws IOException, ClassNotFoundException {
        connectServer();//Inicia a conexão com o servidor
        outTCP.writeObject("CLIENT#REGISTER#"+name+";"+login+";"+passwd);////Envia para o servidor a requisição e os dados de cadastro
        inTCP = new ObjectInputStream(serverTCP.getInputStream());//recebe informação advinda do servidor
        String s = (String) inTCP.readObject();//Lê o dado recebido
        if(s!=null){
            return "SUCCESS";//Se tudo correr bem, retorna Sucesso para a tela
        }
        return "FAIL";
    }  
    
    //Metodo para autenticação no sistema
    public String login(String login, String passworld) throws IOException, ClassNotFoundException {
        connectServer();//Inicia a conexão com o servidor
        outTCP.writeObject("CLIENT#LOGIN#"+login+";"+passworld);//Concatena as informações e as envia para o servidor
        inTCP = new ObjectInputStream(serverTCP.getInputStream());//recebe informação advinda do servidor
        String s = (String) inTCP.readObject();//Lê o dado recebido
        disconnect();//Desconecta
        if(s!=null){
            return "SUCCESS";//Se tudo correr bem, retorna Sucesso para a tela
        }
        return "FAIL";
    }
    
    //Metodo para listar todos os produtos do sistema
    public String[] getProducts() throws IOException, ClassNotFoundException {
        connectServer();//Inicia a conexão com o servidor
        outTCP.writeObject("CLIENT#GETPRODUCTS#");
        inTCP = new ObjectInputStream(serverTCP.getInputStream());//recebe informação advinda do servidor
        String s[] = (String[]) inTCP.readObject();//Lê o dado recebido
        disconnect();//Desconecta
        return s;//Envia o dado recebido para a tela
    }  

    //Metodo para calcular o frete da compra
    public String calculateTransport(String value, String coordinateX, String coordinateY) throws IOException, ClassNotFoundException{
        connectServer();//Inicia a conexão com o servidor
        outTCP.writeObject("CLIENT#CALCTRANSP#"+value+"@"+coordinateX+"@"+coordinateY+"@");
        inTCP = new ObjectInputStream(serverTCP.getInputStream());//recebe informação advinda do servidor
        String s = (String) inTCP.readObject();//Lê o dado recebido
        disconnect();//Desconecta
        return s;//Envia o dado recebido para a tela
    }
    
    //Metodo que realiza a compra dos itens
    public String buyTheList(String user, LinkedList<String> cart, String coordinateX, String coordinateY, String value) throws IOException, ClassNotFoundException {
        connectServer();//Inicia a conexão com o servidor
        System.out.println(cart);
        outTCP.writeObject("CLIENT#BUY#"+user+"@"+cart+"@"+value+"@");
        inTCP = new ObjectInputStream(serverTCP.getInputStream());//recebe informação advinda do servidor
        String s = (String) inTCP.readObject();//Lê o dado recebido
        disconnect();//Desconecta
        return s;//Envia o dado recebido para a tela
    }
}