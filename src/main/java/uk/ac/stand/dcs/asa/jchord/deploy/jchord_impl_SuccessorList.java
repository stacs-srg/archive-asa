/*
 * Created on 18-Nov-2005
 */
package uk.ac.stand.dcs.asa.jchord.deploy;

import uk.ac.stand.dcs.asa.jchord.impl.SuccessorList;
import uk.ac.stand.dcs.asa.jchord.interfaces.IJChordRemote;
import uk.ac.stand.dcs.rafda.rrt.soap.AbstractCustomSerialiser;

import java.util.ArrayList;

public class jchord_impl_SuccessorList extends AbstractCustomSerialiser {
    
    public Object[] convertObjectToArrayOfValues(Object o) {
        SuccessorList sl = (SuccessorList) o;
        Object[] localNode = new Object[]{sl.getLocalNode()};
        Object[] al_values = sl.getListAsArray();
        return new Object[] {localNode, al_values};
    }

    public void initializeObjectUsingArrayOfValues(Object o, Object[] os) {
        SuccessorList sl= (SuccessorList) o;
        Object[] al_values = (Object[]) os[1];
        sl.setLocalNode((IJChordRemote)o);
        ArrayList al = new ArrayList();
        for (int i = 0; i < al_values.length; i++) {
            al.add(al_values[i]);
        }
        sl.setStore(al);
    }
}
