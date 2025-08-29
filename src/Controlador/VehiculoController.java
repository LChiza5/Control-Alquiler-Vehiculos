/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Lists.vehiculoList;
import Modelo.Vehiculo;
import Utils.EntidadNoEncontradaException;
import Utils.ReglaDeNegocioException;
import Utils.UtilGUI;
import Utils.Validators;
import Vista.BuscarVehiculos;
import Vista.FrmVehiculo;
import javax.swing.SwingUtilities;

/**
 *
 * @author ilope
 */
public class VehiculoController {

    private final vehiculoList repo;
    private final FrmVehiculo frm;

    public VehiculoController(vehiculoList repo, FrmVehiculo frm) {
        this.repo = repo;
        this.frm = frm;
        wireEvents();
    }

    private void wireEvents() {
        frm.getBtnSave().addActionListener(e -> guardar());
        frm.getBtnUpdate().addActionListener(e -> actualizar());
        frm.getBtnEliminar().addActionListener(e -> eliminar());
        frm.getBtnSearch().addActionListener(e -> buscar());
        frm.getBtnClear().addActionListener(e -> frm.clearForm());
    }

    // Guardar
    private void guardar() {
        Vehiculo v = frm.getVehiculoFromForm();
        if (v == null) {
            UtilGUI.showErrorMessage(frm, "Datos incompletos", "Error");
            return;
        }
        try {
            // Validaciones
            Validators.noVacio(v.getPlaca(), "placa");
            Validators.noVacio(v.getMarca(), "marca");
            Validators.noVacio(v.getModelo(), "modelo");
            Validators.anioVehiculoValido(v.getAnio(), 20);
            if (v.getTipo() == null) throw new ReglaDeNegocioException("Debe seleccionar un tipo");
            if (v.getEstado() == null) throw new ReglaDeNegocioException("Debe seleccionar un estado");

            boolean ok = repo.add(v);
            if (!ok) {
                UtilGUI.showErrorMessage(frm, "No se pudo guardar (placa duplicada o inválida).", "Error");
                return;
            }
            UtilGUI.showMessage(frm, "Vehículo guardado", "OK");
            frm.clearForm();

        } catch (ReglaDeNegocioException ex) {
            UtilGUI.showErrorMessage(frm, ex.getMessage(), "Error de validación");
        }
    }

    // Actualizar
    private void actualizar() {
        Vehiculo v = frm.getVehiculoFromForm();
        if (v == null) {
            UtilGUI.showErrorMessage(frm, "Datos incompletos", "Error");
            return;
        }
        try {
            Validators.noVacio(v.getModelo(), "modelo");
            if (v.getTipo() == null) throw new ReglaDeNegocioException("Debe seleccionar un tipo");
            if (v.getEstado() == null) throw new ReglaDeNegocioException("Debe seleccionar un estado");

            repo.actualizar(v.getPlaca(), v.getModelo(), v.getTipo(), v.getEstado());
            UtilGUI.showMessage(frm, "Vehículo actualizado", "OK");
        } catch (EntidadNoEncontradaException | ReglaDeNegocioException ex) {
            UtilGUI.showErrorMessage(frm, ex.getMessage(), "Error");
        }
    }

    // Eliminar
    private void eliminar() {
        Vehiculo v = frm.getVehiculoFromForm();
        if (v == null || v.getPlaca().isBlank()) {
            UtilGUI.showErrorMessage(frm, "Debe ingresar la placa del vehículo", "Error");
            return;
        }
        try {
            Vehiculo buscado = repo.find(v.getPlaca());
            if (buscado == null) throw new EntidadNoEncontradaException("Vehículo no encontrado: " + v.getPlaca());

            if (!UtilGUI.confirm(frm, "¿Eliminar el vehículo " + v.getPlaca() + "?", "Confirmar")) return;

            boolean ok = repo.remove(buscado);
            if (!ok) {
                UtilGUI.showErrorMessage(frm, "No se puede eliminar: el vehículo está en alquiler.", "Error");
                return;
            }
            UtilGUI.showMessage(frm, "Vehículo eliminado", "OK");
            frm.clearForm();

        } catch (EntidadNoEncontradaException ex) {
            UtilGUI.showErrorMessage(frm, ex.getMessage(), "Error");
        }
    }

    // Buscar con JDialog
    private void buscar() {
        java.awt.Frame owner = (java.awt.Frame) SwingUtilities.getWindowAncestor(frm);
        BuscarVehiculos dlg = new BuscarVehiculos(owner, true);
        dlg.setList(repo);
        dlg.setLocationRelativeTo(frm);
        dlg.setVisible(true);

        Vehiculo seleccionado = dlg.getVehiculoSeleccionado();
        if (seleccionado != null) {
            frm.setVehiculoToForm(seleccionado);
        }
    }
}