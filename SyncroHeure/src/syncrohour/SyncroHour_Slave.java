/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syncrohour;

import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author aurel
 */
public class SyncroHour_Slave {

   private final String nameSlave;

   //private int countUnknown = 1;
   /*private byte[] slaveTime;
   private byte[] slaveDifference;
   private byte[] slaveDelay;
   private Date systHour;*/
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

      try {
         TimeUnit.SECONDS.sleep((min + (int) (Math.random() * ((max - min) + 1))));
      } catch (InterruptedException ex) {
         Logger.getLogger(SyncroHour_Slave.class.getName()).log(Level.SEVERE, null, ex);
      }

      MessageManager msgM;
      long shift;
      boolean calculation = true;
      long timeSlaveMilliSec;
      try {
         msgM = new MessageManager(2220, "NADIR-PC", min, max);
         Thread threadPtToPt = new Thread(msgM);
         threadPtToPt.start();
         while (calculation) {
            shift = multiM.getGap() + msgM.getDelay();
            timeSlaveMilliSec = System.currentTimeMillis() + shift;//change current time of slave
            System.out.println(new SimpleDateFormat("dd MM yyyy HH:mm:ss").format(new Date(timeSlaveMilliSec)));
         }
      } catch (SocketException ex) {
         Logger.getLogger(SyncroHour_Slave.class.getName()).log(Level.SEVERE, null, ex);
      }
      
   }

}
