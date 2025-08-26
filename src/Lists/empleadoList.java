/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Lists;

import Modelo.Empleado;
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
public class empleadoList implements List<Empleado> {
      private final java.util.List<Empleado> data = new ArrayList<>();

    @Override
    public boolean add(Empleado e) {
          try {
              validarNuevo(e);
          } catch (ReglaDeNegocioException ex) {
              Logger.getLogger(empleadoList.class.getName()).log(Level.SEVERE, null, ex);
          }
        if (find(e.getCedula()) != null) {
              try {
                  throw new ReglaDeNegocioException("Empleado duplicado (cédula)");
              } catch (ReglaDeNegocioException ex) {
                  Logger.getLogger(empleadoList.class.getName()).log(Level.SEVERE, null, ex);
              }
        }
        return data.add(e);
    }

    @Override
    public boolean remove(Empleado e) {
        return e != null && data.remove(e); // sin restricciones según guía
    }

    @Override
    public Empleado find(Object id) {
        if (id == null) return null;
        String ced = String.valueOf(id);
        return data.stream()
                   .filter(x -> x.getCedula().equals(ced))
                   .findFirst()
                   .orElse(null);
    }

    @Override
    public void showAll() {
        data.forEach(System.out::println);
    }

    public void actualizar(String cedula, String telefono, String correo, String puesto) throws ReglaDeNegocioException, EntidadNoEncontradaException{
        Empleado e = find(cedula); // ← usa tipo explícito (no var)
        if (e == null) throw new EntidadNoEncontradaException("Empleado no encontrado");

        if (telefono != null){ Validators.telefono(telefono, 8); e.setTelefono(telefono); }
        if (correo   != null){ Validators.email(correo);         e.setCorreo(correo); }
        if (puesto   != null){ Validators.noVacio(puesto,"puesto"); e.setPuesto(puesto); }
    }

    private void validarNuevo(Empleado e) throws ReglaDeNegocioException{
        if (e == null) throw new ReglaDeNegocioException("Empleado nulo");
        Validators.noVacio(e.getCedula(), "cédula");
        Validators.noVacio(e.getNombre(), "nombre");
        Validators.mayorDeEdad(e.getFechaNac());
        Validators.telefono(e.getTelefono(), 8);
        Validators.email(e.getCorreo());
        Validators.noVacio(e.getPuesto(), "puesto");
    }

    public java.util.List<Empleado> listar(){ return data; }
}

