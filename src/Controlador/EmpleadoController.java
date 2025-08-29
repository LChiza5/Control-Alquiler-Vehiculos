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

    // ===== GUARDAR =====
    private void guardar() {
        Empleado e = frm.getEmpleadoFromForm();
        if (e == null) return;
        try {
            validarAlta(e);
            boolean ok = repo.add(e);
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

    // ===== ACTUALIZAR (ID y fecha NO se tocan; SÍ actualiza nombre, tel, correo, puesto y salario) =====
    private void actualizar() {
        Empleado e = frm.getEmpleadoFromForm();
        if (e == null) return;

        try {
            // Validar lo que sí puede cambiar + existencia
            Validators.noVacio(e.getCedula(), "ID");
            Validators.noVacio(e.getNombre(), "nombre");
            Validators.telefono(e.getTelefono(), 8);
            Validators.email(e.getCorreo());
            Validators.noVacio(e.getPuesto(), "puesto");
            if (e.getSalario() < 0) throw new ReglaDeNegocioException("El salario no puede ser negativo.");

            // 1) Actualiza en repo lo soportado
            repo.actualizar(e.getCedula(), e.getTelefono(), e.getCorreo(), e.getPuesto());

            // 2) Completa en el objeto en memoria (nombre y salario)
            Empleado actual = repo.find(e.getCedula());
            if (actual == null) throw new EntidadNoEncontradaException("Empleado no encontrado.");
            actual.setNombre(e.getNombre());
            actual.setSalario(e.getSalario());

            UtilGUI.showMessage(frm, "Empleado actualizado.", "OK");
        } catch (EntidadNoEncontradaException | ReglaDeNegocioException ex) {
            UtilGUI.showErrorMessage(frm, ex.getMessage(), "Error");
        }
    }

    // ===== ELIMINAR =====
    private void eliminar() {
        String ced = javax.swing.JOptionPane.showInputDialog(frm, "ID a eliminar:");
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

    // ===== BUSCAR (abre, centra y devuelve selección) =====
    private void abrirBuscador() {
        FrmBuscarEmpleado buscador = new FrmBuscarEmpleado();
        buscador.setRepository(repo);
        buscador.setOnSelect(emp -> {
            frm.setEmpleadoToForm(emp);
            buscador.dispose();
        });

        JDesktopPane dp = frm.getDesktopPane();
        if (dp != null) {
            dp.add(buscador);

            java.awt.Dimension d = dp.getSize();
            int w = Math.min(900, d.width  - 60);
            int h = Math.min(600, d.height - 60);
            buscador.setSize(w, h);
            buscador.setLocation((d.width - w)/2, (d.height - h)/2);

            buscador.setVisible(true);
            try { buscador.setSelected(true); } catch (Exception ignore) {}
            buscador.toFront();
        } else {
            buscador.setSize(900, 600);
            buscador.setVisible(true);
        }
    }

    // ===== Validaciones de ALTA (incluye fecha de nacimiento válida) =====
    private void validarAlta(Empleado e) throws ReglaDeNegocioException {
        Validators.noVacio(e.getCedula(), "ID");
        Validators.noVacio(e.getNombre(), "nombre");
        Validators.mayorDeEdad(e.getFechaNac());
        Validators.telefono(e.getTelefono(), 8);
        Validators.email(e.getCorreo());
        Validators.noVacio(e.getPuesto(), "puesto");
        if (e.getSalario() < 0) throw new ReglaDeNegocioException("El salario no puede ser negativo.");
    }
}
