/**
 * Fichier: Utils.java
 * Auteurs: Nadir Benallal, Aurelie Levy
 * Creation: Octobre 2017
 * But: Configuration de l'esclave
 * ATTENTION: doit correspondre a celle du maitre!!!!
 */
package syncrohour;

import java.net.InetAddress;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {

   ///static SocketAddress NAME_MASTER;
   private static InetAddress ADRESS_MASTER;
   private static final int MAX = 60;
   private static final int MIN = 4;
   private static final int K = 2;
   private static final String MULTICAST_ADRESS = "239.10.10.1";
   private static final int PORT_MASTER = 2222;
   private static final byte DELAY_REQUEST = 0x03,
           DELAY_RESPONSE = 0x04,
           SYNC = 0x01,
           FOLLOW_UP = 0x02;

   public static byte getSync() {
      return SYNC;
   }

   public static byte getFollowUp() {
      return FOLLOW_UP;
   }

   public static byte getDelayRequest() {
      return DELAY_REQUEST;
   }

   public static byte getDelayResponse() {
      return DELAY_RESPONSE;
   }

   public static int getPortMaster() {
      return PORT_MASTER;
   }

   public static String getMULTICAST_ADRESS() {
      return MULTICAST_ADRESS;
   }

   public static void setAdressMaster(InetAddress adress) {
      ADRESS_MASTER = adress;
   }

   public static InetAddress getAdressMaster() {
      return ADRESS_MASTER;
   }

   public static int getMAX() {
      return MAX;
   }

   public static int getMIN() {
      return MIN;
   }

   public static int getK() {
      return K;
   }

   public static long getTimeLong(byte[] values) {
      long time = 0;
      for (int i = 9; i >= 2; i--) {
         time <<= 8;
         time |= (values[i] & 0xFF);
      }
      return time;
   }

   public static void waitRandomTime() {
      Random r = new Random();
      try {
         TimeUnit.SECONDS.sleep((Utils.getMIN() * Utils.getK() + r.nextInt(Utils.MAX * Utils.getK() - Utils.getMIN() * Utils.getK())));
      } catch (InterruptedException ex) {
         Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
//TODO
      }
   }

}
