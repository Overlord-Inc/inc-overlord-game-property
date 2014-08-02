/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.util.function.Predicate;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author achelian
 * @param <T>
 */
@RequiredArgsConstructor
@Getter
public class TypeEnforcingPredicate<T> implements Predicate<T> {
  @NonNull
  final Class<T> type;
  final boolean nullAllowed;

  @Override
  public boolean test(T t) {
    return (t == null && nullAllowed) || type.isInstance(t);
  }
}
