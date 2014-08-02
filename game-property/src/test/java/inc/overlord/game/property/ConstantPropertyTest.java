/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 *
 * @author achelian
 */
public class ConstantPropertyTest {
  ConstantProperty<String> property;
  @Mock PropertyChangeListener listener;
  
  public ConstantPropertyTest() {
  }
  
  @Before
  public void setUp() {
    property = new ConstantProperty<>("foo");
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSetValueThrowsException() {
    property.setValue("bar");
  }

  @Test(expected = PropertyVetoException.class)
  public void testVetoableChangeThrowsException() throws PropertyVetoException {
    property.vetoableChange(new PropertyChangeEvent(this, "value", "foo", "bar"));
  }

  @Test
  public void testGetValue() {
    assertEquals("foo", property.getValue());
  }

  @Test
  public void testValidate() {
    // should do nothing
    property.validate();
    assertEquals("foo", property.getValue());
  }
}
