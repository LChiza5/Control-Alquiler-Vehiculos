/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Enums.EstadoContrato;
import java.time.LocalDate;

/**
 *
 * @author ilope
 */
public class Contrato extends RegistroTemp{
    private int id;
    private Cliente cliente;
    private Vehiculo vehiculo;
    private double monto;
    private EstadoContrato estado;
    
    public Contrato(int id, Cliente cliente, Vehiculo vehiculo,LocalDate inicio, LocalDate fin, double monto, EstadoContrato estado) {
    super(inicio, fin);
    this.id = id; this.cliente = cliente; this.vehiculo = vehiculo;
    this.monto = monto; this.estado = estado;
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

    public double getMonto() {
        return monto;
    }

    public EstadoContrato getEstado() {
        return estado;
    }
    @Override public String toString(){
    return "Contrato{id=%d, placa=%s, estado=%s, dias=%d}".formatted(
        id, vehiculo!=null?vehiculo.getPlaca():"-", estado, getDias());
    }

    void setEstado(EstadoContrato estadoContrato) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}  
