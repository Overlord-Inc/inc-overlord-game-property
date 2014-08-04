/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.beans.Transient;
import javax.annotation.PostConstruct;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 *
 * @author achelian
 * @param <T>
 * @param <C>
 */
@EqualsAndHashCode(callSuper = true)
public class ConstrainedPropertyImpl<T, C extends Constraint<T>> extends PropertyImpl<T> implements ConstrainedProperty<T, C> {
  @Getter
  C constraint;

  @Override
  public void setConstraint(@NonNull C value) {
    C oldValue = constraint;
    constraint = value;
    if (!isCreationMode()) {
      firePropertyChange("constraint", oldValue, value);
      firePropertyChange("value", oldValue.constrain(this.value), value.constrain(this.value));
    }
  }

  @Override
  @PostConstruct
  public void validate() {
    ConstrainedProperty.super.validate();
    super.validate();
  }

  @Override
  @Transient
  public T getValue() {
    return getConstraint().constrain(super.getValue());
  }

  public T getUnconstrainedValue() {
    return value;
  }

  public void setUnconstrainedValue(T value) {
    setValue(value);
  }

  @Override
  public void setValue(T value) {
    T oldValue = (!isCreationMode()) ? getConstraint().constrain(this.value) : this.value;
    T oldUnconstrainedValue = this.value;
    this.value = value;
    if (!isCreationMode()) {
      firePropertyChange("unconstrainedValue", oldUnconstrainedValue, value);
      firePropertyChange("value", oldValue, getConstraint().constrain(value));
    }
  }
}
