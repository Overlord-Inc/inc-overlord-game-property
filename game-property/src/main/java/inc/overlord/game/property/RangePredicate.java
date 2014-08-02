/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.function.Predicate;
import javax.annotation.PostConstruct;
import lombok.Delegate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Implicitly not null.
 * @author achelian
 * @param <T>
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude = {"propertyChangeSupport"})
public class RangePredicate<T extends Comparable> implements Predicate<T>, VetoableChangeListener {
  @Delegate
  final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  @Getter
  T min, max;
  @Getter
  boolean creationMode = true;

  @SuppressWarnings("OverridableMethodCallInConstructor")
  public RangePredicate(T min, T max) {
    this.min = min;
    this.max = max;
  }

  public void setMax(T value) {
    if (value == null) {
      return;
    }
    if (!creationMode && min.compareTo(value) > 0) {
      return;
    }
    T oldValue = max;
    max = value;
    if (!creationMode) {
      firePropertyChange("max", oldValue, value);
    }
  }

  public void setMin(T value) {
    if (null == value) {
      return;
    }
    if (!creationMode && max.compareTo(value) < 0) {
      return;
    }
    T oldValue = min;
    min = value;
    if (!creationMode) {
      firePropertyChange("min", oldValue, value);
    }
  }

  public T trim(@NonNull T value) {
    if (value.compareTo(max) > 0) {
      return max;
    }
    else if (value.compareTo(min) < 0) {
      return min;
    }
    else {
      return value;
    }
  }

  @PostConstruct
  public void validate() {
    if (min == null) {
      throw new IllegalStateException("min is null");
    }
    if (max == null) {
      throw new IllegalStateException("max is null");
    }
    if (min.compareTo(max) > 0) {
      throw new IllegalStateException(String.format("min[%s] > max[%s]", min.toString(), max.toString()));
    }
    creationMode = false;
  }

  @Override
  @SuppressWarnings("UnnecessaryReturnStatement")
  public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
    switch (evt.getPropertyName()) {
      case "max": {
        T newValue = (T) evt.getNewValue();
        if (newValue.compareTo(min) < 0) {
          throw new PropertyVetoException(String.format("max[%s] < min[%s]", max.toString(), min.toString()), evt);
        }
        else {
          setMax(newValue);
        }
        return;
      }
      case "min": {
        T newValue = (T) evt.getNewValue();
        if(newValue.compareTo(max) > 0) {
          throw new PropertyVetoException(String.format("min[%s] > max[%s]", min.toString(), max.toString()), evt);
        }
        else {
          setMin(newValue);
        }
        return;
      }
    }
  }

  @Override
  public boolean test(T t) {
    if (t == null) {
      return false;
    }
    if (t.compareTo(max) > 0 || t.compareTo(min) < 0) {
      return false;
    }
    return true;
  }
  
}
