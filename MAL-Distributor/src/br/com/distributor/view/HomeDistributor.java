/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.distributor.view;

import br.com.distributor.connection.ServerTCP;
import br.com.distributor.connection.ServerUDP;
import br.com.distributor.control.ControllerDistributor;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author Antonio Raian
 */
public class HomeDistributor {
    private static ControllerDistributor ctrl = new ControllerDistributor("235.1.1.1","8080","235.2.2.2","8090");
    public static void main(String[] args) {
        try {
            new ServerTCP(45678, ctrl);
            new ServerUDP(48900, ctrl);
            System.out.println("Multicast dos Depósitos: 235.1.1.1:8080");
            System.out.println("Multicast dos Servidores: 235.2.2.2:8090");
        } catch (IOException ex) {
            System.out.println("Error in create a server");
        }
    }
}
