/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Lists;

import Enums.EstadoVehiculo;
import Modelo.Vehiculo;
import Utils.EliminacionNoPermitidoException;
import Utils.EntidadNoEncontradaException;
import Utils.ReglaDeNegocioException;
import Utils.Validators;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ilope
 */
public class vehiculoList implements List<Vehiculo> {
    private final Map<String, Vehiculo> porPlaca = new HashMap<>();

  @Override
  public boolean add(Vehiculo v) {
        try {
            validarNuevo(v);
        } catch (ReglaDeNegocioException ex) {
            Logger.getLogger(vehiculoList.class.getName()).log(Level.SEVERE, null, ex);
        }
    if (porPlaca.containsKey(v.getPlaca())) try {
        throw new ReglaDeNegocioException("Placa duplicada: " + v.getPlaca());
        } catch (ReglaDeNegocioException ex) {
            Logger.getLogger(vehiculoList.class.getName()).log(Level.SEVERE, null, ex);
        }
    porPlaca.put(v.getPlaca(), v);
    return true;
  }

  @Override
  public boolean remove(Vehiculo v) {
    if (v == null) return false;
    if (v.getEstado() == EstadoVehiculo.EN_ALQUILER)
      try {
          throw new EliminacionNoPermitidoException("No se puede eliminar: vehículo en alquiler");
    } catch (EliminacionNoPermitidoException ex) {
        Logger.getLogger(vehiculoList.class.getName()).log(Level.SEVERE, null, ex);
    }
    return porPlaca.remove(v.getPlaca()) != null;
  }

  @Override
  public Vehiculo find(Object id) {
    return id == null ? null : porPlaca.get(String.valueOf(id));
  }

  @Override
  public void showAll() { porPlaca.values().forEach(System.out::println); }

  public void actualizar(String placa, String modelo, Enums.TipoVehiculo tipo, EstadoVehiculo estado) throws EntidadNoEncontradaException, ReglaDeNegocioException{
    var v = find(placa);
    if (v == null) throw new EntidadNoEncontradaException("Vehículo no encontrado");
    if (modelo != null){ Validators.noVacio(modelo,"modelo"); v.setModelo(modelo); }
    if (tipo   != null){ v.setTipo(tipo); }
    if (estado != null){ v.setEstado(estado); }
  }

  private void validarNuevo(Vehiculo v) throws ReglaDeNegocioException{
    if (v == null) throw new ReglaDeNegocioException("Vehículo nulo");
    Validators.noVacio(v.getPlaca(), "placa");
    Validators.noVacio(v.getMarca(), "marca");
    Validators.noVacio(v.getModelo(), "modelo");
    Validators.anioVehiculoValido(v.getAnio(), 20);
    if (v.getTipo() == null)   throw new ReglaDeNegocioException("Tipo de vehículo obligatorio");
    if (v.getEstado() == null) throw new ReglaDeNegocioException("Estado de vehículo obligatorio");
  }

  // utilidades
  public boolean existePlaca(String placa){ return porPlaca.containsKey(placa); }
  public Collection<Vehiculo> todos(){ return porPlaca.values(); }
}
   
    


