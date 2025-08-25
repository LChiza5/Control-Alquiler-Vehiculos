/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Lists;

import Modelo.Cliente;
import java.util.ArrayList;

/**
 *
 * @author ilope
 */
public class clienteList implements List<Cliente> {
    private final java.util.List<Cliente> data = new ArrayList<>();

    @Override
    public boolean add(Cliente c) {
        return data.add(c);
    }

    @Override
    public boolean remove(Cliente c) {
        return data.remove(c);
    }

    @Override
    public Cliente find(Object id) {
        return data.stream()
        .filter(cli -> cli.getCedula().equals(id))
        .findFirst()
        .orElse(null);
    }

    @Override
    public void showAll() {
        data.forEach(System.out::println);
    }
}

