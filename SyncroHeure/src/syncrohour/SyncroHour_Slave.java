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

   final String nameSlave;
   private int countUnknown = 1;
   private byte[] slaveTime;
   private byte[] slaveDifference;
   private byte[] slaveDelay;

   public SyncroHour_Slave(String name) {
      if (name == null || name == "") {
         System.err.println("slave's name missing");
         throw new Error("invalid Parameter");
      } else {
         nameSlave = name;
      }
   }

   //écart
   private double calculGap(double timeMaster, double timeSlave) {
      return (timeMaster - timeSlave);
   }

   //délai
   private double calculDelay(double timeMaster, double timeSlave) {
      return ((timeMaster - timeSlave) / 2);
   }

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      /*try {
         Thread threadCommunication = new Thread(new MessageManager(2222, "NADIR-PC"));
         threadCommunication.start();*/
      multicastManager mm = new multicastManager(2223, "multicast1", "239.10.10.1");
      mm.run();
      //Thread threadMulticastComm = new Thread(new multicastManager(2223, "multicast1", "239.10.10.1"));
      //threadMulticastComm.start();
      /*} catch (SocketException ex) {
         System.err.println("Thread not created!");
      }*/
   }

}
