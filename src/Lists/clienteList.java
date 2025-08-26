/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Lists;

import Modelo.Cliente;
import Utils.EntidadConDependenciaException;
import Utils.EntidadNoEncontradaException;
import Utils.ReglaDeNegocioException;
import Utils.Validators;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ilope
 */
public class clienteList implements List<Cliente> {
   private final java.util.List<Cliente> data = new ArrayList<>();

  // --- List<T> ---
  @Override
  public boolean add(Cliente c) {
       try {
           validarNuevo(c);
       } catch (ReglaDeNegocioException ex) {
           Logger.getLogger(clienteList.class.getName()).log(Level.SEVERE, null, ex);
       }
    if (find(c.getCedula()) != null) try {
        throw new ReglaDeNegocioException("Ya existe cliente con cédula " + c.getCedula());
       } catch (ReglaDeNegocioException ex) {
           Logger.getLogger(clienteList.class.getName()).log(Level.SEVERE, null, ex);
       }
    return data.add(c);
  }

  @Override
  public boolean remove(Cliente c) {
    // Regla: no eliminar si tiene reservas activas
    if (c == null) return false;
    if (tieneReservasActivas(c.getCedula())) {
      throw new RuntimeException("Use EntidadConDependenciasException"); // por si llaman remove() directo
    }
    return data.remove(c);
  }

  @Override
  public Cliente find(Object id) {
    if (id == null) return null;
    String ced = String.valueOf(id);
    return data.stream().filter(x -> x.getCedula().equals(ced)).findFirst().orElse(null);
  }

  @Override
  public void showAll() { data.forEach(System.out::println); }

  // --- Operaciones cómodas ---
  public void actualizarContacto(String cedula, String telefono, String correo, String licencia) throws EntidadNoEncontradaException, ReglaDeNegocioException {
    var c = find(cedula);
    if (c == null) throw new EntidadNoEncontradaException("Cliente no encontrado");
    if (telefono != null) { Validators.telefono(telefono, 8); c.setTelefono(telefono); }
    if (correo   != null) { Validators.email(correo);         c.setCorreo(correo); }
    if (licencia != null) { Validators.noVacio(licencia,"licencia"); c.setLicencia(licencia); }
  }

  public void eliminarPorCedula(String cedula) throws EntidadConDependenciaException, EntidadNoEncontradaException {
    var c = find(cedula);
    if (c == null) throw new EntidadNoEncontradaException("Cliente no encontrado");
    if (tieneReservasActivas(cedula)) {
      throw new EntidadConDependenciaException("No se puede eliminar: cliente con reservas activas");
    }
    data.remove(c);
  }

  // --- Validaciones de alta ---
  private void validarNuevo(Cliente c) throws ReglaDeNegocioException{
    if (c == null) throw new ReglaDeNegocioException("Cliente nulo");
    Validators.noVacio(c.getCedula(), "cédula");
    Validators.noVacio(c.getNombre(), "nombre");
    Validators.mayorDeEdad(c.getFechaNac());
    Validators.telefono(c.getTelefono(), 8);
    Validators.email(c.getCorreo());
    Validators.noVacio(c.getLicencia(), "licencia");
  }

  // Hook: conéctalo en Día 3 contra reservas reales
  protected boolean tieneReservasActivas(String cedula){
    return false; // TODO: consultar módulo de reservas
  }

  public java.util.List<Cliente> listar(){ return data; }
}

