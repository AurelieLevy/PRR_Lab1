/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syncrohour;



/**
 *
 * @author aurel
 */
public class SyncroHour_Slave {

   private final String nameSlave;

   public SyncroHour_Slave(String name) {
      if (name == null || name == "") {
         System.err.println("slave's name missing");
         throw new Error("invalid Parameter");
      } else {
         nameSlave = name;
      }
   }

   

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      //value for the waiting time
      
      int k = 2;
      int min = 4*k;
      int max = 60*k;

      multicastManager multiM = new multicastManager(2223, "multicast1", "239.10.10.1", min, max);
      Thread threadMulticast = new Thread(multiM);

      threadMulticast.start();
   }
}
