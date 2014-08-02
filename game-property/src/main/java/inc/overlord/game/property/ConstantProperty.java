/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import javax.annotation.PostConstruct;
import lombok.Delegate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author achelian
 * @param <T>
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude = {"propertyChangeSupport"})
public class ConstantProperty<T> implements Property<T> {
  @Delegate final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  @Getter
  final T value;

  @Override
  public void setValue(T value) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @PostConstruct
  @Override
  public void validate() {
  }

  @Override
  public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
    throw new PropertyVetoException("This is a constant property, no changes allowed", evt);
  }
  
}
