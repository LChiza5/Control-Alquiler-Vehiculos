/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Lists;

import Modelo.Reserva;
import Utils.EntidadNoEncontradaException;
import java.util.ArrayList;

/**
 *
 * @author ilope
 */
public class ReservaLista implements List<Reserva>{
   private final java.util.List<Reserva> data = new ArrayList<>();

    @Override
    public boolean add(Reserva r) {
        return data.add(r);
    }

    @Override
    public boolean remove(Reserva r) {
        return r != null && data.remove(r);
    }

    @Override
    public Reserva find(Object id) {
        if (id == null) return null;
        int n;
        try { 
            n = (id instanceof Integer) ? (Integer) id : Integer.parseInt(id.toString()); 
        }
        catch (NumberFormatException e){ return null; }
        return data.stream().filter(x -> x.getId() == n).findFirst().orElse(null);
    }

    @Override
    public void showAll() {
        data.forEach(System.out::println);
    }

    // --- métodos útiles ---
    public java.util.List<Reserva> listar() {
        return data;
    }

    public void eliminarPorId(int id) throws EntidadNoEncontradaException {
        Reserva r = find(id);
        if (r == null) throw new EntidadNoEncontradaException("Reserva no encontrada");
        data.remove(r);
    } 
}
