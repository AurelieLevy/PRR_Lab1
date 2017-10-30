
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syncrohour;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aurel
 */
public class Utils {

   ///static SocketAddress NAME_MASTER;
   static InetAddress ADRESS_MASTER;
   static final int MAX = 60;
   static final int MIN = 4;
   static final int K = 2;
   static final String multicastAddress = "239.10.10.1";
   static final int PORT_MASTER = 2222;
   
   static int getPortMaster(){
      return PORT_MASTER;
   }
   
   static String getMulticastAddress(){
      return multicastAddress;
   }

   static void setAdressMaster(InetAddress adress){
      ADRESS_MASTER = adress;
   }
   
   static InetAddress getAdressMaster(){
      return ADRESS_MASTER;
   }
   /*static void setNameMaster(SocketAddress name) {
      NAME_MASTER = name;
   }*/

   /*static SocketAddress getNameMaster() {
      return NAME_MASTER;
   }*/

   static int getMAX() {
      return MAX;
   }

   static int getMIN() {
      return MIN;
   }

   static int getK() {
      return K;
   }

   static long getTimeLong(byte[] values) {
      long time = 0;
      for (int i = 9; i >= 2; i--) {
         time <<= 8;
         time |= (values[i] & 0xFF);
      }
      return time;
   }

   static void waitRandomTime() {
      Random r = new Random();
      try {
         TimeUnit.SECONDS.sleep((Utils.getMIN() * Utils.getK() + r.nextInt(Utils.MAX * Utils.getK() - Utils.getMIN() * Utils.getK())));
      } catch (InterruptedException ex) {
         Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

}
