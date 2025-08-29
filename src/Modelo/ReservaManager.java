/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Enums.EstadoVehiculo;
import Enums.TipoVehiculo;
import Lists.ReservaLista;
import Lists.clienteList;
import Lists.ReservaQueue;
import Lists.vehiculoList;
import Utils.EntidadNoEncontradaException;
import Utils.EstadoInvalidoException;
import Utils.ReglaDeNegocioException;
import Utils.Validators;
import java.time.LocalDate;
import java.util.Collection;

/**
 *
 * @author ilope
 */
public class ReservaManager {

    private final clienteList clienteLista;
    private final vehiculoList vehiculoLista;
    private final ReservaLista reservaLista;
    private final ReservaQueue reservaQueue;

    public ReservaManager(clienteList clienteLista, vehiculoList vehiculoLista,
                          ReservaLista reservaLista, ReservaQueue reservaQueue) {
        this.clienteLista = clienteLista;
        this.vehiculoLista = vehiculoLista;
        this.reservaLista = reservaLista;
        this.reservaQueue = reservaQueue;
    }

    /** Lógica de reservas (por TIPO con cola) */
    public class GestorReservas {

        public Reserva crearReservaPorTipo(String cedulaCliente, TipoVehiculo tipo, LocalDate inicio, LocalDate fin)
                throws ReglaDeNegocioException, EntidadNoEncontradaException {

            Validators.inicioNoAntesDeHoy(inicio);
            Validators.finPosterior(inicio, fin);
            Validators.duracionMaxima(inicio, fin, 30);

            Cliente cliente = clienteLista.find(cedulaCliente);
            if (cliente == null) throw new EntidadNoEncontradaException("Cliente no existe: " + cedulaCliente);

            Vehiculo disponible = buscarVehiculoDisponible(tipo, inicio, fin);
            int nuevoId = generarIdReserva();

            if (disponible == null) {
                Reserva enEspera = new Reserva(nuevoId, cliente, null, tipo, inicio, fin, "EN_ESPERA");
                reservaQueue.add(enEspera);
                return enEspera;
            } else {
                Reserva r = new Reserva(nuevoId, cliente, disponible, null, inicio, fin, "CONFIRMADA");
                reservaLista.add(r);
                return r;
            }
        }

        /** FIRMA CORREGIDA: añade EstadoInvalidoException */
        public void modificarVehiculoReservado(int idReserva, String placaNueva)
                throws ReglaDeNegocioException, EntidadNoEncontradaException, EstadoInvalidoException {

            Reserva r = reservaLista.find(idReserva);
            if (r == null) throw new EntidadNoEncontradaException("Reserva no encontrada");
            if (!r.getInicio().isAfter(LocalDate.now().minusDays(1)))
                throw new EstadoInvalidoException("No se puede modificar una reserva que ya inició o inicia hoy");

            Vehiculo nuevo = vehiculoLista.find(placaNueva);
            if (nuevo == null) throw new EntidadNoEncontradaException("Vehículo no existe");

            TipoVehiculo tipoOriginal = (r.getVehiculo() != null) ? r.getVehiculo().getTipo() : r.getTipoSolicitado();
            if (!nuevo.getTipo().equals(tipoOriginal))
                throw new ReglaDeNegocioException("El vehículo no coincide con el tipo solicitado");

            if (!vehiculoDisponibleEnRango(nuevo, r.getInicio(), r.getFin()))
                throw new ReglaDeNegocioException("El vehículo no está disponible en ese rango de fechas");

            r.setVehiculo(nuevo);
        }

        public void cancelarReserva(int idReserva)
                throws EntidadNoEncontradaException, EstadoInvalidoException {
            Reserva r = reservaLista.find(idReserva);
            if (r == null) throw new EntidadNoEncontradaException("Reserva no encontrada");
            if (!r.getInicio().isAfter(LocalDate.now().minusDays(1)))
                throw new EstadoInvalidoException("No se puede cancelar una reserva que ya inició o inicia hoy");

            r.setEstado("CANCELADA");
            reservaLista.remove(r);
        }

        public Reserva sacarDeEsperaYConfirmar(TipoVehiculo tipo, LocalDate inicio, LocalDate fin) {
            Reserva candidata = null;
            int size = reservaQueue.size();

            for (int i = 0; i < size; i++) {
                Reserva r = reservaQueue.siguiente(); // saca primero
                if (r == null) break;

                if (r.getTipoSolicitado().equals(tipo) && mismasFechas(r, inicio, fin)) {
                    candidata = r;
                    break;
                } else {
                    reservaQueue.add(r); // regresa al final
                }
            }

            if (candidata == null) return null;

            Vehiculo disp = buscarVehiculoDisponible(tipo, inicio, fin);
            if (disp == null) {
                reservaQueue.add(candidata); // sigue en espera
                return null;
            }

            candidata.setVehiculo(disp);
            candidata.setEstado("CONFIRMADA");
            reservaLista.add(candidata);
            return candidata;
        }

        // ---------- helpers ----------
        private int generarIdReserva() {
            return reservaLista.listar().size() + reservaQueue.size() + 1;
        }

        private boolean mismasFechas(Reserva r, LocalDate ini, LocalDate fin) {
            return r.getInicio().equals(ini) && r.getFin().equals(fin);
        }

        private Vehiculo buscarVehiculoDisponible(TipoVehiculo tipo, LocalDate ini, LocalDate fin) {
            Collection<Vehiculo> todos = vehiculoLista.todos();
            for (Vehiculo v : todos) {
                if (v.getTipo().equals(tipo) && v.getEstado() == EstadoVehiculo.DISPONIBLE) {
                    if (vehiculoDisponibleEnRango(v, ini, fin)) return v;
                }
            }
            return null;
        }

        public boolean vehiculoDisponibleEnRango(Vehiculo v, LocalDate ini, LocalDate fin) {
            for (Reserva r : reservaLista.listar()) {
                if (r.getVehiculo() != null && v.getPlaca().equals(r.getVehiculo().getPlaca())) {
                    if (solapa(r.getInicio(), r.getFin(), ini, fin)) return false;
                }
            }
            return true;
        }

        private boolean solapa(LocalDate a1, LocalDate a2, LocalDate b1, LocalDate b2) {
            return !a2.isBefore(b1) && !b2.isBefore(a1);
        }
    }
}