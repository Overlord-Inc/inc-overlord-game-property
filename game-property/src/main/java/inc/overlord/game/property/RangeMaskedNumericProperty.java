/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.Transient;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 *
 * @author achelian
 * @param <T>
 */
@EqualsAndHashCode(callSuper = true)
public class RangeMaskedNumericProperty<T extends Number & Comparable> extends PredicatedPropertyImpl<T, RangePredicate<T>> {
  @Override
  @Transient
  public T getValue() {
    return getPredicate().trim(value);
  }

  @Override
  public void setValue(T value) {
    T oldValue = (!isCreationMode()) ? getPredicate().trim(this.value) : this.value;
    this.value = value;
    if (!isCreationMode()) {
      firePropertyChange("value", oldValue, getPredicate().trim(value));
    }
  }

  @Override
  public void setPredicate(@NonNull RangePredicate<T> value) {
    RangePredicate<T> oldPredicate = this.predicate;
    predicate = value;
    if (!isCreationMode()) {
      firePropertyChange("predicate", oldPredicate, value);
      firePropertyChange("value", oldPredicate.trim(this.value), value.trim(this.value));
    }
  }

  @Override
  public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
    switch (evt.getPropertyName()) {
      case "predicate": {
        if (RangePredicate.class.isInstance(evt.getNewValue())) {
          setPredicate((RangePredicate<T>) evt.getNewValue());
        }
        else {
          throw new PropertyVetoException("predicate must be a RangePredicate and not null", evt);
        }
        break;
      }
      case "value": {
        if (evt.getNewValue() != null) {
          setValue((T) evt.getNewValue());
        }
        else {
          throw new PropertyVetoException("value cannot be null", evt);
        }
        break;
      }
    }
  }
}
