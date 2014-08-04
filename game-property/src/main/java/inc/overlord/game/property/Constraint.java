/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

/**
 *
 * @author achelian
 */
public interface Constraint<T> {
  T constrain(T input);
}
