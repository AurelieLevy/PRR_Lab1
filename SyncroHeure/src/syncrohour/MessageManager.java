/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syncrohour;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.concurrent.TimeUnit;
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
   private final boolean running;
   private long delayMilliSec = 0;
   private int min;
   private int max;

   public MessageManager(int port, String name, int min, int max) throws SocketException {
      this.PORT = port;
      this.NAME_MASTER = name;
      socket = new DatagramSocket();
      this.running = true;
      this.min = min;
      this.max = max;
   }

   public long getDelay() {
      return delayMilliSec;
   }
   
   public int getPORT() {
      return PORT;
   }

   public String getNAME_MASTER() {
      return NAME_MASTER;
   }

   /**
    * Permet de gérer l'envoi/reception des delay_XXXX Format: delay_request:
    * tableau de byte [nomMsg, id] delay_response: tableau de byte [nomMsg, id,
    * h]
    */
   @Override
   public void run() {
      byte id = 0x00;
      byte DELAY_REQUEST = 0x03,
              DELAY_RESPONSE = 0x04;

      
      long timeSendedRequestForSlave;
      long timeReceivedRequestForMaster;
      InetAddress address;
      DatagramPacket packet;
      byte[] buffer;

      while (running) {
         try {
            id++;
            buffer = new byte[]{DELAY_REQUEST, id};
            address = InetAddress.getByName(NAME_MASTER);
            packet = new DatagramPacket(buffer, buffer.length, address, PORT);

            timeSendedRequestForSlave = System.currentTimeMillis();
            socket.send(packet);//send DELAY_REQUEST
            System.out.println("Delay_Request sent id: " + id);
            
            
            buffer = new byte[10];
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);//recieve delayresponse
            
            //verification that we received DELAY_RESPONSE => name = 0x04
            if (packet.getData()[0] == DELAY_RESPONSE && packet.getData()[1] == id) {
               System.out.println("Delay_response id: " + id);
               timeReceivedRequestForMaster = Utils.getTimeLong(buffer);
               //calcul of the delay
                  delayMilliSec = ((timeReceivedRequestForMaster - timeSendedRequestForSlave) / 2);
                  System.out.println("delayMilliSec: " + delayMilliSec);

            }

TimeUnit.SECONDS.sleep((min + (int) (Math.random() * ((max - min) + 1))));
            //String messageRecieved = new String(packet.getData());
         } catch (UnknownHostException ex) {
            Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);

         } catch (IOException ex) {
            Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);
         } catch (InterruptedException ex) {
            Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);
         }

      }
      socket.close();
   }
}
