/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.storehouse.view;

import br.com.storehouse.connection.ConnectionStorage;
import br.com.storehouse.connection.ServerUDP;
import br.com.storehouse.controller.ControllerStorage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Antonio Raian
 */
public class HomeStorage extends javax.swing.JFrame {

    /**
     * Creates new form HomeStorage
     */
    private ControllerStorage ctrl;
    private ConnectionStorage connection;
    //private String number = "";
    public HomeStorage() throws ClassNotFoundException, IOException {
        initComponents();
        post();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnAddMerc = new javax.swing.JButton();
        btnListarMerc = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtCoordinateX = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCoordinateY = new javax.swing.JTextField();
        btnConnect = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Depósito - Magazine Ana Luíza");

        jPanel1.setLayout(new java.awt.GridLayout(2, 2));

        btnAddMerc.setText("Adicionar Mercadoria");
        btnAddMerc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddMercActionPerformed(evt);
            }
        });
        jPanel1.add(btnAddMerc);

        btnListarMerc.setText("Listar Mercadorias");
        btnListarMerc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListarMercActionPerformed(evt);
            }
        });
        jPanel1.add(btnListarMerc);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setText("Coordenada X:");
        jPanel2.add(jLabel2);

        txtCoordinateX.setColumns(5);
        jPanel2.add(txtCoordinateX);

        jLabel3.setText("Coordenada Y:");
        jPanel2.add(jLabel3);

        txtCoordinateY.setColumns(5);
        jPanel2.add(txtCoordinateY);

        btnConnect.setText("Conectar");
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });
        jPanel2.add(btnConnect);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 55, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddMercActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddMercActionPerformed
        AddProduct ap = new AddProduct(this, true, ctrl, connection);
        ap.setVisible(true);
    }//GEN-LAST:event_btnAddMercActionPerformed

    private void btnListarMercActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListarMercActionPerformed
        ListProduct lp = new ListProduct(this, true, ctrl);
        lp.setVisible(true);
    }//GEN-LAST:event_btnListarMercActionPerformed

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
        try {
            ctrl = new ControllerStorage(InetAddress.getLocalHost().getHostAddress(), txtCoordinateX.getText(), txtCoordinateY.getText());
            connection = new ConnectionStorage();
            String resp = "Socket TimeOut";
            int i =0;
            while (resp.equals("Socket TimeOut")&&i<3){
                System.out.println("tentando acessar o log...");
                resp = connection.getLog(ctrl.getLogSize());
                i++;
            }
            if(ctrl.verifyLog(resp)){
                JOptionPane.showMessageDialog(null, "Deposito Atualizado!");
            }
            ServerUDP sudp = new ServerUDP(ctrl);
            btnAddMerc.setEnabled(true);
            btnListarMerc.setEnabled(true);
        } catch (ClassNotFoundException | IOException ex) {
            Logger.getLogger(HomeStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnConnectActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HomeStorage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomeStorage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomeStorage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomeStorage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new HomeStorage().setVisible(true);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Erro:"+ex.getMessage());
                } catch (ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "Não foi possível conectar a base de dados!");
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddMerc;
    private javax.swing.JButton btnConnect;
    private javax.swing.JButton btnListarMerc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField txtCoordinateX;
    private javax.swing.JTextField txtCoordinateY;
    // End of variables declaration//GEN-END:variables
    
//    private void init() throws IOException, ClassNotFoundException{
//        while("".equals(number)){
//            number = JOptionPane.showInputDialog("Qual é o numero desse Depósito?");
//        }
//        if(number==null){
//            throw new ClosedByInterruptException();
//        } 
//    }

    private void post() throws FileNotFoundException, IOException {
        File arq = new File("./StorageFiles/Products_"+InetAddress.getLocalHost().getHostAddress()+".txt");
        if(arq.exists())
            try (BufferedReader buffRead = new BufferedReader(new FileReader(arq))) {
                String line = buffRead.readLine();
                if(line!=null){
                    String[] aux = line.split(":");
                    txtCoordinateX.setText(aux[1]);
                    txtCoordinateY.setText(aux[2]);
                    txtCoordinateX.setEnabled(false);
                    txtCoordinateY.setEnabled(false);
                }
            }
        btnAddMerc.setEnabled(false);
        btnListarMerc.setEnabled(false);
    }
}