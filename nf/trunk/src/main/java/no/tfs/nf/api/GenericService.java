package no.tfs.nf.api;

import java.util.Collection;

public interface GenericService<T>
{
    /**
     * Saves the given object instance.
     * 
     * @param object the object instance.
     * @return the generated identifier.
     */
    int save( T object );
    
    /**
     * Updates the given object instance.
     * 
     * @param object the object instance.
     */
    void update( T object );

    /**
     * Retrieves the object with the given identifier. This method will first
     * look in the current Session, then hit the database if not existing.
     * 
     * @param id the object identifier.
     * @return the object identified by the given identifier.
     */
    T get( int id );
    
    /**
     * Retrieves the object with the given identifier, assuming it exists.
     * 
     * @param id the object identifier.
     * @return the object identified by the given identifier or a generated proxy.
     */
    T load( int id );

    /**
     * Retrieves the object with the given code. Assumes that there is a code
     * property on the relevant object with a uniqueness constraint.
     * 
     * @param code the code.
     * @return the object with the given code.
     */
    T getByCode( String code );

    /**
     * Retrieves a Collection of all objects.
     * 
     * @return a Collection of all objects.
     */
    Collection<T> getAll();
    
    /**
     * Removes the given object instance.
     * 
     * @param object the object instance to delete.
     */
    void delete( T object );
}
