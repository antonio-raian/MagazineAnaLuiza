/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.server.connection;

import br.com.server.control.ControllerServer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

/**
 *
 * @author Antonio Raian
 */
//Essa é a classe responsável por criar novas Threads do servidor UDP
public class ServerUDP implements Runnable{
    private final MulticastSocket server; //atributo responsável por estabelecer a conexão UDP
    private final byte[] receive = new byte[2048];//Array para receber a informação da conexão
    private final ControllerServer ctrl; //Atributo que recebe o controlador
    private final ConnectionServer connection;
    private final int port = 8090;
    private final String ipGroup = "235.2.2.2";

    //Construtor 
    public ServerUDP(ControllerServer ctrl, ConnectionServer connection) throws SocketException, IOException {
        server = new MulticastSocket(this.port);//Abre uma coneão UDP para uma determinada porta
        server.joinGroup(InetAddress.getByName(ipGroup));
        this.ctrl = ctrl;
        this.connection = connection;
        System.out.println("UDP: Ouvindo a porta "+port);
        new Thread(this).start();//Criando e iniciando a thread principal
    }

    @Override
    public void run() {
        while (!server.isClosed()) {//Laço de repetição para a criação de várias threads a medida que receber novas conexões
            try {
                DatagramPacket p = new DatagramPacket(receive, receive.length);//Cria um pacote pra armazenar informações advindas do cliente
                System.out.println("Esperando conexão UDP");
                server.receive(p);//Espera a conexão
                String s = new String(p.getData());
                System.out.println(s);
                new ActionsServer(server, p, ctrl,connection).start();//Cria uma thread do tipo AtividadeServidor que irá tratar as informações recebidas do cliente
                System.out.println("Cliente UDP conectado!");
            } catch (IOException ex) {
                System.out.println("Pacote não recebido!");
            }
        }
    }
    
    //Metodo que "fecha" a conexão do servidor UDP
    public void stop() throws IOException{
        //servidor.disconnect();
        server.close();        
    }
    
}