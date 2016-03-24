/*
 * Created on 06-Jul-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.exampleApplication;

import uk.ac.stand.dcs.asa.applicationFramework.impl.AIDImpl;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.AID;

import java.util.HashMap;

public class Test {
    public static void main(String[] args) {
        HashMap h = new HashMap();
        AID a1 = new AIDImpl("aaa");
        AID a2 = new AIDImpl("aaa");
        
        if(a1.equals(a2)){
            System.out.println("AID objects are equal");
        }else{
            System.out.println("AID objects are not equal");
        }
        System.out.println("adding a1");
        h.put(a1,"data string");
 
        if(h.containsKey(a1)){
            System.out.println("HashMap contains key a1");
        }else{
            System.out.println("HashMap does not contain key a1");
        }
        if(h.containsKey(a2)){
            System.out.println("HashMap contains key a2");
        }else{
            System.out.println("HashMap does not contain key a2");
        }
        
        Object data = h.get(a1);
        if(data!=null){
            System.out.println("retrieved data using a1");
        }else{
            System.out.println("could not retrieve data using a1");
        }
 
        
        Object data2 = h.get(a2);
        if(data2!=null){
            System.out.println("retrieved data using a2");
        }else{
            System.out.println("could not retrieve data using a2");
        }

    }
}
