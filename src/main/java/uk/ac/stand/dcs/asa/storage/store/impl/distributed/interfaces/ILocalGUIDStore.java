/*
 * Created on 11-Aug-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.distributed.interfaces;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;
import uk.ac.stand.dcs.asa.applicationFramework.interfaces.P2PApplicationException;
import uk.ac.stand.dcs.asa.interfaces.IGUID;
import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StorePutException;

public interface ILocalGUIDStore extends IDistributedGUIDStore {
    //TODO kill Scott, rename method to "put" and fix RRT to work with duplicate method names.
    //Methods should be distinguished by signature, not name.
    public void put3(IGUID guid, IPID[] pid) throws P2PApplicationException, P2PNodeException, StorePutException;

}
