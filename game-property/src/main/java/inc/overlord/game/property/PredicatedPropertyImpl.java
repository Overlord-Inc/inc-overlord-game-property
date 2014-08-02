/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.util.function.Predicate;
import javax.annotation.PostConstruct;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 *
 * @author achelian
 * @param <T>
 */
@EqualsAndHashCode(callSuper = true)
public class PredicatedPropertyImpl<T, P extends Predicate<T>> extends PropertyImpl<T> implements PredicatedProperty<T, P> {
  @Getter
  P predicate;

  @Override
  public void setPredicate(P value) {
    if (value == null) {
      return;
    }
    if (!isCreationMode() && !value.test(this.value)) {
      // once consistent, do not let it become inconsistent
      return;
    }
    Predicate<T> oldValue = this.predicate;
    this.predicate = value;
    if (!isCreationMode()) {
      firePropertyChange("predicate", oldValue, value);
    }
  }

  @Override
  public void setValue(T value) {
    if (!isCreationMode() && !predicate.test(value)) {
      return;
    }
    super.setValue(value);
  }

  @PostConstruct
  @Override
  public void validate() {
    PredicatedProperty.super.validate();
    super.validate();
  }
}
