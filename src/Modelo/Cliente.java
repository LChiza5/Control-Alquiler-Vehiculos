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
public class Cliente {
    private String cedula;
    private String nombre;
    private LocalDate fechaNacimiento; //yyyy-MM-dd  
    private String telefono; // CR: 8 dígitos           
    private String correo;
    private String licencia;

    public String getCedula() {
        return cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public String getLicencia() {
        return licencia;
    }
    
    public int getEdad() { 
        return Period.between(fechaNacimiento, LocalDate.now()).getYears(); 
    }

    public Cliente(String cedula, String nombre, LocalDate fechaNacimiento, String telefono, String correo, String licencia) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.correo = correo;
        this.licencia = licencia;
    }
     @Override public String toString() {
        return cedula + " - " + nombre + " (" + getEdad() + " años)";
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente)) return false;
        Cliente c = (Cliente) o;
        return Objects.equals(cedula, c.cedula);
    }

    @Override public int hashCode() { return Objects.hash(cedula); }
}