/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syncrohour;

import java.net.*;
import java.io.*;
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

   public MessageManager(int port, String name) throws SocketException {
      this.PORT = port;
      this.NAME_MASTER = name;
      socket = new DatagramSocket(port);
   }
   
   public int getPORT(){
      return PORT;
   }
   
   public String getNAME_MASTER(){
      return NAME_MASTER;
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
         socket.close();

      } catch (UnknownHostException ex) {
         Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);

      } catch (IOException ex) {
         Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}
