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
public class PropertyChangeEvtGenericMatcher extends ArgumentMatcher<PropertyChangeEvent> {
  @NonNull
  final String propertyName;
  final Object oldValue, newValue;

  @Override
  public boolean matches(Object argument) {
    if (argument instanceof PropertyChangeEvent) {
      PropertyChangeEvent event = (PropertyChangeEvent) argument;
      return propertyName.equals(event.getPropertyName()) &&
              ((oldValue == null && event.getOldValue() == null) ||
                (oldValue.equals(event.getOldValue()))) &&
              ((newValue == null && event.getNewValue() == null) ||
                (newValue.equals(event.getNewValue())));
    }
    return false;
  }
  
}
