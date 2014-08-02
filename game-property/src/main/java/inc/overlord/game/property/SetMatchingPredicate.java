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
import static java.util.Collections.emptySet;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.PostConstruct;
import lombok.Delegate;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 *
 * @author achelian
 * @param <T>
 */
@EqualsAndHashCode(callSuper = false, exclude = {"propertyChangeSupport"})
public class SetMatchingPredicate<T> implements Predicate<T>, VetoableChangeListener {
  @Delegate
  final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  @Getter
  Set<T> matches = emptySet();
  @Getter
  boolean creationMode = true;

  public void setMatches(Set<T> value) {
    if (!creationMode && value == null) {
      return;
    }
    Set<T> oldValue = matches;
    matches = value;
    if (!creationMode) {
      firePropertyChange("matches", oldValue, value);
    }
  }

  @Override
  public boolean test(T t) {
    if (t == null) {
      return false;
    }
    return matches.contains(t);
  }

  @PostConstruct
  public void validate() {
    if (matches == null) {
      throw new IllegalStateException("matches cannot be null");
    }
    creationMode = false;
  }

  @Override
  public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
    if ("matches".equals(evt.getPropertyName())) {
      if (Set.class.isInstance(evt.getNewValue())) {
        setMatches((Set<T>) evt.getNewValue());
      }
      else {
        throw new PropertyVetoException("The new value must be non null instance of Set", evt);
      }
    }
  }
}
