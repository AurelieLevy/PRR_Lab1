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
   private boolean isDoneOnce;
   private boolean initiate;
   private boolean runningMulticast;

   private static long timeSlaveMilliSec;

   /**
    * Constructeur du multicast de l'esclave
    *
    * @param port port
    * @param addGrp adresse du groupe
    */
   public MulticastManager(int port, String addGrp) {
      this.PORT = port;
      this.ADDRESS_GROUP = addGrp;
      this.runningMulticast = true;
      this.gap = 0;
      this.isDoneOnce = false;
      this.initiate = false;
   }

   /*
    * Permet de gérer la reception des SYNC et FOLLOW_UP 
    * Format: SYNC: tableau de byte [nomMsg, id] 
    *          FOLLOW_UP: tableau de byte [nomMsg, id, h]
    */
   @Override
   public void run() {
      long timeReceivedForSlave = 0;
      long timeSendedForMaster = 0;
      byte id = 0;
      //cette constante permet de tester en local le protocole
      //long cstTest = 999999999;

      MulticastSocket socket;
      InetAddress groupe;
      MessageManager msgM = null;

      try {
         socket = new MulticastSocket(PORT);
         groupe = InetAddress.getByName(ADDRESS_GROUP);
         socket.joinGroup(groupe);//abonnement

         byte[] buffer;
         DatagramPacket packet;

         while (runningMulticast) {
            buffer = new byte[10];
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            buffer = packet.getData();
            //verification du message
            if (buffer[0] == Utils.getSync()) {

               //A DECOMMENTER POUR TESTER
               //timeReceivedForSlave = System.currentTimeMillis() + cstTest;
               timeReceivedForSlave = System.currentTimeMillis();
               id = buffer[1];
               System.out.println("SYNC recieved");
            } else if (buffer[0] == Utils.getFollowUp() && buffer[1] == id) {
               System.out.println("FollowUp recieved");
               timeSendedForMaster = Utils.getTimeLong(buffer);

               //calcul de l'ecart (tm - ti)
               gap = timeSendedForMaster - timeReceivedForSlave;
               System.out.println("ecart: " + gap);
               isDoneOnce = true;
            }

            //si on a recu au moins un SYNC
            if (isDoneOnce) {
               //s'il faut creer le manager point a point (a ne faire qu'une fois!)
               if (!initiate) {
                  msgM = new MessageManager();
                  //on recupere l'adresse du master pour pouvoir l'avoir dans la 
                  //communication point a point
                  Utils.setAdressMaster(packet.getAddress());
                  Thread threadPtToPT = new Thread(msgM);
                  Utils.waitRandomTime();//attente pour la première fois
                  threadPtToPT.start();
                  initiate = true;
               }

               //calcul du decalage ( ecart(i) + delai(i) )
               long shift = gap + msgM.getDelay();
               System.out.println("shift: " + shift);

               //calcul de l'heure locale ( hlocal(i) = hsys(i) + decalage(i)
               timeSlaveMilliSec = (System.currentTimeMillis() + shift);//change l'heure courante de l'esclave
               
               //A DECOMMENTER POUR TESTER
               //timeSlaveMilliSec = (System.currentTimeMillis() + shift + cstTest);
               //System.out.println("dec + cst:" + (shift + cstTest)); //vaut 0 si le protocole est bon
               System.out.println("heure de l'esclave: " + new SimpleDateFormat("dd MM yyyy HH:mm:ss").format(new Date(timeSlaveMilliSec)));
            }
         }
         socket.leaveGroup(groupe);
         socket.close();
      } catch (IOException ex) {
         Logger.getLogger(MulticastManager.class.getName()).log(Level.SEVERE, null, ex);
         System.err.println("Problem while creating DatagramPacket");
      }

   }

   /**
    * Permet d'arreter le multicast
    */
   public void stop() {
      runningMulticast = false;
   }

   /**
    * Permet de savoir si le multicast fonctionne
    *
    * @return true si oui, false sinon
    */
   public boolean isRunningMulticast() {
      return runningMulticast;
   }
}
