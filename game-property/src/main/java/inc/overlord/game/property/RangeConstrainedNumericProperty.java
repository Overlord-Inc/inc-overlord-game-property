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
public class RangeConstrainedNumericProperty<T extends Number & Comparable> extends ConstrainedPropertyImpl<T, Range<T>> {

  @Override
  public void validate() {
    super.validate();
    if (this.value == null) {
      throw new IllegalStateException("value cannot be null");
    }
  }

  @Override
  public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
    switch (evt.getPropertyName()) {
      case "constraint": {
        if (Range.class.isInstance(evt.getNewValue())) {
          setConstraint((Range<T>) evt.getNewValue());
        }
        else {
          throw new PropertyVetoException("constraint must be a Range and not null", evt);
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
