/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mousepilots.es.test.constraints.annotations;

import org.mousepilots.es.test.constraints.validators.IllegalUpdateValidator;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 *
 * @author bhofsted
 */
@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = IllegalUpdateValidator.class)
public @interface IllegalUpdate {

    String message() default "No Updates are allowed for "/*entity*/; //entity will be added by validator upon failure

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<?>[] constrainedClasses();
}