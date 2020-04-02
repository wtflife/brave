/*
 * Copyright 2013-2020 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package brave.spring.beans;

import brave.baggage.BaggageField;
import brave.baggage.CorrelationScopeCustomizer;
import brave.baggage.CorrelationScopeDecorator;
import java.util.List;
import org.springframework.beans.factory.FactoryBean;

/** Spring XML config does not support chained builders. This converts accordingly */
public class CorrelationScopeDecoratorFactoryBean implements FactoryBean {
  CorrelationScopeDecorator.Builder builder;
  List<BaggageField> fields;
  List<CorrelationScopeCustomizer> customizers;

  @Override public CorrelationScopeDecorator getObject() {
    if (builder == null) throw new NullPointerException("builder == null");
    if (fields != null) {
      builder.clearFields();
      for (BaggageField field : fields) {
        builder.addField(field);
      }
    }
    if (customizers != null) {
      for (CorrelationScopeCustomizer customizer : customizers) customizer.customize(builder);
    }
    return builder.build();
  }

  @Override public Class<? extends CorrelationScopeDecorator> getObjectType() {
    return CorrelationScopeDecorator.class;
  }

  @Override public boolean isSingleton() {
    return true;
  }

  public void setBuilder(CorrelationScopeDecorator.Builder builder) {
    this.builder = builder;
  }

  public void setFields(List<BaggageField> fields) {
    this.fields = fields;
  }

  public void setCustomizers(List<CorrelationScopeCustomizer> customizers) {
    this.customizers = customizers;
  }
}