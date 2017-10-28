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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class multicastManager implements Runnable {

   private final int PORT;
   private final String NAME_MASTER;
   private final String ADDRESS_GROUP;
   private long gap;
   private boolean isDoneOnce;
   private boolean initiate = false;
   private final boolean running;
   private int max;
   private int min;
   private static long timeSlaveMilliSec;

   public multicastManager(int port, String name, String addGrp, int min, int max) {
      this.PORT = port;
      this.NAME_MASTER = name;
      this.ADDRESS_GROUP = addGrp;
      this.running = true;
      this.gap = 0;
      this.isDoneOnce = false;
      this.max = max;
      this.min = min;
   }

   public long getTimeSlaveMilliSec() {
      return timeSlaveMilliSec;
   }

   public boolean getIsDoneOnce() {
      return isDoneOnce;
   }

   public long getGap() {
      return gap;
   }

   public String getAddressGroupe() {
      return ADDRESS_GROUP;
   }

   public int getPORT() {
      return PORT;
   }

   /**
    * Permet de gérer la reception des SYNC et FOLLOW_UP Format: SYNC: tableau
    * de byte [nomMsg, id] FOLLOW_UP: tableau de byte [nomMsg, id, h]
    */
   @Override
   public void run() {
      byte SYNC = 0x01,
              FOLLOW_UP = 0x02;
      long timeReceivedForSlave = 0;
      long timeSendedForMaster = 0;
      byte id = 0;

      MulticastSocket socket;
      InetAddress groupe;
      MessageManager msgM = null;
      try {
         socket = new MulticastSocket(this.PORT);
         groupe = InetAddress.getByName(ADDRESS_GROUP);
         socket.joinGroup(groupe);

         byte[] buffer;
         DatagramPacket packet;
         
         while (running) {
            buffer = new byte[10];
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            buffer = packet.getData();
            //verification of the message
            if (buffer[0] == SYNC) {
               timeReceivedForSlave = System.currentTimeMillis();
      System.out.println("timeReceivedForSlave " + timeReceivedForSlave);
               id = buffer[1];
      System.out.println("SYNC id: " + id);
            } 
            else if (buffer[0] == FOLLOW_UP && buffer[1] == id) {
               //System.out.println("FollowUp id: " + id);

               timeSendedForMaster = Utils.getTimeLong(buffer);
               
               //calcul of the gap (ecart)
               gap = timeSendedForMaster - timeReceivedForSlave;
      System.out.println("timeReceivedForSlave bis " + timeReceivedForSlave);
               System.out.println("timeSendedForMaster " + timeSendedForMaster);
               System.out.println("gap: " + gap);
               isDoneOnce = true;
            }

            if (isDoneOnce) {
               //MessageManager msgM = new MessageManager(2222, "NADIR-PC", min, max);
               if (!initiate) {
                  msgM = new MessageManager(2222, "MSI", min, max);
                  //msgM = new MessageManager(2225, "MSI", min, max);
                  Thread threadPtToPT = new Thread(msgM);
                  threadPtToPT.start();
                  initiate = true;
               }

               long shift = this.getGap() + msgM.getDelay();
               timeSlaveMilliSec = System.currentTimeMillis() + shift;//change current time of slave
               System.out.println("timeSlaveMilliSec: " + timeSlaveMilliSec);
              System.out.println(new SimpleDateFormat("dd MM yyyy HH:mm:ss").format(new Date(timeSlaveMilliSec)));
            }

         }

         socket.leaveGroup(groupe);
         socket.close();
      } catch (IOException ex) {
         Logger.getLogger(multicastManager.class.getName()).log(Level.SEVERE, null, ex);
      }

   }

}
