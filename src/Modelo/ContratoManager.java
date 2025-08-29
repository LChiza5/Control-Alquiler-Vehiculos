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

    private final clienteList clienteRepo;
    private final vehiculoList vehiculoRepo;
    private final ReservaLista reservaRepo;
    private final ContratoList contratoRepo;

    public ContratoManager(clienteList clientes, vehiculoList vehiculos,
                           ReservaLista reservas, ContratoList contratos) {
        this.clienteRepo = clientes;
        this.vehiculoRepo = vehiculos;
        this.reservaRepo = reservas;
        this.contratoRepo = contratos;
    }

    public class GestorContratos {

        /** Crear desde reserva confirmada (idReserva) */
        public Contrato crearDesdeReserva(int idReserva, double tarifaDiaria)
                throws EntidadNoEncontradaException, ReglaDeNegocioException {

            if (tarifaDiaria <= 0) throw new ReglaDeNegocioException("Tarifa diaria inválida");

            Reserva r = reservaRepo.find(idReserva);
            if (r == null) throw new EntidadNoEncontradaException("Reserva no existe");
            if (!"CONFIRMADA".equalsIgnoreCase(r.getEstado()))
                throw new ReglaDeNegocioException("La reserva no está confirmada");

            Cliente c = r.getCliente();
            Vehiculo v = r.getVehiculo();
            if (c == null || v == null) throw new ReglaDeNegocioException("Reserva incompleta (cliente/vehículo)");

            // Validaciones de fechas
            Validators.inicioNoAntesDeHoy(r.getInicio());
            Validators.finPosterior(r.getInicio(), r.getFin());

            // Vehículo debe estar disponible para el rango (sin contratos activos solapados)
            if (!disponibleParaAlquiler(v, r.getInicio(), r.getFin()))
                throw new ReglaDeNegocioException("Ya existe un alquiler activo para ese vehículo en ese rango");

            // Crear contrato ACTIVO
            int id = generarIdContrato();
            Contrato cto = new Contrato(id, c, v, r.getInicio(), r.getFin(), tarifaDiaria, EstadoContrato.ACTIVO);
            cto.setMonto(calcularMonto(cto.getInicio(), cto.getFin(), tarifaDiaria));
            contratoRepo.add(cto);

            // Cambiar estado del vehículo
            v.setEstado(EstadoVehiculo.EN_ALQUILER);

            // (Opcional) remover la reserva de la lista, o marcarla; aquí la removemos:
            reservaRepo.remove(r);

            return cto;
        }

        /** Crear ad-hoc (sin reserva previa) */
        public Contrato crearAdHoc(String cedula, String placa, LocalDate inicio, LocalDate fin, double tarifaDiaria)
                throws EntidadNoEncontradaException, ReglaDeNegocioException {

            if (cedula == null || cedula.isBlank()) throw new ReglaDeNegocioException("Cédula requerida");
            if (placa == null || placa.isBlank()) throw new ReglaDeNegocioException("Placa requerida");
            if (tarifaDiaria <= 0) throw new ReglaDeNegocioException("Tarifa diaria inválida");

            // Validaciones de fechas
            Validators.inicioNoAntesDeHoy(inicio);
            Validators.finPosterior(inicio, fin);

            Cliente c = clienteRepo.find(cedula);
            if (c == null) throw new EntidadNoEncontradaException("Cliente no existe: " + cedula);

            Vehiculo v = vehiculoRepo.find(placa);
            if (v == null) throw new EntidadNoEncontradaException("Vehículo no existe: " + placa);
            if (v.getEstado() == EstadoVehiculo.EN_MANTENIMIENTO)
                throw new ReglaDeNegocioException("El vehículo está en mantenimiento");

            // No permitir alquiler si hay contrato ACTIVO solapado
            if (!disponibleParaAlquiler(v, inicio, fin))
                throw new ReglaDeNegocioException("Ya existe un alquiler activo para ese vehículo en ese rango");

            // Crear contrato ACTIVO
            int id = generarIdContrato();
            Contrato cto = new Contrato(id, c, v, inicio, fin, tarifaDiaria, EstadoContrato.ACTIVO);
            cto.setMonto(calcularMonto(inicio, fin, tarifaDiaria));
            contratoRepo.add(cto);

            // Cambiar estado del vehículo
            v.setEstado(EstadoVehiculo.EN_ALQUILER);

            return cto;
        }

        /** Finalizar contrato (solo ACTIVO) → libera vehículo */
        public void finalizar(int idContrato) throws EntidadNoEncontradaException, EstadoInvalidoException {
            Contrato c = contratoRepo.find(idContrato);
            if (c == null) throw new EntidadNoEncontradaException("Contrato no encontrado");
            if (c.getEstado() != EstadoContrato.ACTIVO)
                throw new EstadoInvalidoException("Solo contratos ACTIVO pueden finalizarse");

            c.setEstado(EstadoContrato.FINALIZADO);
            // Liberar vehículo
            if (c.getVehiculo() != null) c.getVehiculo().setEstado(EstadoVehiculo.DISPONIBLE);
        }

        /** Cancelar contrato (no permite cancelar si ya está FINALIZADO) */
        public void cancelar(int idContrato) throws EntidadNoEncontradaException, EstadoInvalidoException {
            Contrato c = contratoRepo.find(idContrato);
            if (c == null) throw new EntidadNoEncontradaException("Contrato no encontrado");
            if (c.getEstado() == EstadoContrato.FINALIZADO)
                throw new EstadoInvalidoException("No se puede cancelar un contrato finalizado");

            c.setEstado(EstadoContrato.CANCELADO);
            // Si estaba ACTIVO, liberar vehículo
            if (c.getVehiculo() != null) c.getVehiculo().setEstado(EstadoVehiculo.DISPONIBLE);
        }

        // ===== Helpers =====

        private int generarIdContrato() {
            return contratoRepo.listar().size() + 1;
        }

        private boolean disponibleParaAlquiler(Vehiculo v, LocalDate ini, LocalDate fin) {
            for (Contrato c : contratoRepo.listar()) {
                if (c.getEstado() == EstadoContrato.ACTIVO &&
                    c.getVehiculo() != null &&
                    v.getPlaca().equals(c.getVehiculo().getPlaca())) {
                    if (solapa(c.getInicio(), c.getFin(), ini, fin)) return false;
                }
            }
            return true;
        }

        // igual criterio que usaste en reservas (fin inclusivo)
        private boolean solapa(LocalDate a1, LocalDate a2, LocalDate b1, LocalDate b2) {
            return !a2.isBefore(b1) && !b2.isBefore(a1);
        }

        private double calcularMonto(LocalDate ini, LocalDate fin, double tarifaDiaria) {
            long dias = ChronoUnit.DAYS.between(ini, fin) + 1; // [ini, fin] inclusivo
            if (dias <= 0) dias = 1;
            return tarifaDiaria * dias;
        }
    }
}