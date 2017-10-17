/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syncrohour;

import java.net.*;
import java.io.*;
import java.util.Date;
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
   /*private Date timeMaster;
   private Date timeSlave;*/
   private final boolean running;
   private long delayMilliSec;
   private int min = 4;
   private int max = 60;
   private int k = 2;

   public MessageManager(int port, String name) throws SocketException {
      this.PORT = port;
      this.NAME_MASTER = name;
      socket = new DatagramSocket(port);
      //timeMaster = new Date();
      running = true;
   }
   
   public long getDelay(){
      return delayMilliSec;
   }

   /*private String getTimeString(){
      Long m = System.currentTimeMillis();
      Date d = new Date(m);
      return new SimpleDateFormat("hh:mm:ss").parse(d);
   }*/
   /*public void setTimeMaster(Date newTime) {
      timeMaster = newTime;
   }*/

   public int getPORT() {
      return PORT;
   }

   public String getNAME_MASTER() {
      return NAME_MASTER;
   }

  /* public Date getTimeMaster() {
      return timeMaster;
   }*/

   //delay
   //voir https://www.developpez.net/forums/d510150/java/general-java/apis/java-util/difference-entre-heures/
   /*private double calculDelay(Date timeMaster, Date timeSlave) {
      return (((timeMaster.getTime() - timeSlave.getTime()) / 1000) / 2);
   }*/

   /**
    * Permet de gérer l'envoi/reception des delay_XXXX
    * Format:
    * delay_request: tableau de byte [nomMsg, id]
    * delay_response: tableau de byte [nomMsg, id, h]
    */
   @Override
   public void run() {
      byte id = 0x00;
      byte DELAY_REQUEST = 0x03,
              DELAY_RESPONSE = 0x04;
      
      //date de l'envoi de la requête. Il s'agit du nb de ms depuis 01.01.1970
      long dateSendRequest;
      long dateReceiveRequest;
      
      
      while (running) {
         try {
            //String message = "Hello from slave";                                    
            //byte[] buffer = message.getBytes();
            id++;
            byte[] buffer = new byte[]{DELAY_REQUEST, id};

            InetAddress address = InetAddress.getByName(NAME_MASTER);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, PORT);
            dateSendRequest = System.currentTimeMillis();
            socket.send(packet);//send DELAY_REQUEST

            buffer = new byte[256];
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            //verification that we received DELAY_RESPONSE => name = 0x04
            if (packet.getData()[0] == DELAY_RESPONSE && packet.getData()[1] == id) {
               dateReceiveRequest = packet.getData()[2];
               //calcul of the delay
                delayMilliSec = (dateReceiveRequest - dateSendRequest)/2;    
            }
            //String messageRecieved = new String(packet.getData());
         } catch (UnknownHostException ex) {
            Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);

         } catch (IOException ex) {
            Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);
         }
         //TODO
         //Thread.sleep(PORT);
      }
      socket.close();
   }
}
