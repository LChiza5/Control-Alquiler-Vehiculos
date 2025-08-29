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

    public Vehiculo(String placa, String marca, String modelo, int anio, TipoVehiculo tipo, EstadoVehiculo estado1) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.tipo = tipo;
        this.estado = EstadoVehiculo.DISPONIBLE; // 👈 por defecto DISPONIBLE
    }

    // ===== Getters & Setters =====
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public TipoVehiculo getTipo() { return tipo; }
    public void setTipo(TipoVehiculo tipo) { this.tipo = tipo; }

    public EstadoVehiculo getEstado() { return estado; }
    public void setEstado(EstadoVehiculo estado) { this.estado = estado; }

    @Override
    public String toString() {
        return placa + " - " + marca + " " + modelo + " (" + tipo + ")";
    }
}