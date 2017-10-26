
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
public class utils {

   static long getTimeByByteTab(byte[] values) {
      long time = 0;
      for (int i = 9; i >= 2; i--) {
         time <<= 8;
         time |= (values[i] & 0xFF);
      }
      return time;
   }

}
