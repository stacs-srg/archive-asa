/*
 * Created on 29-Nov-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.impl;

import uk.ac.stand.dcs.asa.applicationFramework.interfaces.ReplicaInfo;
import uk.ac.stand.dcs.asa.jchord.impl.KeyRange;

import java.util.ArrayList;
import java.util.Iterator;

public class ReplicaInfoImpl implements ReplicaInfo {

    ArrayList replica_sites;
    private KeyRange range;
    
    /**
     * @param replica_sites
     */
    public ReplicaInfoImpl(KeyRange range, ArrayList replica_sites){
        this.range = range;
        
        if (replica_sites == null) this.replica_sites = new ArrayList();
        else                       this.replica_sites = replica_sites;
    }

    public KeyRange getKeyRange() {
        return range;
    }

    public Iterator replica_site_iterator() {
        return replica_sites.iterator();
    }
}
