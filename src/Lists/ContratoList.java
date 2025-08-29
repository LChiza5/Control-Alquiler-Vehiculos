/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Lists;

import Modelo.Contrato;
import Utils.EntidadNoEncontradaException;
import java.util.ArrayList;

/**
 *
 * @author ilope
 */
public class ContratoList implements List<Contrato> {
    private final java.util.List<Contrato> data = new ArrayList<>();

    @Override
    public boolean add(Contrato c) { return data.add(c); }

    @Override
    public boolean remove(Contrato c) { return c != null && data.remove(c); }

    @Override
    public Contrato find(Object id) {
        if (id == null) return null;
        int n;
        try { n = (id instanceof Integer) ? (Integer) id : Integer.parseInt(id.toString()); }
        catch (NumberFormatException e) { return null; }
        return data.stream().filter(x -> x.getId() == n).findFirst().orElse(null);
    }

    @Override
    public void showAll() {}

    // Utilidades
    public java.util.List<Contrato> listar() { return data; }

    public void eliminarPorId(int id) throws EntidadNoEncontradaException {
        Contrato c = find(id);
        if (c == null) throw new EntidadNoEncontradaException("Contrato no encontrado");
        data.remove(c);
    }
}