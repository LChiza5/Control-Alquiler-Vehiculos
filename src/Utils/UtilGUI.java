/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

import java.awt.Color;
import java.awt.Component;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Luisk
 */
public final class UtilGUI {

    private UtilGUI() {}

    // ======== MENSAJES ========
    private static void showMessage(Component parent, Object message, String title, int messageType) {
        JOptionPane.showMessageDialog(parent, message, title, messageType);
    }

    public static void showMessage(Component parent, Object message, String title) {
        showMessage(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showErrorMessage(Component parent, Object message, String title) {
        showMessage(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(Component parent, String message, String title) {
        int r = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION);
        return r == JOptionPane.YES_OPTION;
    }

    // ======== VALIDACIÓN DE CAMPOS ========
    /** Devuelve true si el componente tiene un valor no vacío. */
    private static boolean hasValue(JComponent c) {
        if (c instanceof JTextComponent text) {
            String s = text.getText();
            return s != null && !s.trim().isEmpty();
        }
        if (c instanceof JComboBox<?> combo) {
            Object i = combo.isEditable() ? combo.getEditor().getItem() : combo.getSelectedItem();
            return i != null && !i.toString().trim().isEmpty();
        }
        if (c instanceof JSpinner sp) {
            Object v = sp.getValue();
            return v != null && !String.valueOf(v).trim().isEmpty();
        }
        if (c instanceof JCheckBox ch) {
            // Un checkbox siempre "tiene" un valor; deja true
            return true;
        }
        if (c instanceof JRadioButton rb) {
            // El valor lo decide el ButtonGroup, así que aquí devuelve true
            return true;
        }
        // Por defecto, considérese válido
        return true;
    }

    /** Valida que todos los componentes tengan valor. No muestra mensajes. */
    public static boolean validateRequiereSilent(JComponent... comps) {
        for (JComponent c : comps) {
            if (!hasValue(c)) return false;
        }
        return true;
    }

    /**
     * Valida requeridos y si falla:
     * - resalta el primer campo vacío,
     * - hace requestFocus,
     * - muestra mensaje.
     */
    public static boolean validateRequiere(Component parent, JComponent... comps) {
        for (JComponent c : comps) {
            if (!hasValue(c)) {
                highlightOnce(c);
                c.requestFocusInWindow();
                showErrorMessage(parent, "Complete los campos marcados.", "Campos obligatorios");
                return false;
            }
        }
        return true;
    }

    private static void highlightOnce(JComponent c) {
        Color old = c.getBackground();
        c.setBackground(new Color(255, 240, 240));
        // vuelta al color original cuando gane foco de nuevo:
        c.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusLost(java.awt.event.FocusEvent e) { c.setBackground(old); }
        });
    }

    // ======== PARSEOS Y FORMATEOS ========
    private static final DateTimeFormatter DF_DDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Intenta parsear LocalDate (dd/MM/yyyy) desde un JTextComponent.
     * Si obligatorio=true y está vacío o es inválido, muestra mensaje y devuelve null.
     * Si obligatorio=false y está vacío, devuelve null sin mensaje.
     */
    public static LocalDate parseLocalDate(Component parent, JTextComponent field, boolean obligatorio) {
        String txt = field.getText();
        if (txt == null || txt.trim().isEmpty()) {
            if (obligatorio) {
                showErrorMessage(parent, "Fecha requerida (formato dd/MM/yyyy).", "Fecha inválida");
            }
            return null;
        }
        try {
            return LocalDate.parse(txt.trim(), DF_DDMMYYYY);
        } catch (DateTimeParseException ex) {
            showErrorMessage(parent, "Fecha inválida. Use dd/MM/yyyy", "Fecha inválida");
            return null;
        }
    }

    /** Confirma la edición de un JFormattedTextField para que su valor “asiente”. */
    public static void commitEdit(JFormattedTextField f) {
        try {
            f.commitEdit();
        } catch (ParseException ignored) {
            // No mostramos nada aquí; el flujo ya valida por otro lado.
        }
    }

    // ======== VALIDACIONES BÁSICAS ========
    public static boolean email(String correo) {
        if (correo == null) return false;
        String s = correo.trim();
        // regex básica
        return s.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean telefono8(String tel) {
        if (tel == null) return false;
        String s = tel.trim();
        return s.matches("^\\d{8}$");
    }

    // ======== JTABLE HELPERS ========
    /**
     * Obtiene el valor de la columna 'keyColumn' de la fila seleccionada,
     * respetando un posible RowSorter. Devuelve null si no hay selección.
     */
    public static Object getSelectedKey(JTable table, int keyColumn) {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int modelRow = table.convertRowIndexToModel(viewRow);
        return table.getModel().getValueAt(modelRow, keyColumn);
    }
}