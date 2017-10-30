/**
 * Fichier: SyncroHour_Slave.java
 * Auteurs: Nadir Benallal, Aurelie Levy
 * Creation: Octobre 2017
 * But: Gestion principale de l'esclave
 * Lance le thread de multicast, lequel gerera le lancement du thread du 
 * point a point
 */
package syncrohour;


public class SyncroHour_Slave {

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      MulticastManager multiM = new MulticastManager(2223, Utils.getMULTICAST_ADRESS());
      Thread threadMulticast = new Thread(multiM);

      threadMulticast.start();
   }
}
