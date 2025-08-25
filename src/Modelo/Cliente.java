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
public class Cliente extends Persona {
  private String licencia;

      public Cliente(String cedula, String nombre, LocalDate fechaNac,String telefono, String correo, String licencia) {
      super(cedula, nombre, fechaNac, telefono, correo);
      this.licencia = licencia;
    }

    public String getLicencia() {
        return licencia;
    }

     
  
}