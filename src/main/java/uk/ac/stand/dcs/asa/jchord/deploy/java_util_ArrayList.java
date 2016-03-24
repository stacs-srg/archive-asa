/*
 * Created on 18-Nov-2005
 */
package uk.ac.stand.dcs.asa.jchord.deploy;

import uk.ac.stand.dcs.rafda.rrt.soap.AbstractCustomSerialiser;

import java.util.ArrayList;
import java.util.Iterator;

public class java_util_ArrayList extends AbstractCustomSerialiser {

    public Object[] convertObjectToArrayOfValues(Object o) {
        ArrayList hm=(ArrayList) o;
        int s=hm.size();
        Object[] values = new Object[s];
        for (Iterator i = hm.iterator(); i.hasNext();) {
            values[s] = i.next();
        }
        return new Object[] { values };
    }

    public void initializeObjectUsingArrayOfValues(Object o, Object[] os) {
        ArrayList hm = (ArrayList) o;
        Object[] values = (Object[]) os[0];
        for (int i = 0; i < values.length; i++) {
            hm.add(values[i]);
        }
    }

}
