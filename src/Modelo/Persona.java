/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 *
 * @author ilope
 */
public abstract class Persona {
    protected String cedula;
    protected String nombre;
    protected LocalDate fechaNac;
    protected String telefono;
    protected String correo;
    
    public int getEdad() {
    return fechaNac == null ? 0 : Period.between
        (fechaNac, LocalDate.now()).getYears();
    } 

    public String getCedula() {
        return cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDate getFechaNac() {
        return fechaNac;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Persona(String cedula, String nombre, LocalDate fechaNac, String telefono, String correo) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.fechaNac = fechaNac;
        this.telefono = telefono;
        this.correo = correo;
    }
    
    @Override public boolean equals(Object o){
    return o instanceof Persona p && Objects.equals(cedula, p.cedula);
    }
  
    @Override public int hashCode(){ 
      return Objects.hash(cedula); 
    }
  
  
    @Override public String toString(){
      return "%s{cedula='%s', nombre='%s'}".formatted(
      getClass().getSimpleName(), cedula, nombre);
    }
}