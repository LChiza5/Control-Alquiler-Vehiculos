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
    private String placa;
    private String marca;
    private String modelo;
    private int anio;
    private TipoVehiculo tipo;
    private EstadoVehiculo estado;

    public Vehiculo(String placa, String marca, String modelo, int anio, TipoVehiculo tipo, EstadoVehiculo estado) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.tipo = tipo;
        this.estado = estado;
    }

    // Getters
    public String getPlaca() { return placa; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public int getAnio() { return anio; }
    public TipoVehiculo getTipo() { return tipo; }
    public EstadoVehiculo getEstado() { return estado; }

    // Setters
    public void setModelo(String modelo) { this.modelo = modelo; }
    public void setTipo(TipoVehiculo tipo) { this.tipo = tipo; }
    public void setEstado(EstadoVehiculo estado) { this.estado = estado; }

    @Override
    public boolean equals(Object o) {
        return o instanceof Vehiculo v && placa != null && placa.equals(v.placa);
    }
    @Override
    public int hashCode() { return Objects.hash(placa); }

    @Override
    public String toString() {
        return "Vehiculo{placa='%s', marca='%s', modelo='%s', anio=%d, tipo=%s, estado=%s}"
                .formatted(placa, marca, modelo, anio, tipo, estado);
    }
}
