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
    private final clienteList clienteLista;
  private final vehiculoList vehiculoLista;
  private final ReservaLista reservaLista;
  private final ContratoList contratoLista;

  public ContratoManager(clienteList clienteLista, vehiculoList vehiculoLista,
                         ReservaLista reservaLista, ContratoList contratoLista) {
    this.clienteLista = clienteLista;
    this.vehiculoLista = vehiculoLista;
    this.reservaLista = reservaLista;
    this.contratoLista = contratoLista;
  }

  /** Crear contrato sin reserva previa. */
  public Contrato crearContrato(String cedulaCliente, String placaVehiculo,
                                LocalDate inicio, LocalDate fin, double tarifaDiaria) throws ReglaDeNegocioException, EntidadNoEncontradaException {
    Validators.inicioNoAntesDeHoy(inicio);
    Validators.finPosterior(inicio, fin);
    Validators.duracionMaxima(inicio, fin, 30);

    Cliente cliente = clienteLista.find(cedulaCliente);
    if (cliente == null) throw new EntidadNoEncontradaException("Cliente no encontrado");

    Vehiculo vehiculo = vehiculoLista.find(placaVehiculo);
    if (vehiculo == null) throw new EntidadNoEncontradaException("Vehículo no encontrado");

    if (vehiculo.getEstado() != EstadoVehiculo.DISPONIBLE)
      throw new ReglaDeNegocioException("El vehículo no está disponible");

    if (!vehiculoDisponibleParaContrato(vehiculo, inicio, fin))
      throw new ReglaDeNegocioException("El vehículo ya está reservado o en contrato activo en ese rango");

    int id = contratoLista.listar().size() + 1;
    double monto = tarifaDiaria * ChronoUnit.DAYS.between(inicio, fin);

    Contrato c = new Contrato(id, cliente, vehiculo, inicio, fin, monto, EstadoContrato.ACTIVO);
    contratoLista.add(c);
    return c;
  }

  /** Crear contrato desde una reserva confirmada. */
  public Contrato crearDesdeReserva(int idReserva, double tarifaDiaria) throws EntidadNoEncontradaException, ReglaDeNegocioException {
    Reserva r = reservaLista.find(idReserva);
    if (r == null) throw new EntidadNoEncontradaException("Reserva no encontrada");
    if (!"CONFIRMADA".equals(r.getEstado()))
      throw new EstadoInvalidoException("Solo reservas CONFIRMADAS pueden generar contrato");

    Contrato c = crearContrato(r.getCliente().getCedula(), r.getVehiculo().getPlaca(),
            r.getInicio(), r.getFin(), tarifaDiaria);

    reservaLista.remove(r);
    return c;
  }

  /** Iniciar contrato → vehículo EN_ALQUILER. */
  public void iniciarContrato(int idContrato) throws EntidadNoEncontradaException {
    Contrato c = contratoLista.find(idContrato);
    if (c == null) throw new EntidadNoEncontradaException("Contrato no encontrado");
    if (c.getEstado() != EstadoContrato.ACTIVO)
      throw new EstadoInvalidoException("El contrato no está en estado ACTIVO");

    if (c.getInicio().isAfter(LocalDate.now()))
      throw new EstadoInvalidoException("No se puede iniciar antes de la fecha de inicio");

    c.getVehiculo().setEstado(EstadoVehiculo.EN_ALQUILER);
  }

  /** Finalizar contrato → estado FINALIZADO, vehículo DISPONIBLE. */
  public void finalizarContrato(int idContrato) throws EntidadNoEncontradaException {
    Contrato c = contratoLista.find(idContrato);
    if (c == null) throw new EntidadNoEncontradaException("Contrato no encontrado");
    if (c.getEstado() != EstadoContrato.ACTIVO)
      throw new EstadoInvalidoException("Solo contratos ACTIVO pueden finalizarse");

    c.setEstado(EstadoContrato.FINALIZADO);
    c.getVehiculo().setEstado(EstadoVehiculo.DISPONIBLE);
  }

  /** Cancelar contrato → estado CANCELADO (no si ya finalizado). */
  public void cancelarContrato(int idContrato) throws EntidadNoEncontradaException {
    Contrato c = contratoLista.find(idContrato);
    if (c == null) throw new EntidadNoEncontradaException("Contrato no encontrado");
    if (c.getEstado() == EstadoContrato.FINALIZADO)
      throw new EstadoInvalidoException("No se puede cancelar un contrato finalizado");

    c.setEstado(EstadoContrato.CANCELADO);
    if (c.getVehiculo().getEstado() == EstadoVehiculo.EN_ALQUILER) {
      c.getVehiculo().setEstado(EstadoVehiculo.DISPONIBLE);
    }
  }

  // --- helpers ---
  private boolean vehiculoDisponibleParaContrato(Vehiculo v, LocalDate ini, LocalDate fin) {
    // reservas confirmadas
    for (Reserva r : reservaLista.listar()) {
      if (r.getVehiculo() != null && v.getPlaca().equals(r.getVehiculo().getPlaca())) {
        if (solapa(r.getInicio(), r.getFin(), ini, fin)) return false;
      }
    }
    // contratos activos
    for (Contrato c : contratoLista.listar()) {
      if (v.getPlaca().equals(c.getVehiculo().getPlaca()) && c.getEstado() == EstadoContrato.ACTIVO) {
        if (solapa(c.getInicio(), c.getFin(), ini, fin)) return false;
      }
    }
    return true;
  }

  private boolean solapa(LocalDate a1, LocalDate a2, LocalDate b1, LocalDate b2) {
    return !a2.isBefore(b1) && !b2.isBefore(a1);
  }
}