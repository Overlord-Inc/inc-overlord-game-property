/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import javax.annotation.PostConstruct;

/**
 *
 * @param <T>
 * @author achelian
 */
public interface Property<T> extends VetoableChangeListener {
  /**
   * The contained property value.
   * @return The property value.
   */
  T getValue();
  /**
   * The value to set. It is not guaranteed to be set.
   * @param value The value to set.
   */
  void setValue(T value);
  /**
   * This is intended to make sure the property is in a good state.
   */
  @PostConstruct
  void validate();
  /**
   * The controllers should fire a vetoable property change. If it is vetoed,
   * then handle it, otherwise, it will propagate as a change.
   * @see 
   * @param event
   * @throws PropertyVetoException 
   */
  @Override
  default void vetoableChange(PropertyChangeEvent event) throws PropertyVetoException {
    if ("value".equals(event.getPropertyName())) {
      T newValue = (T) event.getNewValue();
      setValue(newValue);
    }
  }
  /**
   * Set the listeners who will be informed if anything changes. The views
   * will tie into this.
   * @param listener 
   */
  void addPropertyChangeListener(PropertyChangeListener listener);
  /**
   * Remove a listener. Use this when a view is disassociated from this model.
   * @param listener 
   */
  void removePropertyChangeListener(PropertyChangeListener listener);
}
