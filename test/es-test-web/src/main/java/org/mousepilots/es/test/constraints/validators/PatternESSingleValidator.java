/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mousepilots.es.test.constraints.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.mousepilots.es.core.command.CRUD;
import org.mousepilots.es.core.command.Command;
import org.mousepilots.es.core.model.AttributeES;
import org.mousepilots.es.core.model.ManagedTypeES;
import org.mousepilots.es.core.model.MemberES;
import org.mousepilots.es.core.model.impl.AbstractMetamodelES;
import org.mousepilots.es.test.constraints.MessageBuilder;
import org.mousepilots.es.test.constraints.annotations.PatternES;
import org.mousepilots.es.test.server.Request;

/**
 *
 * @author bhofsted
 */
public class PatternESSingleValidator implements ConstraintValidator<PatternES, Request> {

    private ManagedTypeES<?> managedType;
    private AttributeES<?, ?> attribute;
    private CRUD operation;
    private String regexp;

    @Override
    public void initialize(PatternES constraintAnnotation) {
        managedType = AbstractMetamodelES.getInstance().managedType(constraintAnnotation.javaType());
        attribute = managedType.getAttribute(constraintAnnotation.attribute());
        operation = constraintAnnotation.operation();
        regexp = constraintAnnotation.regexp();
    }

    @Override
    public boolean isValid(Request value, ConstraintValidatorContext context) {
        for (Command command : value.getCommands()) {
            if (command.getType() == managedType && command.getOperation() == operation) {
                final MemberES javaMember = attribute.getJavaMember();
                final String attributeValue = (String) javaMember.get(command.getRealSubject());
                if (attributeValue == null || !attributeValue.matches(regexp)) {
                    new MessageBuilder().buildMessage(context, attribute.getName(), "");
                    return false;
                }
            }
        }
        return true;
    }
}
