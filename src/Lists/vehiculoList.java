/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Lists;

import Enums.EstadoVehiculo;
import Modelo.Vehiculo;
import Utils.EntidadNoEncontradaException;
import Utils.ReglaDeNegocioException;
import Utils.Validators;
import java.util.Collection;
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
    try {
        validarNuevo(v);
        if (porPlaca.containsKey(v.getPlaca())) {
            throw new ReglaDeNegocioException("Placa duplicada: " + v.getPlaca());
        }
        porPlaca.put(v.getPlaca(), v);
        return true;
    } catch (ReglaDeNegocioException ex) {
        // No imprimir en consola: la vista/controlador debe mostrar el mensaje
        return false;
    }
}

// 2) remove(...)
@Override
public boolean remove(Vehiculo v) {
    if (v == null) return false;
    if (v.getEstado() == EstadoVehiculo.EN_ALQUILER) {
        // No se permite eliminar si está en alquiler
        return false;
    }
    return porPlaca.remove(v.getPlaca()) != null;
}

// 3) showAll()
@Override
public void showAll() {
    // No imprimir en consola. La UI debe encargarse de mostrar datos.
}


  public void actualizar(String placa, Enums.TipoVehiculo modelo, EstadoVehiculo estado) throws EntidadNoEncontradaException, ReglaDeNegocioException{
    var v = find(placa);
    if (v == null) throw new EntidadNoEncontradaException("Vehículo no encontrado");
    if (modelo != null) { 
    v.setModelo(modelo); 
    } else {
    throw new ReglaDeNegocioException("El campo 'modelo' es obligatorio.");
    }
    if (estado != null){ v.setEstado(estado); }
  }

  private void validarNuevo(Vehiculo v) throws ReglaDeNegocioException{
    if (v == null) throw new ReglaDeNegocioException("Vehículo nulo");
    Validators.noVacio(v.getPlaca(), "placa");
    Validators.noVacio(v.getMarca(), "marca");
    Validators.anioVehiculoValido(v.getAnio(), 20);
    if (v.getModelo() == null)   throw new ReglaDeNegocioException("Tipo de vehículo obligatorio");
    if (v.getEstado() == null) throw new ReglaDeNegocioException("Estado de vehículo obligatorio");
  }

  // utilidades
  public boolean existePlaca(String placa){ return porPlaca.containsKey(placa); }
  public Collection<Vehiculo> todos(){ return porPlaca.values(); }

    @Override
    public Vehiculo find(Object id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
   
    


