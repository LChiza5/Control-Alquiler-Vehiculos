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
public class Contrato {
    private final int id;
    private final Cliente cliente;
    private final Vehiculo vehiculo;
    private final LocalDate inicio;
    private final LocalDate fin;
    private final double tarifaDiaria;
    private double monto;               // tarifaDiaria * dias
    private EstadoContrato estado;

    public Contrato(int id, Cliente cliente, Vehiculo vehiculo,
                    LocalDate inicio, LocalDate fin, double tarifaDiaria,
                    EstadoContrato estado) {
        this.id = id;
        this.cliente = cliente;
        this.vehiculo = vehiculo;
        this.inicio = inicio;
        this.fin = fin;
        this.tarifaDiaria = tarifaDiaria;
        this.estado = estado;
    }

    public int getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public LocalDate getInicio() { return inicio; }
    public LocalDate getFin() { return fin; }
    public double getTarifaDiaria() { return tarifaDiaria; }
    public double getMonto() { return monto; }
    public EstadoContrato getEstado() { return estado; }

    public void setEstado(EstadoContrato estado) { this.estado = estado; }
    public void setMonto(double monto) { this.monto = monto; }
}
