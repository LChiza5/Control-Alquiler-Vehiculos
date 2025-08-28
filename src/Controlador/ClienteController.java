/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Cliente;
import Utils.ReglaDeNegocioException;
import Utils.UtilGUI;
import Utils.Validators;



/**
 *
 * @author ilope
 */
public class ClienteController {
    private final Vista.FrmCliente frm;
private final Lists.clienteList repo;

public ClienteController(Vista.FrmCliente frm, Lists.clienteList repo) {
    this.frm = frm;
    this.repo = repo;
    wireEvents();
}

private void wireEvents() {
    // Al pulsar Buscar → abrir diálogo y cargar selección
    frm.onBuscar(e -> frm.abrirDialogoBusqueda(repo));

    // (Ejemplos de otros botones por si aún no los conectaste)
    frm.onGuardar(e -> {
            Cliente c = frm.getClienteFromForm();
            if (c == null) return; // la vista ya mostró mensaje si algo faltó

            try {
                // === VALIDACIONES DE NEGOCIO ===
                Validators.noVacio(c.getCedula(), "cédula");
                Validators.noVacio(c.getLicencia(), "licencia");
                Validators.mayorDeEdad(c.getFechaNac());
                Validators.telefono(c.getTelefono(), 8);
                Validators.email(c.getCorreo());

                boolean ok = repo.add(c);
                if (!ok) {
                    UtilGUI.showErrorMessage(frm, "No se pudo guardar (duplicado o inválido)", "Error");
                    return;
                }

                UtilGUI.showMessage(frm, "Cliente guardado", "OK");
                frm.clearForm();

            } catch (ReglaDeNegocioException ex) {
                // AQUÍ se muestran los mensajes de Validators
                UtilGUI.showErrorMessage(frm, ex.getMessage(), "Error de validación");
            }
        });

    frm.onActualizar(e -> {
            String ced = frm.getCedula();
            Cliente existe = repo.find(ced);
            if (existe == null) {
                UtilGUI.showErrorMessage(frm, "Cliente inexistente (Seleccione uno existente).", "Error");
                return;
            }
            Cliente c = frm.getClienteFromForm();
            if (c == null) return;

            try {
                // Revalida campos importantes
                Validators.telefono(c.getTelefono(), 8);
                Validators.email(c.getCorreo());

                // Actualiza campos permitidos
                existe.setNombre(c.getNombre());
                existe.setTelefono(c.getTelefono());
                existe.setCorreo(c.getCorreo());
                existe.setLicencia(c.getLicencia());
                existe.setFechaNac(c.getFechaNac());

                UtilGUI.showMessage(frm, "Cliente actualizado", "OK");
            } catch (ReglaDeNegocioException ex) {
                UtilGUI.showErrorMessage(frm, ex.getMessage(), "Error de validación");
            }
        });


    frm.onEliminar(e -> {
            String ced = frm.getCedula();
            Cliente c = repo.find(ced);
            if (c == null) {
                UtilGUI.showErrorMessage(frm, "No existe ese cliente (Seleccione uno existente).", "Error");
                return;
            }
            if (!UtilGUI.confirm(frm, "¿Eliminar el cliente " + c.getNombre() + "?", "Confirmar")) return;

            boolean ok = repo.remove(c); // este debe bloquear si tiene reservas activas
            if (!ok) {
                UtilGUI.showErrorMessage(frm, "No se puede eliminar: tiene reservas activas.", "Error");
                return;
            }
            UtilGUI.showMessage(frm, "Cliente eliminado", "OK");
            frm.clearForm();
        });
    }
}