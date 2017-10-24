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
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
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
      long timeReceivedForSlave = 0;
      long timeSendedForMaster = 0;
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
               timeReceivedForSlave = System.currentTimeMillis();
               id = packet.getData()[1];
               isDoneOnce = true;
               System.out.println("SYNC id: " + id);
            } else if (packet.getData()[0] == FOLLOW_UP && packet.getData()[1] == id) {
               System.out.println("FollowUp id: " + id);
               byte[] values = new byte[8];
               for (int i = 2; i < 10; i++) {
                  values[i - 2] = packet.getData()[i];
               }
               ByteBuffer buf = ByteBuffer.allocate(Long.BYTES);
               buf.put(values, 0, values.length);
               buf.flip();
               timeSendedForMaster = buf.getLong();
               //calcul of the gap
               gap = timeSendedForMaster - timeReceivedForSlave;
               System.out.println("gap: " + gap);
               //isDoneOnce = true;
            }

            if (isDoneOnce) {
               //MessageManager msgM = new MessageManager(2222, "NADIR-PC", min, max);
               MessageManager msgM = new MessageManager(2225, "MSI", min, max);
               Thread threadPtToPT = new Thread(msgM);
               /*
               try {
                  TimeUnit.SECONDS.sleep((min + (int) (Math.random() * ((max - min) + 1))));
               } catch (InterruptedException ex) {
                  Logger.getLogger(SyncroHour_Slave.class.getName()).log(Level.SEVERE, null, ex);
               }
               */
               threadPtToPT.start();
               long shift = this.getGap() + msgM.getDelay();
               timeSlaveMilliSec = System.currentTimeMillis() + shift;//change current time of slave
               System.out.println(new SimpleDateFormat("dd MM yyyy HH:mm:ss").format(new Date(timeSlaveMilliSec)));
               /*MessageManager msgM;
               long shift;
               try {
                  msgM = new MessageManager(2222, "NADIR-PC", min, max);
                  Thread threadPtToPt = new Thread(msgM);
                  while (calculation) {
                     shift = multiM.getGap() + msgM.getDelay();
                     timeSlaveMilliSec = System.currentTimeMillis() + shift;//change current time of slave
                     System.out.println(new SimpleDateFormat("dd MM yyyy HH:mm:ss").format(new Date(timeSlaveMilliSec)));
                  }
               } catch (SocketException ex) {
                  Logger.getLogger(SyncroHour_Slave.class.getName()).log(Level.SEVERE, null, ex);
               }*/
            }

            //String messageRecieved = new String(packet.getData());
            //System.out.println("Diffusion client: Message recu: " + messageRecieved);
         }
         
         socket.leaveGroup(groupe);
         socket.close();
      } catch (IOException ex) {
         Logger.getLogger(multicastManager.class.getName()).log(Level.SEVERE, null, ex);
      }

   }

}
