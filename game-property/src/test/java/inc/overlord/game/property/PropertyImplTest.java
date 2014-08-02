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
import org.mockito.Mockito;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 *
 * @author achelian
 */
public class PropertyImplTest {
  PropertyImpl<String> property;
  @Mock PropertyChangeListener listener;

  @Before
  public void setUp() {
    initMocks(this);
    property = new PropertyImpl<>();
    property.addPropertyChangeListener(listener);
  }

  @Test
  public void testValidate() {
    assertTrue(property.isCreationMode());
    property.validate();
    assertFalse(property.isCreationMode());
    assertNull(property.getValue());
  }

  @Test
  public void testSetValue() {
    property.setValue("foo");
    verifyZeroInteractions(listener);
    assertEquals("foo", property.getValue());
  }

  @Test
  public void testSetValueAfterValidation() {
    property.validate();
    property.setValue("bar");
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", null, "bar")));
    verifyNoMoreInteractions(listener);
    assertEquals("bar", property.getValue());
  }

  @Test
  public void testRemovePropertyListener() {
    assertTrue(property.hasListeners(null));
    property.removePropertyChangeListener(listener);
    assertFalse(property.hasListeners(null));
  }

  @Test
  public void testVetoableChangeOfValue() throws PropertyVetoException {
    property.validate();
    property.vetoableChange(new PropertyChangeEvent(this, "value", null, "bar"));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", null, "bar")));
    verifyNoMoreInteractions(listener);
  }

  @Test
  public void testVetoableChangeOfSomething() throws PropertyVetoException {
    property.validate();
    property.vetoableChange(new PropertyChangeEvent(this, "something", null, "foo"));
    verifyZeroInteractions(listener);
  }
}
