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


   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
       multicastManager multiM = new multicastManager(2223, Utils.getMulticastAddress());
      Thread threadMulticast = new Thread(multiM);

      threadMulticast.start();
   }
}
