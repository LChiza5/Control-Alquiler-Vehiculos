/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Enums.EstadoContrato;
import Enums.EstadoVehiculo;
import Lists.ContratoList;
import Lists.ReservaLista;
import Lists.clienteList;
import Lists.vehiculoList;
import Utils.EntidadNoEncontradaException;
import Utils.EstadoInvalidoException;
import Utils.ReglaDeNegocioException;
import Utils.Validators;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author ilope
 */
public class ContratoManager {

    private final clienteList repoClientes;
    private final vehiculoList repoVehiculos;
    private final ReservaLista repoReservas;
    private final ContratoList repoContratos;

    public ContratoManager(clienteList clientes,
                           vehiculoList vehiculos,
                           ReservaLista reservas,
                           ContratoList contratos) {
        this.repoClientes = clientes;
        this.repoVehiculos = vehiculos;
        this.repoReservas = reservas;
        this.repoContratos = contratos;
    }

    // ================== Clase interna ==================
    public class GestorContratos {

        /** Crear contrato sin reserva (ad-hoc) */
        public Contrato crearAdHoc(int id, String cedula, String placa,
                           LocalDate ini, LocalDate fin, double tarifa)
        throws EntidadNoEncontradaException, ReglaDeNegocioException {

    Cliente cli = repoClientes.find(cedula);
    if (cli == null) throw new EntidadNoEncontradaException("Cliente no encontrado");

    Vehiculo veh = repoVehiculos.find(placa);
    if (veh == null) throw new EntidadNoEncontradaException("Vehículo no encontrado");

    if (ini.isBefore(LocalDate.now()))
        throw new ReglaDeNegocioException("La fecha de inicio no puede ser menor a hoy.");
    if (!fin.isAfter(ini))
        throw new ReglaDeNegocioException("La fecha fin debe ser posterior a la de inicio.");

    long dias = java.time.temporal.ChronoUnit.DAYS.between(ini, fin);
    double monto = dias * tarifa;

    Contrato c = new Contrato(id, cli, veh, ini, fin, monto, EstadoContrato.ACTIVO);
    repoContratos.add(c);

    veh.setEstado(EstadoVehiculo.EN_ALQUILER);

    return c;
}


        /** Crear contrato desde una reserva ya confirmada */
        public Contrato crearDesdeReserva(int idContrato, int idReserva, double tarifa) throws EntidadNoEncontradaException, ReglaDeNegocioException {
    Reserva r = repoReservas.find(idReserva);
    if (r == null) throw new EntidadNoEncontradaException("Reserva no existe.");
    if (r.getVehiculo() == null)
        throw new ReglaDeNegocioException("La reserva no tiene vehículo asignado.");

    Vehiculo veh = r.getVehiculo();
    if (veh.getEstado() != EstadoVehiculo.DISPONIBLE)
        throw new ReglaDeNegocioException("El vehículo no está disponible.");

    long dias = ChronoUnit.DAYS.between(r.getInicio(), r.getFin());
    if (dias <= 0) throw new ReglaDeNegocioException("Las fechas de la reserva no son válidas.");

    double monto = dias * tarifa;

    Contrato c = new Contrato(idContrato, r.getCliente(), veh, r.getInicio(), r.getFin(), monto, EstadoContrato.ACTIVO);
    repoContratos.add(c);

    veh.setEstado(EstadoVehiculo.EN_ALQUILER);

    return c;
}

        /** Finalizar contrato activo → pasa a FINALIZADO y libera el vehículo */
        public void finalizar(int idContrato) throws EntidadNoEncontradaException {
            Contrato c = repoContratos.find(idContrato);
            if (c == null) throw new EntidadNoEncontradaException("Contrato no existe.");
            if (c.getEstado() != EstadoContrato.ACTIVO)
                throw new EstadoInvalidoException("Solo se pueden finalizar contratos activos.");

            c.setEstado(EstadoContrato.FINALIZADO);
            c.getVehiculo().setEstado(EstadoVehiculo.DISPONIBLE);
        }

        /** Cancelar contrato (si estaba activo, libera vehículo) */
        public void cancelar(int idContrato) throws EntidadNoEncontradaException {
            Contrato c = repoContratos.find(idContrato);
            if (c == null) throw new EntidadNoEncontradaException("Contrato no existe.");
            if (c.getEstado() == EstadoContrato.FINALIZADO)
                throw new EstadoInvalidoException("No se puede cancelar un contrato finalizado.");

            c.setEstado(EstadoContrato.CANCELADO);
            if (c.getVehiculo().getEstado() == EstadoVehiculo.EN_ALQUILER) {
                c.getVehiculo().setEstado(EstadoVehiculo.DISPONIBLE);
            }
        }
    }
}