package org.mousepilots.es.change.impl;

import java.io.Serializable;
import org.mousepilots.es.change.ChangeVisitor;
import org.mousepilots.es.model.AttributeES;
import org.mousepilots.es.model.DTO;

/**
 * @author Roy Cleven
 */
public final class EmbeddableToIdentifiableSingularAssociationAttributeUpdate<A extends Serializable> extends EmbeddableSingularAssociationAttributeUpdate<A> {

    public EmbeddableToIdentifiableSingularAssociationAttributeUpdate() {
    }

    public EmbeddableToIdentifiableSingularAssociationAttributeUpdate(DTO container, AttributeES containerAttribute, DTO updated, AttributeES updatedAttribute, A oldValue, A newValue) {
        super(container, containerAttribute, updated, updatedAttribute, oldValue, newValue);
    }

    @Override
    public void accept(ChangeVisitor changeHandler) {
        changeHandler.visit(this);
    }
}