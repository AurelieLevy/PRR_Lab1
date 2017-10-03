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

   private final int port;
   private final String nameMaster;
   private DatagramSocket socket;

   public MessageManager(int port, String name) throws SocketException {
      this.port = port;
      this.nameMaster = name;
      socket = new DatagramSocket(port);
   }

   @Override
   public void run() {
      try {
         /*String message = "Hello from slave";
         byte[] bufferSend = message.getBytes();
         byte[] bufferReceived = new byte[256];
         InetAddress address;
         try {
         address = InetAddress.getByName(name);
         DatagramPacket packetSended = new DatagramPacket(bufferSend, bufferSend.length,
         address, port);
         socket.send(packetSended);
         DatagramPacket packetReceived = new DatagramPacket(bufferReceived, bufferReceived.length);
         socket.receive(packetReceived);
         String messageReceived = new String(packetSended.getData());
         System.out.println("Paquet reçu: " + messageReceived);
         } catch (UnknownHostException ex) {
         Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE,
         null, ex);
         //TODO
         } catch (IOException ex) {
         Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);
         //TODO
         }*/
         String message = "Hello from slave";
         byte[] buffer = message.getBytes();
         
         InetAddress address = InetAddress.getByName(nameMaster);
         DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
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
