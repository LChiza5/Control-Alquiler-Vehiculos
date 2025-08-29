/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;
import Enums.EstadoContrato;
import Enums.EstadoVehiculo;
import Lists.ContratoList;
import Lists.ReservaLista;
import Lists.clienteList;
import Lists.vehiculoList;
import Modelo.Cliente;
import Modelo.Contrato;
import Modelo.ContratoManager;
import Modelo.Reserva;
import Modelo.Vehiculo;
import Utils.EntidadNoEncontradaException;
import Utils.EstadoInvalidoException;
import Utils.ReglaDeNegocioException;
import Vista.FrmBuscarContrato;
import Vista.FrmContratos;

import javax.swing.*;
import javax.swing.SwingUtilities;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author ilope
 */
public class ContratoController {

    private final clienteList repoClientes;
    private final vehiculoList repoVehiculos;
    private final ReservaLista repoReservas;
    private final ContratoList repoContratos;

    private final ContratoManager manager;
    private final ContratoManager.GestorContratos gestor;

    private final FrmContratos frm;

    public ContratoController(clienteList clientes,
                              vehiculoList vehiculos,
                              ReservaLista reservas,
                              ContratoList contratos,
                              FrmContratos frm) {
        this.repoClientes = clientes;
        this.repoVehiculos = vehiculos;
        this.repoReservas = reservas;
        this.repoContratos = contratos;
        this.manager = new ContratoManager(clientes, vehiculos, reservas, contratos);
        this.gestor = manager.new GestorContratos();
        this.frm = frm;

        // cargar placas disponibles (vehículos DISPONIBLE)
        frm.setPlacas(placasDisponibles(repoVehiculos));

        // cargar estados en el combo (si quieres forzar los nombres del enum)
        frm.setEstados(estadoContratoComoTexto());

        wire();
    }

    private void wire() {
        frm.getBtnSave().addActionListener(e -> guardarAdHoc());
        frm.getBtnUpdate().addActionListener(e -> finalizar());
        frm.getBtnEliminar().addActionListener(e -> cancelar());
        frm.getBtnSearch().addActionListener(e -> abrirBuscar());
        frm.getBtnClear().addActionListener(e -> frm.clearForm());
    }

    // ==================== Acciones ====================

    /** Guardar: crea un contrato ad-hoc pidiendo tarifa diaria por input */
    private void guardarAdHoc() {
        try {
            String cedula = frm.getCedulaCliente();
            String placa  = frm.getPlacaSeleccionada();
            LocalDate ini = parse(frm.getFechaInicioText());
            LocalDate fin = parse(frm.getFechaFinText());

            if (cedula.isBlank() || placa == null || placa.isBlank())
                throw new ReglaDeNegocioException("Complete cédula y seleccione una placa.");

            String s = JOptionPane.showInputDialog(frm, "Tarifa diaria:", "0");
            if (s == null) return; // cancelado
            double tarifa = Double.parseDouble(s);

            Contrato cto = gestor.crearAdHoc(cedula, placa, ini, fin, tarifa);

            frm.setNumeroContrato(cto.getId());
            frm.setMontoCalculado(String.valueOf(cto.getMonto()));
            frm.setEstado(cto.getEstado().name());

            // refrescar combo de placas disponibles (el vehículo quedó EN_ALQUILER)
            frm.setPlacas(placasDisponibles(repoVehiculos));

            JOptionPane.showMessageDialog(frm,
                    "Contrato #" + cto.getId() + " creado. Monto: " + cto.getMonto(),
                    "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (EntidadNoEncontradaException | ReglaDeNegocioException ex) {
            JOptionPane.showMessageDialog(frm, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frm, "Tarifa inválida.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frm, "Datos inválidos o incompletos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Update: finaliza contrato ACTIVO y libera el vehículo */
    private void finalizar() {
        try {
            int id = Integer.parseInt(frm.getNumeroContratoText());
            gestor.finalizar(id);

            JOptionPane.showMessageDialog(frm, "Contrato finalizado.", "OK",
                    JOptionPane.INFORMATION_MESSAGE);

            // refrescar placas disponibles (vehículo liberado a DISPONIBLE)
            frm.setPlacas(placasDisponibles(repoVehiculos));
            frm.setEstado(EstadoContrato.FINALIZADO.name());

        } catch (EntidadNoEncontradaException | EstadoInvalidoException ex) {
            JOptionPane.showMessageDialog(frm, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frm, "Número de contrato inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Eliminar: cancela contrato (si estaba ACTIVO, también libera vehículo) */
    private void cancelar() {
        try {
            int id = Integer.parseInt(frm.getNumeroContratoText());
            gestor.cancelar(id);

            JOptionPane.showMessageDialog(frm, "Contrato cancelado.", "OK",
                    JOptionPane.INFORMATION_MESSAGE);

            // refrescar placas disponibles (vehículo liberado a DISPONIBLE si estaba activo)
            frm.setPlacas(placasDisponibles(repoVehiculos));
            frm.setEstado(EstadoContrato.CANCELADO.name());

        } catch (EntidadNoEncontradaException | EstadoInvalidoException ex) {
            JOptionPane.showMessageDialog(frm, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frm, "Número de contrato inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Buscar: abre el JDialog, carga todos los contratos y pega selección al form */
    private void abrirBuscar() {
        java.awt.Frame owner = (java.awt.Frame) SwingUtilities.getWindowAncestor(frm);
        FrmBuscarContrato dlg = new FrmBuscarContrato(owner, true);

        dlg.setResultados(repoContratos.listar());
        dlg.setLocationRelativeTo(frm);
        dlg.setVisible(true);

        Contrato sel = dlg.getContratoSeleccionado();
        if (sel != null) {
            frm.setContratoToForm(sel);
        }
    }

    // ==================== Utils ====================

    private static LocalDate parse(String s) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(s, fmt);
    }

    private static List<String> placasDisponibles(vehiculoList repo) {
        List<String> out = new ArrayList<>();
        for (Vehiculo v : repo.listar()) {
            if (v.getEstado() == EstadoVehiculo.DISPONIBLE) out.add(v.getPlaca());
        }
        return out;
    }

    private static List<String> estadoContratoComoTexto() {
        List<String> out = new ArrayList<>();
        for (EstadoContrato e : EstadoContrato.values()) out.add(e.name());
        return out;
    }

    // (Opcional) Si necesitas confirmar desde Reserva por UI en este form:
    public void crearDesdeReserva(int idReserva) {
        try {
            String s = JOptionPane.showInputDialog(frm, "Tarifa diaria:", "0");
            if (s == null) return;
            double tarifa = Double.parseDouble(s);
            Contrato cto = gestor.crearDesdeReserva(idReserva, tarifa);

            frm.setContratoToForm(cto);
            frm.setPlacas(placasDisponibles(repoVehiculos));

            JOptionPane.showMessageDialog(frm,
                    "Contrato #" + cto.getId() + " creado desde reserva. Monto: " + cto.getMonto(),
                    "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frm, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}