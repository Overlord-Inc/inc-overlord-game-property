/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;

/**
 * Imposes a constraint on the output of the value. The value 
 * @author achelian
 * @param <T>
 * @param <C>
 */
public interface ConstrainedProperty<T, C extends Constraint<T>> extends Property<T> {
  C getConstraint();
  void setConstraint(C value);

  @Override
  default void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
    switch (evt.getPropertyName()) {
      case "constraint": {
        if (Constraint.class.isInstance(evt.getNewValue())) {
          setConstraint((C) evt.getNewValue());
        }
        else {
          throw new PropertyVetoException("constraint must be a Constraint and not null", evt);
        }
        break;
      }
      case "unconstrainedValue":
      case "value": {
        setValue((T) evt.getNewValue());
        break;
      }
    }
  }

  @Override
  default void validate() {
    if (getConstraint() == null) {
      throw new IllegalStateException("constraint cannot be null");
    }
  }
}
