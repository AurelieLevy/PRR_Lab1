/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syncrohour;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class multicastManager implements Runnable {

   private final int PORT;
   private final String NAME_MASTER;
   private final String ADDRESS_GROUP;
   private double timeMaster;

   public multicastManager(int port, String name, String addGrp) {
      this.PORT = port;
      this.NAME_MASTER = name;
      this.ADDRESS_GROUP = addGrp;
   }
   
   public String getAddressGroupe(){
      return ADDRESS_GROUP;
   }
   
   public int getPORT(){
      return PORT;
   }
   
   public double getTimeMaster(){
      return timeMaster;
   }
   
   
   @Override
   public void run() {
      byte[] buffer = new byte[256];
      try {
         MulticastSocket socket = new MulticastSocket(this.PORT);
         InetAddress groupe = InetAddress.getByName(ADDRESS_GROUP);
         socket.joinGroup(groupe);
         
         DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
         socket.receive(packet);
         String messageRecieved = new String(packet.getData());
         
         //envoi timeMaster
         
         
         
         
         
         
         System.out.println("Diffusion client: Message recu: " + messageRecieved);
         
         socket.leaveGroup(groupe);
         socket.close();
         
         
      } catch (IOException ex) {
         Logger.getLogger(multicastManager.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

}
