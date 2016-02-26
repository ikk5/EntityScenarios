package org.mousepilots.es.core.model.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.persistence.metamodel.Type;
import org.mousepilots.es.core.model.HasValue;
import org.mousepilots.es.core.model.TypeES;
import org.mousepilots.es.core.util.StringUtils;

/**
 * @author Nicky Ernste
 * @version 1.0, 18-12-2015
 * @param <T> The type of the represented object or attribute
 */
public abstract class TypeESImpl<T> implements TypeES<T> {
    /**
     * this {@link Type}'s unique ordinal
     */
    public final int ordinal;
    private final Class<T> javaType;
    private final Type.PersistenceType persistenceType;
    private final Class<?> metamodelClass;
    private final Integer superTypeOrdinal;
    private final SortedSet<Integer> subTypes;
    private final Constructor<? extends HasValue<T>> hasValueConstructor;

    /**
     * @param ordinal the type's ordinal
     * @param javaType the {@link Type#getJavaType()}
     * @param persistenceType the {@link Type#getPersistenceType()}
     * @param metamodelClass the type's original JPA meta-model class
     * @param superTypeOrdinal the ordinal of {@code javaType}'s super-class' {@link Type}
     * @param subTypeOrdinals the ordinals of {@code javaType}'s sub-class' {@link Type}s
     * @param hasValueConstructor the value of hasValueConstructor
     */
    public TypeESImpl(
         int ordinal, 
         Class<T> javaType, 
         PersistenceType persistenceType, 
         Class<?> metamodelClass, 
         Integer superTypeOrdinal, 
         Collection<Integer> subTypeOrdinals, 
         Constructor<? extends HasValue<T>> hasValueConstructor){
        this.ordinal = ordinal;
        this.javaType = javaType;
        this.persistenceType = persistenceType;
        this.metamodelClass = metamodelClass;
        this.superTypeOrdinal = superTypeOrdinal;
        this.subTypes = new TreeSet<>(subTypeOrdinals);
        this.hasValueConstructor=hasValueConstructor;
    }
    
    protected void init(){}

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public Class<T> getJavaType() {
        return javaType;
    }

    @Override
    public PersistenceType getPersistenceType() {
        return persistenceType;
    }

    @Override
    public Class<?> getMetamodelClass() {
        return metamodelClass;
    }

    @Override
    public SortedSet<TypeES<? super T>> getSuperTypes() {
        final SortedSet<TypeES<? super T>> supers = new TreeSet<>();
        for(TypeES t= this; t!=null; t=t.getSuperType()){
            supers.add(t);
        }
        return supers;
    }

    @Override
    public SortedSet<TypeES<? extends T>> getSubTypes() {
        SortedSet<TypeES<? extends T>> subs = new TreeSet<>();
        final AbstractMetamodelES metaModel = AbstractMetamodelES.getInstance();
        for(Integer subTypeOrdinal : subTypes) {
            subs.add(metaModel.getType(subTypeOrdinal));
        }
        return subs;
    }
    
    @Override
    public TypeES<? super T> getSuperType() {
        return AbstractMetamodelES.getInstance().getType(this.superTypeOrdinal);
    }
    
    public HasValue<T> wrap(T value){
        final HasValue<T> hv = hasValueConstructor.invoke();
        hv.setValue(value);
        return hv;
    }
        
    @Override
    public int compareTo(TypeES o) {
        return Integer.compare(getOrdinal(), o.getOrdinal());
    }

    @Override
    public String toString() {
        return StringUtils.createToString(getClass(), Arrays.asList(
                "javaType", javaType,
                "ordinal",  ordinal));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(toString());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        //singleton
        return this==obj;
    }
    
    
}