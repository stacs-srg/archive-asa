/*
 * Created on 11-Jan-2006
 */
package uk.ac.stand.dcs.asa.storage.store.impl.newDistibuted.genericP2PStore.interfaces;

import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IPIDGenerator;

public interface IPIDGeneratorFactory {
    IPIDGenerator makePidGenerator();
}
