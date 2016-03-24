package uk.ac.stand.dcs.asa.applications.ringVis;

import uk.ac.stand.dcs.asa.util.Diagnostic;

import java.util.HashMap;
import java.util.Map;

/**
 * Directory that maps node keys to the node cells used to represent the nodes graphically.
 */
public class JChordCellDirectory {
    
    /************************ Fields ************************/
    
	/**
	 * Map that implements the cell directory
	 */
	private Map map;


   /********************** Constructors **********************/
   
    public JChordCellDirectory() {
        Diagnostic.trace(Diagnostic.FULL);
        map = new HashMap();
    }

    /************************ Methods ************************/
    
    /**
     * Adds a cell to the directory for the node with the given key
     * @param key the key of the node
     * @param nc the cell that graphically represents the node
     */
    public void addCell( String key, JChordNodeCell nc ) {
        Diagnostic.trace("adding JChordNodeCell to directory", Diagnostic.FULL);
        map.put( key, nc );
    }
    
    public void removeCell(String key) {
        Diagnostic.trace(Diagnostic.FULL);
        map.remove(key);
    }

    /**
     * Looks up a cell in the directory corresponding to a given key
     * @param key the key of the node
     * @return the cell that represents the node
     */
    public JChordNodeCell lookup( String key ) {        
    		Diagnostic.trace(Diagnostic.FULL);
    		return (JChordNodeCell)map.get( key );
    }

    /**
     * Checks whether the directory is empty
     * @return true if the directory is empty
     */
    public boolean dirEmpty(){
        Diagnostic.trace(Diagnostic.FULL);
        return map.isEmpty();
    }

	/**
	 * Returns the directory that stores the cells
	 * @return the directory that stores the cells
	 */
	public Map getMap() {
		return map;
	}

    /**
     * Returns the number of cells in the directory
     * @return The number of cells in the directory
     */
    public int size(){
        Diagnostic.trace(Diagnostic.FULL);
        return map.size();
    }
}
