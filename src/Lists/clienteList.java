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


/**
 *
 * @author ilope
 */
public class clienteList implements List<Cliente> {
  private final ArrayList<Cliente> data = new ArrayList<>();

    @Override
    public boolean add(Cliente c) {
    try {
        validarNuevo(c);
        if (find(c.getCedula()) != null)
            throw new ReglaDeNegocioException("Ya existe cliente con cédula " + c.getCedula());
        return data.add(c);
    } catch (ReglaDeNegocioException ex) {
        
        return false;
    }
}

    @Override
    public boolean remove(Cliente c) {
    try {
        validarDependencias(c);
        return data.remove(c);
    } catch (EntidadConDependenciaException ex) {
        
        return false;
    }
}
    @Override
    public Cliente find(Object id) {
        if (id == null) return null;
        String ced = String.valueOf(id);
        return data.stream().filter(x -> x.getCedula().equals(ced)).findFirst().orElse(null);
    }

    @Override
    public void showAll() {
        
    }

    // --- Métodos adicionales ---
    public void actualizarContacto(String cedula, String telefono, String correo, String licencia) 
            throws EntidadNoEncontradaException, ReglaDeNegocioException {
        Cliente c = find(cedula);
        if (c == null) throw new EntidadNoEncontradaException("Cliente no encontrado");
        if (telefono != null) { Validators.telefono(telefono, 8); c.setTelefono(telefono); }
        if (correo != null)   { Validators.email(correo); c.setCorreo(correo); }
        if (licencia != null) { Validators.noVacio(licencia,"licencia"); c.setLicencia(licencia); }
    }

    public void eliminarPorCedula(String cedula) 
            throws EntidadConDependenciaException, EntidadNoEncontradaException {
        Cliente c = find(cedula);
        if (c == null) throw new EntidadNoEncontradaException("Cliente no encontrado");
        if (tieneReservasActivas(cedula))
            throw new EntidadConDependenciaException("No se puede eliminar: cliente con reservas activas");
        data.remove(c);
    }

    public java.util.List<Cliente> listar() {
        return new ArrayList<>(data); // devuelvo copia para proteger data
    }

    // --- Validaciones ---
    private void validarNuevo(Cliente c) throws ReglaDeNegocioException {
        if (c == null) throw new ReglaDeNegocioException("Cliente nulo");
        Validators.noVacio(c.getCedula(), "cédula");
        Validators.noVacio(c.getNombre(), "nombre");
        Validators.mayorDeEdad(c.getFechaNac());
        Validators.telefono(c.getTelefono(), 8);
        Validators.email(c.getCorreo());
        Validators.noVacio(c.getLicencia(), "licencia");
    }

    protected void validarDependencias(Cliente c) throws EntidadConDependenciaException {
        if (tieneReservasActivas(c.getCedula())) {
            throw new EntidadConDependenciaException("Cliente con reservas activas");
        }
    }

    protected boolean tieneReservasActivas(String cedula) {
        return false; // TODO: conectar con reservas
    }
    public Modelo.Cliente find(String cedula) {
    if (cedula == null) return null;
    for (Modelo.Cliente c : data) {
        if (cedula.equals(c.getCedula())) return c;
    }
    return null;
    }
    public java.util.List<Cliente> todos() {
    return new java.util.ArrayList<>(data);
}
    
}