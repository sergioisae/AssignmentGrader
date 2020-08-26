/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nfp121.Model;

import java.util.ArrayList;

/**
 *
 * @author sergio
 */

 public class CriteriaListSingelton  {  

        private static CriteriaListSingelton mInstance;
        private ArrayList<String[]> list = null;

        public static CriteriaListSingelton getInstance() {
            if(mInstance == null)
                mInstance = new CriteriaListSingelton();

            return mInstance;
        }

        private CriteriaListSingelton() {
          list = new ArrayList<String[]>();
        }
        // retrieve array from anywhere
        public ArrayList<String[]> getArray() {
         return this.list;
        }
        //Add element to array
        public void addToArray(String[] value) {
         list.add(value);
        }
        public void removeFromArray(String[] value){
            list.remove(value);
        }
}
