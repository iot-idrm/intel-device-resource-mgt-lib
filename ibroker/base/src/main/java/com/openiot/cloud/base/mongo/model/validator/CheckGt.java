/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.openiot.cloud.base.mongo.model.validator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = CheckGtValidator.class)
@Documented
public @interface CheckGt {
  String message() default "{com.openiot.cloud.base.mongo.model.validator" + "message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  Class<?> value();
}
