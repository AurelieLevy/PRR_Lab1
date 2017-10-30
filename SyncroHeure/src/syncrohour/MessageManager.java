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

   //private final int PORT;
   //private final String NAME_MASTER;
   private final DatagramSocket SOCKET;
   private boolean runningPtToPt;
   private long delayMilliSec = 0;

   public MessageManager() throws SocketException {
      SOCKET = new DatagramSocket();
      runningPtToPt = true;
   }

   public long getDelay() {
      return delayMilliSec;
   }

   /**
    * Permet de gérer l'envoi/reception des delay_XXXX Format: delay_request:
    * tableau de byte [nomMsg, id] delay_response: tableau de byte [nomMsg, id,
    * h]
    */
   @Override
   public void run() {
      byte id = 0x00;

      long timeSendedRequestForSlave;
      long timeReceivedRequestForMaster;
      //SocketAddress address;
      InetAddress address;
      DatagramPacket packet;
      byte[] buffer;

      Random r = new Random();

      while (runningPtToPt) {
         try {
            id++;
            buffer = new byte[]{Utils.getDelayRequest(), id};
            address = Utils.getAdressMaster();

            //System.out.println("port : " + address.toString());
            packet = new DatagramPacket(buffer, buffer.length, address, Utils.getPortMaster());

            timeSendedRequestForSlave = System.currentTimeMillis();
            SOCKET.send(packet);//envoi du DELAY_REQUEST
            //System.out.println("Delay_Request sent");

            buffer = new byte[10];
            packet = new DatagramPacket(buffer, buffer.length);
            SOCKET.receive(packet);//recieve delayresponse
            //System.out.println("Delay response recieved");
            //verification qu'on a recu DELAY_RESPONSE => nom = 0x04
            if (packet.getData()[0] == Utils.getDelayResponse() && packet.getData()[1] == id) {
               //System.out.println("Delay_response id: " + id);
               timeReceivedRequestForMaster = Utils.getTimeLong(buffer);

               //calcul du delai (delay = (tm - ts) / 2)
               delayMilliSec = ((timeReceivedRequestForMaster - timeSendedRequestForSlave) / 2);
               //System.out.println("delayMilliSec: " + delayMilliSec);

            }

//VOIR POUR TASK SCHEDULER
            //Utils.waitRandomTime();
         } catch (UnknownHostException ex) {
            Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);
//TODO

         } catch (IOException ex) {
            Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);
//TODO
         }
      }
      SOCKET.close();
   }

   public void stop() {
      runningPtToPt = false;
   }

   public boolean isRunningPtToPt() {
      return runningPtToPt;
   }
}
