/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Vista;

/**
 *
 * @author Luisk
 */
public class FrmEmpleado extends javax.swing.JInternalFrame {

public javax.swing.JButton getBtnGuardar()   { return btnGuardar; }   
public javax.swing.JButton getBtnActualizar(){ return btnActualizar; } 
public javax.swing.JButton getBtnEliminar()  { return btnEliminar; }   
public javax.swing.JButton getBtnLimpiar()   { return btnLimpiar; }    
public javax.swing.JButton getBtnBuscar()    { return btnBuscar; }     


private javax.swing.JTextField getTxtId()        { return txtId1; }
private javax.swing.JTextField getTxtNombre()    { return txtName1; }
private javax.swing.JFormattedTextField getTxtFechaNac() { return txtBirthdate; } 
private javax.swing.JComboBox<String> getCbPuesto(){ return txtPuesto1; }
private javax.swing.JTextField getTxtSalario()   { return txtSalario1; }
private javax.swing.JTextField getTxtCorreo()    { return txtCorreo; }
private javax.swing.JTextField getTxtTelefono()  { return txtTelefono; }

public Modelo.Empleado getEmpleadoFromForm() {
    try {
        String id        = getTxtId().getText().trim();
        String nombre    = getTxtNombre().getText().trim();

        java.time.LocalDate nacimiento;
        try {
            String dateText = txtBirthdate.getText().replace("_","").trim();
            nacimiento = java.time.LocalDate.parse(
                    dateText,
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
            );
        } catch (java.time.format.DateTimeParseException ex) {
            Utils.UtilGUI.showErrorMessage(this, "Fecha inválida. Use dd/MM/yyyy.", "Error");
            return null;
        }

        String puesto    = (String) getCbPuesto().getSelectedItem();
        String salarioTx = getTxtSalario().getText().trim();
        String digits    = salarioTx.replaceAll("\\D", "");              // ← cambio
        double salario   = digits.isEmpty() ? 0.0 : Double.parseDouble(digits); // ← cambio
        String correo    = getTxtCorreo().getText().trim();
        String telefono  = getTxtTelefono().getText().trim();

        return new Modelo.Empleado(id, nombre, nacimiento, telefono, correo, puesto, salario);

    } catch (NumberFormatException nfe) {
        Utils.UtilGUI.showErrorMessage(this, "Salario inválido (solo números).", "Error");
        return null;
    } catch (Exception ex) {
        Utils.UtilGUI.showErrorMessage(this, "Datos incompletos o fecha inválida (dd/MM/yyyy).", "Error");
        return null;
    }
}

public void setEmpleadoToForm(Modelo.Empleado e) {
    if (e == null) return;

    // ID (si viene nulo/ vacío, dejamos los guiones de la máscara)
    String ced = e.getCedula();
    getTxtId().setText((ced == null || ced.isBlank()) ? "--" : ced);

    // Nombre, Teléfono, Correo
    getTxtNombre().setText(e.getNombre() == null ? "" : e.getNombre());
    getTxtTelefono().setText(e.getTelefono() == null ? "" : e.getTelefono());
    getTxtCorreo().setText(e.getCorreo() == null ? "" : e.getCorreo());

    // Puesto (combo editable)
    if (e.getPuesto() != null) {
        getCbPuesto().setSelectedItem(e.getPuesto());
    } else if (getCbPuesto().getItemCount() > 0) {
        getCbPuesto().setSelectedIndex(0);
    }

    // Salario con separador de miles (punto)
    java.text.DecimalFormatSymbols sym = new java.text.DecimalFormatSymbols();
    sym.setGroupingSeparator('.');
    java.text.DecimalFormat df = new java.text.DecimalFormat("#,###", sym);
    long salLong = Math.round(e.getSalario()); // por si está en double
    getTxtSalario().setText(df.format(salLong));

    // Fecha de nacimiento (dd/MM/yyyy)
    if (e.getFechaNac() != null) {
        getTxtFechaNac().setText(
            e.getFechaNac().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
    } else {
        getTxtFechaNac().setText("");
    }
}

private void instalarMascaraCedula(javax.swing.JTextField field) {
    javax.swing.text.AbstractDocument doc = (javax.swing.text.AbstractDocument) field.getDocument();
    doc.setDocumentFilter(new javax.swing.text.DocumentFilter() {
        private String soloDigitos(String s) { return s == null ? "" : s.replaceAll("\\D", ""); }

        private String formatear(String digits) {
            if (digits.length() > 9) digits = digits.substring(0, 9);
            String p1 = digits.length() >= 1 ? digits.substring(0, 1) : "";
            String p2 = digits.length() >= 2 ? digits.substring(1, Math.min(5, digits.length())) : "";
            String p3 = digits.length() >= 6 ? digits.substring(5, Math.min(9, digits.length())) : "";
            // Siempre mostramos los guiones, aunque falten dígitos
            return p1 + "-" + p2 + "-" + p3;
        }

        private String recomponer(String actualConGuiones, int offset, String textoNuevo, int lengthToRemove) {
            // 1) Tomar el contenido actual y quitar guiones
            String actualDigits = soloDigitos(actualConGuiones);
            // 2) Calcular cómo quedaría tras remover y luego insertar (en términos de dígitos)
            //    Mapeo: posiciones visibles -> índice de dígitos (ignorando guiones)
            StringBuilder visibles = new StringBuilder(actualConGuiones);
            visibles.replace(offset, offset + lengthToRemove, textoNuevo == null ? "" : textoNuevo);
            String futurosDigits = soloDigitos(visibles.toString());
            // 3) Limitar a 9 dígitos y re-formatear
            if (futurosDigits.length() > 9) futurosDigits = futurosDigits.substring(0, 9);
            return formatear(futurosDigits);
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr)
                throws javax.swing.text.BadLocationException {
            String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
            String nuevo = recomponer(actual, offset, string, 0);
            fb.replace(0, actual.length(), nuevo, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs)
                throws javax.swing.text.BadLocationException {
            String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
            String nuevo = recomponer(actual, offset, text, length);
            fb.replace(0, actual.length(), nuevo, attrs);
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length)
                throws javax.swing.text.BadLocationException {
            String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
            String nuevo = recomponer(actual, offset, "", length);
            fb.replace(0, actual.length(), nuevo, null);
        }
    });

    // Dejar los guiones visibles aunque esté vacío
    field.setText("--");
}

// ==== Formato de miles "1.000.000" sobre un JTextField ====
private void instalarFormatoMiles(javax.swing.JTextField field) {
    final java.text.DecimalFormatSymbols sym = new java.text.DecimalFormatSymbols();
    sym.setGroupingSeparator('.');   // separador de miles .
    sym.setDecimalSeparator(',');    // (no usaremos decimales)
    final java.text.DecimalFormat df = new java.text.DecimalFormat("#,###", sym);
    df.setGroupingUsed(true);

    javax.swing.text.AbstractDocument doc = (javax.swing.text.AbstractDocument) field.getDocument();
    doc.setDocumentFilter(new javax.swing.text.DocumentFilter() {

        private String onlyDigits(String s) { return s == null ? "" : s.replaceAll("\\D", ""); }

        private String formatDigits(String digits) {
            if (digits.isEmpty()) return "";
            // hasta 12 dígitos por seguridad
            if (digits.length() > 12) digits = digits.substring(0, 12);
            long value = Long.parseLong(digits);
            return df.format(value);
        }

        private String recompute(javax.swing.text.Document doc,
                                 int offset, int length, String text) throws javax.swing.text.BadLocationException {
            String current = doc.getText(0, doc.getLength());
            StringBuilder sb = new StringBuilder(current);
            sb.replace(offset, offset + length, text == null ? "" : text);
            String digits = onlyDigits(sb.toString());
            return formatDigits(digits);
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr)
                throws javax.swing.text.BadLocationException {
            String nuevo = recompute(fb.getDocument(), offset, 0, string);
            fb.replace(0, fb.getDocument().getLength(), nuevo, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs)
                throws javax.swing.text.BadLocationException {
            String nuevo = recompute(fb.getDocument(), offset, length, text);
            fb.replace(0, fb.getDocument().getLength(), nuevo, attrs);
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length)
                throws javax.swing.text.BadLocationException {
            String nuevo = recompute(fb.getDocument(), offset, length, "");
            fb.replace(0, fb.getDocument().getLength(), nuevo, null);
        }
    });
}

public void clearForm() {
    txtId1.setText("--");                  
    txtName1.setText("");
    txtBirthdate.setText("");
    if (txtPuesto1.getItemCount() > 0) txtPuesto1.setSelectedIndex(0);
    txtSalario1.setText("");              
    txtCorreo.setText("");
    txtTelefono.setText("");
}
        
    /**
     * Creates new form FrmEmpleado
     */
    public FrmEmpleado() {
        initComponents();
        instalarMascaraCedula(txtId1);
        txtPuesto1.setEditable(true);
        instalarFormatoMiles(txtSalario1);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnBuscar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jLabelEmpleados1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtPuesto1 = new javax.swing.JComboBox<>();
        txtName1 = new javax.swing.JTextField();
        txtId1 = new javax.swing.JTextField();
        txtSalario1 = new javax.swing.JTextField();
        txtBirthdate = new javax.swing.JFormattedTextField();
        txtCorreo = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();

        setBackground(new java.awt.Color(153, 153, 153));

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/3floppy_unmount (4).png"))); // NOI18N
        btnGuardar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnActualizar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnActualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/actualizar-flecha.png"))); // NOI18N
        btnActualizar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnEliminar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/application_exit (4).png"))); // NOI18N
        btnEliminar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnBuscar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/deskbar-applet.png"))); // NOI18N
        btnBuscar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnLimpiar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnLimpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/application_vnd.oasis.opendocument.spreadsheet (4).png"))); // NOI18N
        btnLimpiar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(197, 197, 197))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnActualizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jLabelEmpleados1.setBackground(new java.awt.Color(204, 204, 204));
        jLabelEmpleados1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabelEmpleados1.setForeground(new java.awt.Color(0, 0, 0));
        jLabelEmpleados1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelEmpleados1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/contact (4).png"))); // NOI18N
        jLabelEmpleados1.setText("Empleados");
        jLabelEmpleados1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelEmpleados1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabelEmpleados1.setOpaque(true);

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/tarjeta-de-identificacion (1).png"))); // NOI18N
        jLabel4.setText("ID");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/nombre.png"))); // NOI18N
        jLabel7.setText("Nombre");
        jLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/empleado-de-oficina.png"))); // NOI18N
        jLabel8.setText("Puesto");
        jLabel8.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel9.setBackground(new java.awt.Color(153, 153, 153));
        jLabel9.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/fecha-de-nacimiento.png"))); // NOI18N
        jLabel9.setText("Fecha De Nacimiento");
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel10.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/ingreso.png"))); // NOI18N
        jLabel10.setText("Salario");
        jLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        txtPuesto1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        txtPuesto1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtPuesto1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPuesto1ActionPerformed(evt);
            }
        });

        txtName1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        txtName1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        txtId1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        txtId1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        txtSalario1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        txtSalario1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtSalario1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSalario1ActionPerformed(evt);
            }
        });

        txtBirthdate.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtBirthdate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("dd/MM/yyyy"))));
        txtBirthdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBirthdateActionPerformed(evt);
            }
        });

        txtCorreo.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel11.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/gmail.png"))); // NOI18N
        jLabel11.setText("Correo");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel12.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/telefono.png"))); // NOI18N
        jLabel12.setText("Telefono");
        jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        txtTelefono.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtId1, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtName1, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(46, 46, 46)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtBirthdate))
                .addGap(37, 37, 37)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPuesto1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(57, 57, 57)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSalario1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(24, 24, 24))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(223, 223, 223)
                        .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(60, 60, 60)
                        .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(281, 281, 281)
                        .addComponent(jLabel11)
                        .addGap(137, 137, 137)
                        .addComponent(jLabel12)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(jLabel8)
                        .addComponent(jLabel10)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtName1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtId1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPuesto1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSalario1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBirthdate))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtCorreo)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelEmpleados1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabelEmpleados1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtPuesto1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPuesto1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPuesto1ActionPerformed

    private void txtSalario1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSalario1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSalario1ActionPerformed

    private void txtBirthdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBirthdateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBirthdateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelEmpleados1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JFormattedTextField txtBirthdate;
    private javax.swing.JFormattedTextField txtCorreo;
    private javax.swing.JTextField txtId1;
    private javax.swing.JTextField txtName1;
    private javax.swing.JComboBox<String> txtPuesto1;
    private javax.swing.JTextField txtSalario1;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
