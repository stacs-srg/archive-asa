/*
 * Created on 07-Jan-2005
 */
package uk.ac.stand.dcs.asa.jchord.interfaces;

/**
 * @author stuart
 */
public interface IFingerTableFactory {
	public abstract IFingerTable makeFingerTable(IJChordNode localNode);
}
