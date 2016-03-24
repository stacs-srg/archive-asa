/*
 * Created on May 23, 2005 at 1:44:55 PM.
 */
package uk.ac.stand.dcs.asa.storage.absfilesystem.usecase;

import uk.ac.stand.dcs.asa.interfaces.IGUID;
import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.persistence.impl.ReflectivePersistentObject;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;

/**
 * @author al
 */
public abstract class Person extends ReflectivePersistentObject {

    private int age;
    private String name;
    /**
     * 
     */
    public Person( int age, String name ) {
        super();
        this.age = age;
        this.name = name;
    }
    
    public Person( IGUID guid, IPID pid, IData thedata ) {
        super(thedata, pid, guid);       
    }
    
    public int getAge() {
        return age;
    }

    public String getName() {
    	return name;
    }
    
}
