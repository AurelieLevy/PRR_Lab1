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
import java.util.Date;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class multicastManager implements Runnable {

   private final int PORT;
   private final String NAME_MASTER;
   private final String ADDRESS_GROUP;
   /*private Date timeMaster;
   private Date timeSlave;*/
   private long gap;
   private boolean isDoneOnce;
   private final boolean running;

   public multicastManager(int port, String name, String addGrp) {
      this.PORT = port;
      this.NAME_MASTER = name;
      this.ADDRESS_GROUP = addGrp;
      this.running = true;
      this.gap = 0;
      isDoneOnce = false;
   }

   public boolean getIsDoneOnce() {
      return isDoneOnce;
   }

   public long getGap() {
      return gap;
   }

   /*public void setTimeMaster(Date newTime) {
      timeMaster = newTime;
   }*/
   public String getAddressGroupe() {
      return ADDRESS_GROUP;
   }

   public int getPORT() {
      return PORT;
   }

   /*public Date getTimeMaster() {
      return timeMaster;
   }*/
   /**
    * Permet de gérer la reception des SYNC et FOLLOW_UP Format: SYNC: tableau
    * de byte [nomMsg, id] FOLLOW_UP: tableau de byte [nomMsg, id, h]
    */
   @Override
   public void run() {
      byte SYNC = 0x01,
              FOLLOW_UP = 0x02;
      long dateReceiveSync = 0;
      long dateSendSync = 0;
      byte id = 0;

      MulticastSocket socket;
      InetAddress groupe;
      try {
         socket = new MulticastSocket(this.PORT);
         groupe = InetAddress.getByName(ADDRESS_GROUP);
         socket.joinGroup(groupe);

         while (running) {
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            //verification of the message
            if (packet.getData()[0] == SYNC) {
               dateReceiveSync = System.currentTimeMillis();
               id = packet.getData()[1];
            }
            if (packet.getData()[0] == FOLLOW_UP && packet.getData()[1] == id) {
               //calcul of the gap
               gap = dateSendSync - dateReceiveSync;
               isDoneOnce = true;
            }
            //String messageRecieved = new String(packet.getData());

            //System.out.println("Diffusion client: Message recu: " + messageRecieved);
            socket.leaveGroup(groupe);
         }
         socket.close();
      } catch (IOException ex) {
         Logger.getLogger(multicastManager.class.getName()).log(Level.SEVERE, null, ex);
      }

   }

}
