/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mousepilots.es.core.command;

import java.io.Serializable;
import org.mousepilots.es.core.model.AttributeES;
import org.mousepilots.es.core.scenario.ServerContext;

/**
 *
 * @author geenenju
 * @param <E>
 * @param <A>
 * @param <AD>
 */
public interface UpdateAttribute<E, A, AD extends AttributeES<? super E, A>> extends Serializable{
     
     void executeOnClient(Update<E,?,A,AD,?> update);
     
     void undo(Update<E,?,A,AD,?> update);
     
     void redo(Update<E,?,A,AD,?> update);
     
     void executeOnServer(Update<E,?,A,AD,?> update, ServerContext serverContext);
     
}