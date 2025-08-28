/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Lists.clienteList;
import Modelo.Cliente;
import Utils.EntidadNoEncontradaException;
import Utils.ReglaDeNegocioException;
import Vista.FrmCliente;
import javax.swing.JOptionPane;

/**
 *
 * @author ilope
 */
public class ClienteController {
    private final clienteList repo;
    private final FrmCliente vista;

    public ClienteController(clienteList repo, FrmCliente vista) {
        this.repo = repo;
        this.vista = vista;

        // Enlazar eventos de la vista con acciones del controlador
        vista.onGuardar(e -> guardar());
        vista.onBuscar(e -> buscar());
        vista.onActualizar(e -> {
            try {
                actualizar();
            } catch (EntidadNoEncontradaException | ReglaDeNegocioException ex) {
                JOptionPane.showMessageDialog(vista, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        vista.onEliminar(e -> {
            try {
                eliminar();
            } catch (EntidadNoEncontradaException ex) {
                JOptionPane.showMessageDialog(vista, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // ──────────── Métodos de operaciones ────────────

    private void guardar() {
        try {
            Cliente c = vista.getClienteFromForm();
            repo.add(c);
            JOptionPane.showMessageDialog(vista, "Cliente agregado correctamente");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(vista, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscar() {
        Cliente c = repo.find(vista.getCedula());
        if (c != null) {
            JOptionPane.showMessageDialog(vista, "Encontrado: " + c);
        } else {
            JOptionPane.showMessageDialog(vista, "Cliente no encontrado");
        }
    }

    private void eliminar() throws EntidadNoEncontradaException {
        Cliente c = repo.find(vista.getCedula());
        if (c == null) throw new EntidadNoEncontradaException("Cliente no existe");

        repo.remove(c);
        JOptionPane.showMessageDialog(vista, "Cliente eliminado correctamente");
    }

    private void actualizar() throws EntidadNoEncontradaException, ReglaDeNegocioException {
        String cedula   = vista.getCedula();
        String telefono = vista.getTelefono();
        String correo   = vista.getCorreo();
        String licencia = vista.getLicencia();

        // Validar existencia
        Cliente existente = repo.find(cedula);
        if (existente == null) throw new EntidadNoEncontradaException("Cliente no existe");

        // Actualizar datos
        repo.actualizarContacto(cedula, telefono, correo, licencia);

        JOptionPane.showMessageDialog(vista, "Cliente actualizado correctamente");
    }
}