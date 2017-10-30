/**
 * Fichier: MulticastManage.java
 * Auteurs: Nadir Benallal, Aurelie Levy
 * Creation: Octobre 2017
 * But: Gestion du multicast du cote de l'esclave
 * Recoit les messages SYNC et FOLLOW_UP du maitre, utilise leur contenu pour
 * mettre a jour l'heure de l'esclave
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

public class MulticastManager implements Runnable {

   private final int PORT;
   private final String ADDRESS_GROUP;
   private long gap;
   private boolean isDoneOnce = false;
   private boolean initiate = false;
   private boolean runningMulticast;

   private static long timeSlaveMilliSec;

   public MulticastManager(int port, String addGrp) {
      PORT = port;
      ADDRESS_GROUP = addGrp;
      runningMulticast = true;
      gap = 0;
      isDoneOnce = false;
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
      long timeReceivedForSlave = 0;
      long timeSendedForMaster = 0;
      byte id = 0;

      MulticastSocket socket;
      InetAddress groupe;
      MessageManager msgM = null;
      try {
         socket = new MulticastSocket(PORT);
         groupe = InetAddress.getByName(ADDRESS_GROUP);
         socket.joinGroup(groupe);

         byte[] buffer;
         DatagramPacket packet;

         while (runningMulticast) {
            buffer = new byte[10];
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            buffer = packet.getData();
            //verification of the message
            if (buffer[0] == Utils.getSync()) {
               timeReceivedForSlave = System.currentTimeMillis();
               //System.out.println("timeReceivedForSlave " + timeReceivedForSlave);
               id = buffer[1];
               System.out.println("SYNC recieved");
            } else if (buffer[0] == Utils.getFollowUp() && buffer[1] == id) {
               System.out.println("FollowUp recieved");

               timeSendedForMaster = Utils.getTimeLong(buffer);

               //calcul de l'ecart
               gap = timeSendedForMaster - timeReceivedForSlave;
               //System.out.println("timeSendedForMaster " + timeSendedForMaster);
               System.out.println("ecart: " + gap);
               isDoneOnce = true;
            }

            if (isDoneOnce) {
               if (!initiate) {
                  msgM = new MessageManager();
                  Utils.setAdressMaster(packet.getAddress());
                  Thread threadPtToPT = new Thread(msgM);
//Utils.waitRandomTime();//attente pour la première fois
                  threadPtToPT.start();
                  initiate = true;
               }

               long shift = gap + msgM.getDelay();
               timeSlaveMilliSec = System.currentTimeMillis() + shift;//change l'heure courante de l'esclave
               //System.out.println("timeSlaveMilliSec: " + timeSlaveMilliSec);
               System.out.println("heure de l'esclave: " + new SimpleDateFormat("dd MM yyyy HH:mm:ss").format(new Date(timeSlaveMilliSec)));
            }
         }
         socket.leaveGroup(groupe);
         socket.close();
      } catch (IOException ex) {
         Logger.getLogger(MulticastManager.class.getName()).log(Level.SEVERE, null, ex);
      }

   }

   public void stop() {
      runningMulticast = false;
   }

   public boolean isRunningPtToPt() {
      return runningMulticast;
   }
}
