/*
 * Created on 29-Nov-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.interfaces;

import uk.ac.stand.dcs.asa.jchord.impl.KeyRange;

import java.util.Iterator;

/**
 * A data structure that specifies a some key range R and provides an iterator
 * over a set of ReplicaSite objects that represent the sites on which data, with
 * keys that are in R, should be replicated.
 * 
 * @author stuart
 */
public interface ReplicaInfo {
    public KeyRange getKeyRange();
    public Iterator replica_site_iterator();
}
