/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.beans.PropertyChangeEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.mockito.ArgumentMatcher;

/**
 *
 * @author achelian
 */
@RequiredArgsConstructor @ToString
public class PropertyChangeEvtMatcher extends ArgumentMatcher<PropertyChangeEvent> {
  @NonNull
  final String propertyName;
  final int oldValue, newValue;
  @Override
  public boolean matches(Object argument) {
    if (argument instanceof PropertyChangeEvent) {
      PropertyChangeEvent event = (PropertyChangeEvent) argument;
      return propertyName.equals(event.getPropertyName())
              && event.getOldValue() != null
              && event.getNewValue() != null
              && event.getOldValue() instanceof Integer
              && event.getNewValue() instanceof Integer
              && oldValue == (Integer) event.getOldValue()
              && newValue == (Integer) event.getNewValue();
    }
    return false;
  }
  
}
