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
   
   
   
   public SyncroHour_Slave(String name){
      if(name == null || name == ""){
         System.err.println("slave's name missing");
         throw new Error("invalid Parameter");
      }
      else{
         nameSlave = name;
      }
   }
   
   public double calculDiff(){
      
   }

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      // TODO code application logic here
   }

}
