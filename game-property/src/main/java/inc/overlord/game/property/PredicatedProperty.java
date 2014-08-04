/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.function.Predicate;

/**
 * Imposes a predicate on the input of a value.
 * @author achelian
 * @param <T>
 * @param <P>
 */
public interface PredicatedProperty<T, P extends Predicate<T>> extends Property<T> {
  /**
   * 
   * @return 
   */
  P getPredicate();
  void setPredicate(P value);

  @Override
  default void validate() {
    if (getPredicate() == null) {
      throw new IllegalStateException("predicate cannot be null");
    }
    if (!getPredicate().test(getValue())) {
      throw new IllegalStateException("value does not pass predicate");
    }
  }

  @Override
  @SuppressWarnings("UnnecessaryReturnStatement")
  default void vetoableChange(PropertyChangeEvent event) throws PropertyVetoException {
    switch (event.getPropertyName()) {
      case "value": {
        T newValue = (T) event.getNewValue();
        if (getPredicate().test(newValue)) {
          setValue(newValue);
        }
        else {
          throw new PropertyVetoException("value does not fulfill predicate", event);
        }
        return;
      }
      case "predicate": {
        P newValue = (P) event.getNewValue();
        if (newValue == null) {
          throw new PropertyVetoException("predicate cannot be null", event);
        }
        else if (!newValue.test(getValue())) {
          throw new PropertyVetoException("value would no longer validate", event);
        }
        else {
          setPredicate(newValue);
        }
        return;
      }
    }
  }

}
