/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.beans.PropertyChangeSupport;
import java.beans.Transient;
import java.beans.VetoableChangeListener;
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
public class PropertyImpl<T> implements Property<T>, VetoableChangeListener {
  @Delegate
  final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  @Getter
  T value;
  @Getter(onMethod = @_(@Transient))
  boolean creationMode = true;

  @Override
  public void setValue(T value) {
    T oldValue = this.value;
    this.value = value;
    if (!isCreationMode()) {
      firePropertyChange("value", oldValue, this.value);
    }
  }

  @PostConstruct
  @Override
  public void validate() {
    creationMode = false;
  }
}
