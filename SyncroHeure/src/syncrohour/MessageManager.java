/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syncrohour;

import java.net.*;
import java.io.*;
import java.util.Date;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aurel
 */
public class MessageManager implements Runnable {

   private final int PORT;
   private final String NAME_MASTER;
   private DatagramSocket socket;
   private Date timeMaster;
   private Date timeSlave;
   private final boolean running;

   public MessageManager(int port, String name) throws SocketException {
      this.PORT = port;
      this.NAME_MASTER = name;
      socket = new DatagramSocket(port);
      timeMaster = new Date();
      running = true;
   }
   
   public void setTimeMaster(Date newTime){
      timeMaster = newTime;
   }

   public int getPORT() {
      return PORT;
   }

   public String getNAME_MASTER() {
      return NAME_MASTER;
   }

   public Date getTimeMaster() {
      return timeMaster;
   }

   //delay
   //voir https://www.developpez.net/forums/d510150/java/general-java/apis/java-util/difference-entre-heures/
   private double calculDelay(Date timeMaster, Date timeSlave) {
      return (((timeMaster.getTime() - timeSlave.getTime())/1000) / 2);
   }

   @Override
   public void run() {
      try {
         String message = "Hello from slave";
         byte[] buffer = message.getBytes();

         InetAddress address = InetAddress.getByName(NAME_MASTER);
         DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, PORT);
         socket.send(packet);

         buffer = new byte[256];
         packet = new DatagramPacket(buffer, buffer.length);
         socket.receive(packet);
         String messageRecieved = new String(packet.getData());

         System.out.println("Message recu: " + messageRecieved);
                                                                  //ICI IL FAUT TRAITER LE MESSAGE RECU POUR OBTENIR L'HEURE DU MASTER
         /* 
          //calcul of the  delay
                                                                  
          //maj du timeMaster. il sera récup par le main au fur et a mesure
          timeMaster = ????
          
                                   
*/
         
         
         

      } catch (UnknownHostException ex) {
         Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);

      } catch (IOException ex) {
         Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);
      }

      socket.close();
   }
}
