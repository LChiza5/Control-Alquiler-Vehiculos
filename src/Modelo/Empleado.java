/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.time.LocalDate;

/**
 *
 * @author ilope
 */
public class Empleado extends Persona {
  private String puesto;
  private double salario;

  public Empleado(String cedula, String nombre, LocalDate fechaNac, String telefono, String correo, String puesto, double salario) {
    super(cedula, nombre, fechaNac, telefono, correo);
    this.puesto = puesto; 
    this.salario = salario;
  }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) { this.puesto = puesto; }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    
    

}
