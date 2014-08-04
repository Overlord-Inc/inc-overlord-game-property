/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.annotation.PostConstruct;
import lombok.NonNull;

/**
 * This is a numeric property that is forced to be within a range. It expects
 * a range predicate that can truncate the value.
 * @author achelian
 * @param <T> A java numeric type.
 */
public class RangeCoercedNumericProperty<T extends Number & Comparable> extends PredicatedPropertyImpl<T, Range<T>> implements PropertyChangeListener {
  @Override
  public void setPredicate(@NonNull Range<T> value) {
    Range<T> oldPredicate = predicate;
    if (oldPredicate != null) {
      ((Range<T>) oldPredicate).removePropertyChangeListener(this);
    }
    predicate = value;
    value.addPropertyChangeListener(this);
    if (!creationMode) {
      firePropertyChange("predicate", oldPredicate, value);
      setValue(value.constrain(this.value));
    }
  }

  @Override
  @SuppressWarnings("UnnecessaryReturnStatement")
  public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
    switch (evt.getPropertyName()) {
      case "predicate": {
        if (Range.class.isInstance(evt.getNewValue())) {
          Range<T> rangePredicate = Range.class.cast(evt.getNewValue());
          setPredicate(rangePredicate);
        }
        else {
          throw new PropertyVetoException("predicate must be rangePredicate", evt);
        }
        return;
      }
      case "value": {
        T newValue = (T) evt.getNewValue();
        if (!predicate.test(newValue)) {
          throw new PropertyVetoException("value does not fulfill predicate", evt);
        }
        else {
          setValue(newValue);
        }
        return;
      }
    }
  }

  @PostConstruct
  @Override
  public void validate() {
    if (value != null && predicate != null) {
      value = predicate.constrain(value);
    }
    super.validate();
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource() == predicate) {
      switch (evt.getPropertyName()) {
        case "max": {
          T newValue = (T) evt.getNewValue();
          if (value.compareTo(newValue) > 0) {
            setValue(newValue);
          }
          break;
        }
        case "min": {
          T newValue = (T) evt.getNewValue();
          if (value.compareTo(newValue) < 0) {
            setValue(newValue);
          }
          break;
        }
      }
    }
  }
}
