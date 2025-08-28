/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Enums.EstadoVehiculo;
import Enums.TipoVehiculo;
import java.util.Objects;

/**
 *
 * @author ilope
 */
public class Vehiculo {
    private String placa, marca;
    private int anio;
    private TipoVehiculo modelo;
    private EstadoVehiculo estado;

    public String getPlaca() {
        return placa;
    }

    public String getMarca() {
        return marca;
    }

    public TipoVehiculo getModelo() {
        return modelo;
    }

    public int getAnio() {
        return anio;
    }

    

    public EstadoVehiculo getEstado() {
        return estado;
    }

    public void setEstado(EstadoVehiculo estado) {
        this.estado = estado;
    }

    public Vehiculo(String placa, String marca, TipoVehiculo modelo, int anio, EstadoVehiculo estado) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.estado = estado;
    }
    
    public boolean equals(Object o){ 
        return o instanceof Vehiculo v && placa != null && placa.equals(v.placa); 
    }
    
    @Override 
    public int hashCode(){ 
        return Objects.hash(placa); 
    }
    
    @Override
    public String toString(){ 
        return "Vehiculo{placa='%s', modelo='%s'}".formatted(placa,modelo); 
    }

    public void setModelo(TipoVehiculo modelo) {
        this.modelo = modelo;
    }

   
}
