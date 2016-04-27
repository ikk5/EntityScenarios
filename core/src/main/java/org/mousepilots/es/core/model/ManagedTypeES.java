package org.mousepilots.es.core.model;

import java.util.Set;
import org.mousepilots.es.core.model.proxy.Proxy;
import javax.persistence.metamodel.ManagedType;

/**
 * Instances of the type {@link ManagedTypeES} represent entity, mapped superclass, and embeddable types.
 * @param <T> The represented type.
 * @see TypeES
 * @see ManagedType
 * @author Roy Cleven
 * @version 1.0, 19-10-2015
 */
public interface ManagedTypeES<T> extends TypeES<T>, ManagedType<T> {
    
    /**
    * @return the original JPA static meta-model class for the represented class
    */
    Class<?> getMetamodelClass();    
    
    Class<? extends Proxy<T>> getProxyJavaType();

    @Override
    <Y> SingularAttributeES<? super T, Y> getSingularAttribute(String name, Class<Y> type);

    @Override
    <Y> SingularAttributeES<T, Y> getDeclaredSingularAttribute(String name, Class<Y> type);

    @Override
    <E> CollectionAttributeES<? super T, E> getCollection(String name, Class<E> elementType);

    @Override
    <E> CollectionAttributeES<T, E> getDeclaredCollection(String name,Class<E> elementType);

    @Override
    <E> SetAttributeES<? super T, E> getSet(String name, Class<E> elementType);

    @Override
    <E> SetAttributeES<T, E> getDeclaredSet(String name, Class<E> elementType);

    @Override
    <E> ListAttributeES<? super T, E> getList(String name, Class<E> elementType);

    @Override
    <E> ListAttributeES<T, E> getDeclaredList(String name, Class<E> elementType);

    @Override
    <K, V> MapAttributeES<? super T, K, V> getMap(String name, Class<K> keyType, Class<V> valueType);

    @Override
    <K, V> MapAttributeES<T, K, V> getDeclaredMap(String name, Class<K> keyType, Class<V> valueType);

    @Override
    AttributeES<? super T, ?> getAttribute(String name);

    @Override
    AttributeES<T, ?> getDeclaredAttribute(String name);

    @Override
    SingularAttributeES<? super T, ?> getSingularAttribute(String name);

    @Override
    SingularAttributeES<T, ?> getDeclaredSingularAttribute(String name);

    @Override
    CollectionAttributeES<? super T, ?> getCollection(String name);

    @Override
    CollectionAttributeES<T, ?> getDeclaredCollection(String name);

    @Override
    SetAttributeES<? super T, ?> getSet(String name);

    @Override
    SetAttributeES<T, ?> getDeclaredSet(String name);

    @Override
    ListAttributeES<? super T, ?> getList(String name);

    @Override
    ListAttributeES<T, ?> getDeclaredList(String name);

    @Override
    MapAttributeES<? super T, ?, ?> getMap(String name);

    @Override
    MapAttributeES<T, ?, ?> getDeclaredMap(String name);

    /**
     * @return whether or not {@code getJavaType()} is concrete, and has a zero-arg accessible constructor
     */
    boolean isInstantiable();

    /**
    * @return a new instance of {@link #getJavaType()}
    * @throws UnsupportedOperationException if {@code !this.isInstantiable()}
    */
    T createInstance() throws UnsupportedOperationException;
    
    
    /**
     * Returns whether or not {@code this.getJavaType()} provides a method for determining the owner(s) of an instance. See 
     * {@link ProvidesOwners} on how to configure such a method.
     * @return whether or not {@code this.getJavaType()} provides a method for determining the owner(s) of an instance
     */
    boolean supportsHasOwners();
    
    /**
     * 
     * @param instance
     * @return the user-names of the owner(s) of the {@code instance}
     * @throws UnsupportedOperationException if {@code !this.supportsHasOwners()} 
     */
    Set<String> getOwners(T instance)  throws UnsupportedOperationException;

    /**
    * @return a new and otherwise uninitialized proxy for instances of {@link #getJavaType()}
    * @throws UnsupportedOperationException if {@code !this.isInstantiable()}
    */
    Proxy<T> createProxy() throws UnsupportedOperationException;
    
}
