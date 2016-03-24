/*
 * Created on 08-Dec-2004
 */
package uk.ac.stand.dcs.asa.jchord.interfaces;

/**
 * Interface permitting a node to be informed of neighbours close in physical space (or elsewhere)
 * 
 * @author stuart
 */
public interface INeighbourAwareJChordNode {
    
    /**
     * @param neighbours - the neighbours (close in physical space to inform the node about
     */
    void addNeighbours(IJChordNode[] neighbours);
}
