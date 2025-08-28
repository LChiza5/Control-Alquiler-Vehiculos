/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Lists;

import Modelo.Contrato;
import java.util.ArrayList;

/**
 *
 * @author ilope
 */
public class ContratoList implements List<Contrato> {
  private final java.util.List<Contrato> data = new ArrayList<>();

    @Override 
    public boolean add(Contrato c) {
        return data.add(c); 
    }
    @Override
    public boolean remove(Contrato c) {
        return c != null && data.remove(c); 
    }
    @Override 
    public Contrato find(Object id) {
    if (id == null) return null;
    int n;
    try { n = (id instanceof Integer) ? (Integer) id : Integer.parseInt(id.toString()); }
    catch (NumberFormatException e){ return null; }
    for (Contrato c : data) if (c.getId() == n) return c;
    return null;
  }
  @Override public void showAll(){ 
  
  }

  public java.util.List<Contrato> listar(){ return data; }
}