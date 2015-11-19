/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mousepilots.es.change.abst;

import java.io.Serializable;
import org.mousepilots.es.change.HasVersion;
import org.mousepilots.es.model.AttributeES;
import org.mousepilots.es.model.IdentifiableTypeES;
import org.mousepilots.es.model.Wrapper;

/**
 * @author Jurjen van Geenen
 * @author Roy Cleven
 * @param <I>
 * @param <V>
 */
public abstract class AbstractIdentifiableVersionedChange<I extends Serializable, V extends Serializable> extends AbstractIdentifiableChange<I> implements HasVersion<V> {

    private Wrapper version;
    public AbstractIdentifiableVersionedChange() {
        super();
    }

    public AbstractIdentifiableVersionedChange(IdentifiableTypeES type, I id, V version) {
        super(type, id);
        AttributeES va = type.getVersion(version.getClass());
    }

    @Override
    public final V getVersion() {
        return version == null ? null : (V) version.unwrap();
    }
}