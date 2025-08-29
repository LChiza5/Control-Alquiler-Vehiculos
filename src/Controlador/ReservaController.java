/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;
import Lists.*;
import Modelo.*;
import Enums.*;
import Utils.*;
import Vista.BuscarVehiculos;
import Vista.FrmBuscarReserva;
import Vista.FrmReserva;
import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
/**
 *
 * @author ilope
 */
public class ReservaController {

    private final clienteList clientes;
    private final vehiculoList vehiculos;
    private final ReservaLista reservas;
    private final ReservaQueue cola;

    private final ReservaManager manager;
    private final ReservaManager.GestorReservas gestor;

    private final FrmReserva frm;

    public ReservaController(clienteList clientes,
                             vehiculoList vehiculos,
                             ReservaLista reservas,
                             ReservaQueue cola,
                             FrmReserva frm) {
        this.clientes = clientes;
        this.vehiculos = vehiculos;
        this.reservas = reservas;
        this.cola = cola;

        this.manager = new ReservaManager(clientes, vehiculos, reservas, cola);
        this.gestor = manager.new GestorReservas();
        this.frm = frm;

        // Carga el combo con los TIPOS del enum
        frm.setTipos(tiposComoTexto());

        wire();
    }

    private void wire() {
        frm.getBtnSave().addActionListener(e -> crearPorTipo());
        frm.getBtnUpdate().addActionListener(e -> modificarVehiculo());
        frm.getBtnEliminar().addActionListener(e -> cancelar());
        frm.getBtnClear().addActionListener(e -> frm.clearForm());
        frm.getBtnSearch().addActionListener(e -> abrirDialogoBuscarReservas());
    }

    // ================= Acciones =================

    // Abre el JDialog de búsqueda y pega la selección al form
    private void abrirDialogoBuscarReservas() {
        java.awt.Frame owner = (java.awt.Frame) SwingUtilities.getWindowAncestor(frm);
        FrmBuscarReserva dlg = new FrmBuscarReserva(owner, true);

        // Confirmadas + en espera (snapshot, sin alterar el orden de la cola)
        java.util.List<Reserva> todas = listarTodasLasReservas();
        dlg.setResultados(todas);

        dlg.setLocationRelativeTo(frm);
        dlg.setVisible(true);

        Reserva seleccion = dlg.getReservaSeleccionada();
        if (seleccion != null) {
            frm.setReservaToForm(seleccion);
        }
    }

    private void crearPorTipo() {
        try {
            String cedula = frm.getCedulaCliente().trim();
            String tipoTxt = frm.getTipoSeleccionado();
            LocalDate ini = parse(frm.getFechaInicioText());
            LocalDate fin = parse(frm.getFechaFinText());

            if (cedula.isBlank() || tipoTxt == null || tipoTxt.isBlank()) {
                throw new ReglaDeNegocioException("Complete cédula y seleccione un tipo de vehículo.");
            }

            TipoVehiculo tipo = TipoVehiculo.valueOf(tipoTxt);

            // Crea por TIPO (si no hay disponible, queda EN_ESPERA en la cola)
            Reserva r = gestor.crearReservaPorTipo(cedula, tipo, ini, fin);
            frm.setNumeroReserva(r.getId());

            JOptionPane.showMessageDialog(
                    frm,
                    (r.getVehiculo() == null)
                            ? "No hay disponible de ese tipo en ese rango. La reserva quedó en cola."
                            : "Reserva confirmada con vehículo asignado.",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (EntidadNoEncontradaException | ReglaDeNegocioException ex) {
            JOptionPane.showMessageDialog(frm, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frm, "Datos inválidos o incompletos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarVehiculo() {
        try {
            int id = Integer.parseInt(frm.getNumeroReservaText().trim());

            // Abre buscador de vehículos para elegir la nueva placa
            java.awt.Frame owner = (java.awt.Frame) SwingUtilities.getWindowAncestor(frm);
            BuscarVehiculos dlg = new BuscarVehiculos(owner, true);
            dlg.setList(vehiculos);
            dlg.setLocationRelativeTo(frm);
            dlg.setVisible(true);

            Vehiculo nuevo = dlg.getVehiculoSeleccionado();
            if (nuevo == null) return; // cancelaron

            gestor.modificarVehiculoReservado(id, nuevo.getPlaca());

            JOptionPane.showMessageDialog(frm, "Vehículo reasignado correctamente.", "OK",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (EntidadNoEncontradaException | ReglaDeNegocioException | EstadoInvalidoException ex) {
            JOptionPane.showMessageDialog(frm, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frm, "Número de reserva inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelar() {
        try {
            int id = Integer.parseInt(frm.getNumeroReservaText().trim());
            gestor.cancelarReserva(id);

            JOptionPane.showMessageDialog(frm, "Reserva cancelada.", "OK",
                    JOptionPane.INFORMATION_MESSAGE);
            frm.clearForm();
        } catch (EntidadNoEncontradaException | EstadoInvalidoException ex) {
            JOptionPane.showMessageDialog(frm, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frm, "Número de reserva inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= Utils =================

    private static LocalDate parse(String s) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(s, fmt);
    }

    private static java.util.List<String> tiposComoTexto() {
        java.util.List<String> out = new java.util.ArrayList<>();
        for (TipoVehiculo t : TipoVehiculo.values()) out.add(t.name());
        return out;
    }

    private java.util.List<Reserva> listarTodasLasReservas() {
        java.util.List<Reserva> out = new java.util.ArrayList<>();
        out.addAll(reservas.listar());     // confirmadas
        out.addAll(cola.toList());         // snapshot de la cola (EN_ESPERA)
        return out;
    }
}