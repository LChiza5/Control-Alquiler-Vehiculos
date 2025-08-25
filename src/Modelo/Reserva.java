/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Enums.TipoVehiculo;
import java.time.LocalDate;

/**
 *
 * @author ilope
 */
public class Reserva extends RegistroTemp {
    private int id;
    private Cliente cliente;
    private Vehiculo vehiculo;            
    private TipoVehiculo tipoSolicitado;  
    private String estado;                

    public Reserva(int id, Cliente cliente, Vehiculo vehiculo,TipoVehiculo tipoSolicitado, LocalDate inicio, LocalDate fin, String estado){
    super(inicio, fin);
    this.id = id; this.cliente = cliente; this.vehiculo = vehiculo;
    this.tipoSolicitado = tipoSolicitado; this.estado = estado;
  }

    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public TipoVehiculo getTipoSolicitado() {
        return tipoSolicitado;
    }

    public String getEstado() {
        return estado;
    }
    @Override public String toString(){
    var placa = (vehiculo != null ? vehiculo.getPlaca() : "pendiente");
    return "Reserva{id=%d, cliente=%s, vehiculo=%s, dias=%d}".formatted(id, cliente.getCedula(), placa, getDias());
    } 
    
}
