/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syncrohour;

import java.net.*;
import java.io.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aurel
 */
public class MessageManager implements Runnable {

   private final int PORT;
   //private final String NAME_MASTER;
   private final DatagramSocket socket;
   private final boolean running;
   private long delayMilliSec = 0;

   public MessageManager(int port) throws SocketException {
      PORT = port;
      socket = new DatagramSocket();
      running = true;
   }

   public long getDelay() {
      return delayMilliSec;
   }

   public int getPORT() {
      return PORT;
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
      SocketAddress address;
      DatagramPacket packet;
      byte[] buffer;

      Random r = new Random();

      while (running) {
         try {
            id++;
            buffer = new byte[]{DELAY_REQUEST, id};
            address = Utils.getNameMaster();
            packet = new DatagramPacket(buffer, 0, buffer.length, address);

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

               //calcul of the delay (delay = (tm - ts) / 2)
               delayMilliSec = ((timeReceivedRequestForMaster - timeSendedRequestForSlave) / 2);
               System.out.println("delayMilliSec: " + delayMilliSec);

            }

//TimeUnit.SECONDS.sleep((Utils.getMIN() + (int) (Math.random() * ((Utils.getMAX() - Utils.getMIN() ) + 1))));
            Utils.waitRandomTime();

         } catch (UnknownHostException ex) {
            Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);

         } catch (IOException ex) {
            Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
      socket.close();
   }
}
