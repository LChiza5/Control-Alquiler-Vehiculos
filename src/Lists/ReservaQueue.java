/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Lists;

import Modelo.Reserva;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author ilope
 */
public class ReservaQueue implements List<Reserva> {

    private final Queue<Reserva> espera = new LinkedList<>();

    @Override
    public boolean add(Reserva r) { return espera.add(r); }

    @Override
    public boolean remove(Reserva r) { return espera.remove(r); }

    @Override
    public Reserva find(Object id) {
        if (id == null) return null;
        int n;
        try {
            n = (id instanceof Integer) ? (Integer) id : Integer.parseInt(id.toString());
        } catch (NumberFormatException e) {
            return null;
        }
        for (Reserva r : espera) {
            if (r.getId() == n) return r;
        }
        return null;
    }

    @Override
    public void showAll() { /* opcional mostrar en consola */ }

    // Helpers
    public int size(){ return espera.size(); }
    public boolean isVacia(){ return espera.isEmpty(); }
    public Reserva siguiente(){ return espera.poll(); }

    /** Snapshot de la cola sin modificar su orden. */
    public java.util.List<Reserva> toList() {
        return new java.util.ArrayList<>(espera);
    }
}