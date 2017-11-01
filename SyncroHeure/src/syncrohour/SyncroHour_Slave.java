/**
 * Fichier: SyncroHour_Slave.java
 * Auteurs: Nadir Benallal, Aurelie Levy
 * Creation: Octobre 2017
 * But: Gestion principale de l'esclave
 * Lance le thread de multicast, lequel gerera le lancement du thread du
 * point a point
 *
 * Commentaires: Nous avons separe le laboratoire en deux parties afin d'avoir
 * un maitre et un esclave bien distincts. Cependant, dans les deux programmes,
 * vous trouverez un fichier Utils.java. Il s'agit des constantes en commun pour
 * les deux. Pour le bon fonctionnement du protocole entier, il faut s'assurer
 * que les deux fichiers correspondent.
 * Cela permet, dans le cas d'un changement de maitre par exemple, de pouvoir
 * modifier uniquement ce qui est necessaire sans devoir obligatoirement toucher
 * au code.
 *
 */
package syncrohour;

public class SyncroHour_Slave {

   public static void main(String[] args) {
      MulticastManager multiM = new MulticastManager(2223, Utils.getMULTICAST_ADRESS());
      Thread threadMulticast = new Thread(multiM);

      threadMulticast.start();
   }
}
