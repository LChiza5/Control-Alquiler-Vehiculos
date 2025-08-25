/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Lists;

import Modelo.Empleado;
import java.util.ArrayList;

/**
 *
 * @author ilope
 */
public class empleadoList implements List<Empleado> {
    private final java.util.List<Empleado> data = new ArrayList<>();

    @Override
    public boolean add(Empleado e) {
        return data.add(e);
    }

    @Override
    public boolean remove(Empleado e) {
        return data.remove(e);
    }

    @Override
    public Empleado find(Object id) {
        return data.stream()
        .filter(emp -> emp.getCedula().equals(id))
        .findFirst()
        .orElse(null);
    }

    @Override
    public void showAll() {
        data.forEach(System.out::println);
    }

    //Exponer lista para mostrar en tabla
    public java.util.List<Empleado> listar() { return data; }
}
