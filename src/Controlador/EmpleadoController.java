/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;
import Lists.empleadoList;
import Modelo.Empleado;
import Utils.EntidadNoEncontradaException;
import Utils.ReglaDeNegocioException;
import Utils.UtilGUI;
import Utils.Validators;
import Vista.FrmBuscarEmpleado;
import Vista.FrmEmpleado;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;

/**
 *
 * @author Luisk
 */
public class EmpleadoController {
    private final empleadoList repo;
    private final FrmEmpleado frm;

    public EmpleadoController(empleadoList repo, FrmEmpleado frm) {
        this.repo = repo;
        this.frm = frm;
        wireEvents();
    }

    private void wireEvents() {
        frm.getBtnGuardar().addActionListener(e -> guardar());
        frm.getBtnActualizar().addActionListener(e -> actualizar());
        frm.getBtnEliminar().addActionListener(e -> eliminar());
        frm.getBtnLimpiar().addActionListener(e -> frm.clearForm());
        frm.getBtnBuscar().addActionListener(e -> abrirBuscador());
    }

    // ===== Validaciones básicas (reusa tus Validators) =====
    private void validar(Empleado e) throws ReglaDeNegocioException {
        Validators.noVacio(e.getCedula(), "ID");
        Validators.noVacio(e.getNombre(), "nombre");
        Validators.mayorDeEdad(e.getFechaNac());
        Validators.email(e.getCorreo());
        Validators.telefono(e.getTelefono(), 8);
        Validators.noVacio(e.getPuesto(), "puesto");
        if (e.getSalario() < 0) throw new ReglaDeNegocioException("El salario no puede ser negativo.");
    }

    // ===== GUARDAR =====
    private void guardar() {
        Empleado e = frm.getEmpleadoFromForm();
        if (e == null) return;
        try {
            validar(e);
            boolean ok = repo.add(e); // tu repo devuelve boolean
            if (!ok) {
                UtilGUI.showErrorMessage(frm, "No se pudo guardar (ID duplicado u otra regla).", "Error");
                return;
            }
            UtilGUI.showMessage(frm, "Empleado guardado correctamente.", "OK");
            frm.clearForm();
        } catch (ReglaDeNegocioException ex) {
            UtilGUI.showErrorMessage(frm, ex.getMessage(), "Validación");
        }
    }

    // ===== ACTUALIZAR =====
    private void actualizar() {
       Empleado e = frm.getEmpleadoFromForm();
    if (e == null) return;

    try {
        // Validamos únicamente lo que sí vamos a actualizar + que exista el ID
        Validators.noVacio(e.getCedula(), "ID");
        Validators.telefono(e.getTelefono(), 8);
        Validators.email(e.getCorreo());
        Validators.noVacio(e.getPuesto(), "puesto");
        if (e.getSalario() < 0) throw new ReglaDeNegocioException("El salario no puede ser negativo.");

        // 1) Actualiza en el repositorio lo que soporta (tel/correo/puesto)
        repo.actualizar(e.getCedula(), e.getTelefono(), e.getCorreo(), e.getPuesto());

        // 2) Completa el salario directamente sobre el objeto almacenado
        Empleado actual = repo.find(e.getCedula());
        if (actual == null) throw new EntidadNoEncontradaException("Empleado no encontrado.");
        actual.setSalario(e.getSalario());

        UtilGUI.showMessage(frm, "Empleado actualizado.", "OK");
    } catch (EntidadNoEncontradaException | ReglaDeNegocioException ex) {
        UtilGUI.showErrorMessage(frm, ex.getMessage(), "Error");
        }
    }

    // ===== ELIMINAR =====
    private void eliminar() {
        String ced = JOptionPane.showInputDialog(frm, "ID a eliminar:");
        if (ced == null || ced.isBlank()) return;

        Empleado e = repo.find(ced.trim());
        if (e == null) {
            UtilGUI.showErrorMessage(frm, "No existe ese empleado.", "Error");
            return;
        }
        if (!UtilGUI.confirm(frm,
                "¿Eliminar el empleado " + e.getNombre() + " (" + e.getCedula() + ")?",
                "Confirmar")) return;

        boolean ok = repo.remove(e);
        if (!ok) {
            UtilGUI.showErrorMessage(frm, "No se pudo eliminar (revise dependencias).", "Error");
            return;
        }
        UtilGUI.showMessage(frm, "Empleado eliminado.", "OK");
        frm.clearForm();
    }

    // ===== BUSCAR: abre FrmBuscarEmpleado y devuelve la selección =====
    private void abrirBuscador() {
        FrmBuscarEmpleado buscador = new FrmBuscarEmpleado();
        buscador.setRepository(repo);
        buscador.setOnSelect(emp -> {
            frm.setEmpleadoToForm(emp);
            buscador.dispose();
        });

        // Abrir dentro del mismo DesktopPane del MDI
        JDesktopPane dp = frm.getDesktopPane();
        if (dp != null) {
            dp.add(buscador);
            buscador.setVisible(true);
            buscador.toFront();
        } else {
            buscador.setVisible(true);
        }
    }
}
