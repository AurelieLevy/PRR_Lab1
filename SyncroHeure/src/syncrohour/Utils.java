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

   private static InetAddress ADRESS_MASTER;
   private static final int MAX = 60;
   private static final int MIN = 4;
   private static final int K = 2;
   private static final String MULTICAST_ADRESS = "239.10.10.1";
   private static final int PORT_MASTER = 2222;
   private static final byte SYNC = 0x01,
           FOLLOW_UP = 0x02,
           DELAY_REQUEST = 0x03,
           DELAY_RESPONSE = 0x04;

   /**
    * Permet de recuperer l'identifiant du SYNC
    *
    * @return
    */
   public static byte getSync() {
      return SYNC;
   }

   /**
    * Permet de recuperer l'identifiant du FOLLOW_UP
    *
    * @return
    */
   public static byte getFollowUp() {
      return FOLLOW_UP;
   }

   /**
    * Permet de recuperer l'identifiant du DELAY_REQUEST
    *
    * @return
    */
   public static byte getDelayRequest() {
      return DELAY_REQUEST;
   }

   /**
    * Permet de recuperer l'identifiant du DELAY_RESPONSE
    *
    * @return
    */
   public static byte getDelayResponse() {
      return DELAY_RESPONSE;
   }

   /**
    * Permet de recuperer le port utilise par le master
    *
    * @return
    */
   public static int getPortMaster() {
      return PORT_MASTER;
   }

   /**
    * Permet de recuperer l'adresse multicast
    *
    * @return
    */
   public static String getMULTICAST_ADRESS() {
      return MULTICAST_ADRESS;
   }

   /**
    * Permet de modifier l'adresse du maitre
    *
    * @param adress nouvelle adresse
    */
   public static void setAdressMaster(InetAddress adress) {
      ADRESS_MASTER = adress;
   }

   /**
    * Permet de recuperer l'adresse du maitre
    *
    * @return l'adresse du maitre
    */
   public static InetAddress getAdressMaster() {
      return ADRESS_MASTER;
   }

   /**
    * Permet de recuperer depuis un tableau de byte un long
    *
    * @param values tableau de byte
    * @return valeur en long
    */
   public static long getTimeLong(byte[] values) {
      long time = 0;
      for (int i = 9; i >= 2; i--) {
         time <<= 8;
         time |= (values[i] & 0xFF);
      }
      return time;
   }

   /**
    * Fonction d'attente
    */
   public static void waitRandomTime() {
      Random r = new Random();
      try {
         TimeUnit.SECONDS.sleep((MIN * K + r.nextInt(MAX * K - MIN * K)));
      } catch (InterruptedException ex) {
         Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
         System.err.println("Problem while waiting");
//TODO
      }
   }

}
