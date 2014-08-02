/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.util.function.Predicate;

/**
 *
 * @author achelian
 * @param <T>
 */
public class NonNullPredicate<T> implements Predicate<T> {

  @Override
  public boolean test(T t) {
    return (t != null);
  }

}
