/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.distributor.control;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 *
 * @author Antonio Raian
 */
public class ControllerDistributor {
    private final LinkedList<String> servers;//Pilha de servidores
    private final String addressStorages;
    private final String portStorages;
    private final String addressServers;
    private final String portServers;

    public ControllerDistributor(String addressStorages, String portStorages, String addressServers, String portServers) {
        servers = new  LinkedList<>();
        this.addressStorages = addressStorages;
        this.portStorages = portStorages;
        this.addressServers = addressServers;
        this.portServers = portServers;
    }
    
    public String newServerConnection(String data){
        servers.add(data);
        return addressServers+";"+portServers; //Retorna porta e endereço do grupo de servidores;
    }
    
    //Métoro que faz o ciclo dos servidores (O servidor da vez reebe a conexão e vai pro final da fila)
    public String newClientConnection(){
        String server = null;
        try{
            server = servers.remove();//Pega o próximo da lista
            servers.add(server);//Coloca no final
        }catch (NoSuchElementException e){
        }
        if(server!=null)
            return server;//retorna o endereço e a porta do servidor da vez
        return null;
    }

    public boolean serverDisconnect(String data) {
        return servers.remove(data);
    }

    public String getStoragesAddress() {
        return addressStorages+";"+portStorages;
    }
}