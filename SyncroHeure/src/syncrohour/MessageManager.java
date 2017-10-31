/**
 * Fichier: MessageManager.java
 * Auteurs: Nadir Benallal, Aurelie Levy
 * Creation: Octobre 2017
 * But: Gestion de la communication point a point du cote de l'esclave
 * Envoi le delay request en enregistrant l'heure d'envoi puis la compare avec
 * l'heure envoyee en reponse par le maitre (delay response)
 */
package syncrohour;

import java.net.*;
import java.io.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageManager implements Runnable {

   private final DatagramSocket SOCKET;
   private boolean runningPtToPt;
   private long delay;

   /**
    * Constructeur du manager point a point de l'esclave
    *
    * @throws SocketException
    */
   public MessageManager() throws SocketException {
      this.SOCKET = new DatagramSocket();
      this.runningPtToPt = true;
      this.delay = 0;
   }

   /**
    * Permet de recuperer le dela
    *
    * @return un long representant le delai
    */
   public long getDelay() {
      return delay;
   }

   /*
   Permet de gérer l'envoi/reception des delay_XXXX
   Format: delay_request:  tableau de byte [nomMsg, id] 
            delay_response: tableau de byte [nomMsg, id, h]
    */
   @Override
   public void run() {
      byte id = 0x00;

      long timeSendedRequestForSlave;
      long timeReceivedRequestForMaster;
      InetAddress address;
      DatagramPacket packet;
      byte[] buffer;

      Random r = new Random();

      while (runningPtToPt) {//tant que le point a point a lieu
         try {
            id++;
            buffer = new byte[]{Utils.getDelayRequest(), id};
            //recupere l'adresse du maitre recuperee via le multicast
            address = Utils.getAdressMaster();
            
            //preparation du paquet
            packet = new DatagramPacket(buffer, buffer.length, address, Utils.getPortMaster());

            //sauvegarde de l'heure d'envoi du message
            timeSendedRequestForSlave = System.currentTimeMillis();
            SOCKET.send(packet);//envoi du delay_request
            //System.out.println("Delay_Request sent");

            //reception du delay_response
            buffer = new byte[10];
            packet = new DatagramPacket(buffer, buffer.length);
            SOCKET.receive(packet);
            //System.out.println("Delay response recieved");
            
            //verification qu'on a recu DELAY_RESPONSE => nom = 0x04
            if (packet.getData()[0] == Utils.getDelayResponse() && packet.getData()[1] == id) {
               //transformation du tableau recu en long
               timeReceivedRequestForMaster = Utils.getTimeLong(buffer);

               //calcul du delai ( delay = (tm - ts) / 2 )
               delay = ((timeReceivedRequestForMaster - timeSendedRequestForSlave) / 2);
               //System.out.println("delay: " + delay);

            }

//VOIR POUR TASK SCHEDULER
            Utils.waitRandomTime();
         } catch (IOException ex) {
            Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Problem while sending packet");
//TODO
         }
      }
      SOCKET.close();
   }

   /**
    * Permet d'arreter le point a point
    */
   public void stop() {
      runningPtToPt = false;
   }

   /**
    * permet de savoir l'etat du point a point
    * @return true si en marche, false sinon
    */
   public boolean isRunningPtToPt() {
      return runningPtToPt;
   }
}
