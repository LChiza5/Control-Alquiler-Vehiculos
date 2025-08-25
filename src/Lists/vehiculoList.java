/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Lists;

import Modelo.Vehiculo;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ilope
 */
public class vehiculoList implements List<Vehiculo> {
    private final Map<String, Vehiculo> porPlaca = new HashMap<>();

    @Override
    public boolean add(Vehiculo v) {
        if (porPlaca.containsKey(v.getPlaca())) return false; // evita duplicados
        porPlaca.put(v.getPlaca(), v);
        return true;
    }

    @Override
    public boolean remove(Vehiculo v) {
        return porPlaca.remove(v.getPlaca()) != null;
    }

    @Override
    public Vehiculo find(Object id) {
        return porPlaca.get(String.valueOf(id));
    }

    @Override
    public void showAll() {
        porPlaca.values().forEach(System.out::println);
    }

    // (Opcional) utilidades extra
    public boolean existePlaca(String placa){ 
        return porPlaca.containsKey(placa);
    }
    public java.util.Collection<Vehiculo> todos(){ 
        return porPlaca.values(); 
    }
}
   
    


