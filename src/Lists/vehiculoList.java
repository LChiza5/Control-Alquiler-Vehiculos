/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Lists;

import Enums.EstadoVehiculo;
import Enums.TipoVehiculo;
import Modelo.Vehiculo;
import Utils.EntidadNoEncontradaException;
import Utils.ReglaDeNegocioException;
import Utils.Validators;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author ilope
 */
public class vehiculoList {
    private final Map<String, Vehiculo> porPlaca = new HashMap<>();

    // ========================
    // Implementación de List<T>
    // ========================
    public boolean add(Vehiculo v) {
        try {
            validarNuevo(v);
            if (porPlaca.containsKey(v.getPlaca())) {
                throw new ReglaDeNegocioException("Placa duplicada: " + v.getPlaca());
            }
            porPlaca.put(v.getPlaca(), v);
            return true;
        } catch (ReglaDeNegocioException ex) {
            // No imprimir: la vista/controlador debe capturar el error
            return false;
        }
    }

    public boolean remove(Vehiculo v) {
        if (v == null) return false;
        if (v.getEstado() == EstadoVehiculo.EN_ALQUILER) {
            // No se permite eliminar si está alquilado
            return false;
        }
        return porPlaca.remove(v.getPlaca()) != null;
    }

    public Vehiculo find(Object id) {
        if (id == null) return null;
        return porPlaca.get(id.toString());
    }

    public void showAll() {
        
    }

    // ========================
    // Métodos adicionales para la GUI
    // ========================

    /**
     * Lista de vehículos como java.util.List (para JTable).
     */
    public java.util.List<Vehiculo> listar() {
        return new ArrayList<>(porPlaca.values());
    }

    /**
     * Buscar vehículo por placa (más cómodo que find(Object)).
     */
    public Vehiculo find(String placa) {
        return porPlaca.get(placa);
    }

    /**
     * Actualizar vehículo existente (modelo, tipo, estado).
     */
    public void actualizar(String placa, String modelo, TipoVehiculo tipo, EstadoVehiculo estado)
            throws EntidadNoEncontradaException, ReglaDeNegocioException {

        Vehiculo v = porPlaca.get(placa);
        if (v == null) throw new EntidadNoEncontradaException("Vehículo no encontrado");

        if (modelo == null || modelo.isBlank())
            throw new ReglaDeNegocioException("El modelo es obligatorio");
        if (tipo == null)
            throw new ReglaDeNegocioException("El tipo es obligatorio");
        if (estado == null)
            throw new ReglaDeNegocioException("El estado es obligatorio");

        v.setModelo(modelo);
        v.setTipo(tipo);
        v.setEstado(estado);
    }

    // ========================
    // Validaciones de negocio
    // ========================
    private void validarNuevo(Vehiculo v) throws ReglaDeNegocioException {
        if (v == null) throw new ReglaDeNegocioException("Vehículo nulo");
        Validators.noVacio(v.getPlaca(), "placa");
        Validators.noVacio(v.getMarca(), "marca");
        Validators.noVacio(v.getModelo(), "modelo");
        Validators.anioVehiculoValido(v.getAnio(), 20);

        if (v.getTipo() == null)
            throw new ReglaDeNegocioException("Tipo de vehículo obligatorio");
        if (v.getEstado() == null)
            throw new ReglaDeNegocioException("Estado de vehículo obligatorio");
    }

    // ========================
    // Utilidades extra
    // ========================
    public boolean existePlaca(String placa) {
        return porPlaca.containsKey(placa);
    }

    public Collection<Vehiculo> todos() {
        return porPlaca.values();
    }
}
   
    


