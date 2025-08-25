/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Lists;

/**
 *
 * @author ilope
 */
public interface List<T> {
    boolean add(T t);
    boolean remove(T t);
    T find(Object id);
    void showAll();
}