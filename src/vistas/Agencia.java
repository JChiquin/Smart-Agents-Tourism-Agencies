/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vistas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import modelos.Paquete;

/**
 *
 * @author Jorge
 */
public class Agencia extends JFrame {
    
    private agentes.Agencia agente;
    private JPanel contentPane;
    private JTable tablaPaquetes;
    private JButton btnEliminar;
    private JButton btnEditar;
    private ModeloTablaPaquetes modeloTablaPaquetes;

    /**
     * Create the frame.
     */
    public Agencia(agentes.Agencia agencia) {
        agente = agencia;
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                agente.doDelete();
            }
        });

        setTitle(agencia.getLocalName());
        setBounds(100, 100, 400, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        

        

        JLabel lblNewLabel = new JLabel("Vendedor");
        add(lblNewLabel);

        modeloTablaPaquetes = new ModeloTablaPaquetes(new ArrayList<Paquete>());

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane);

        tablaPaquetes = new JTable();
        tablaPaquetes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPaquetes.setModel(modeloTablaPaquetes);
        scrollPane.setViewportView(tablaPaquetes);
        tablaPaquetes.setColumnSelectionAllowed(false);
        tablaPaquetes.setRowSelectionAllowed(true);
        tablaPaquetes.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                boolean enable = tablaPaquetes.getSelectedRow() > -1;
                btnEditar.setEnabled(enable);
                btnEliminar.setEnabled(enable);
            }
        });

        JPanel panel_1 = new JPanel();
        add(panel_1);

        JButton btnAgregar = new JButton("Agregar");
        panel_1.add(btnAgregar);

        btnEditar = new JButton("Editar");
        panel_1.add(btnEditar);
        btnEditar.setEnabled(false);
        btnEditar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tablaPaquetes.getSelectedRow();
                if (selectedRow > -1) {
                    Paquete paqueteEditado = modeloTablaPaquetes.getPaquete(selectedRow);
                    AgenciaPaquete editarPaquete = new AgenciaPaquete(Agencia.this, "Editar Paquete", true, paqueteEditado);
                    paqueteEditado = editarPaquete.mostrar();
                    modeloTablaPaquetes.actualizarPaquete(selectedRow, paqueteEditado);
                }
            }
        });

        btnEliminar = new JButton("Eliminar");
        panel_1.add(btnEliminar);
        btnEliminar.setEnabled(false);
        btnEliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tablaPaquetes.getSelectedRow();
                if (selectedRow > -1) {
                    modeloTablaPaquetes.eliminarPaquete(selectedRow);
                }
            }
        });

        btnAgregar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AgenciaPaquete agregarPaquete = new AgenciaPaquete(Agencia.this, "Agregar Paquete", true, null);
                Paquete paquete = agregarPaquete.mostrar();
                if(paquete != null) {
                    if(!getPaquetes().contains(paquete)) {
                        modeloTablaPaquetes.agregarPaquete(paquete);
                    } else {
                        JOptionPane.showMessageDialog(Agencia.this, "El Paquete '" + paquete.getNombre() + "' ya existe");
                    }
                }
            }
        });
    }

    // Metodo que usaría el agente comprador para obtener sus Paquetes
    public ArrayList<Paquete> getPaquetes() {
        return modeloTablaPaquetes.getPaquetes();
    }
    

    // Modelo de tabla para manejar Paquetes 
    private class ModeloTablaPaquetes extends AbstractTableModel {
        private ArrayList<Paquete> Paquetes;
        private String[] columnas = {"Título", "Precio"};

        public ModeloTablaPaquetes(ArrayList<Paquete> Paquetes) {
            super();
            this.Paquetes = Paquetes;
        }

        public String getColumnName(int col) {
            return columnas[col];
        }

        @Override
        public int getColumnCount() {
            return columnas.length;
        }

        @Override
        public int getRowCount() {
            return Paquetes.size();
        }

        @Override
        public Object getValueAt(int row, int col) {
            Object object = null;
            switch(col) {
            case 0:
                object = (Object) Paquetes.get(row).getNombre();
                break;
            case 1:
                object = (Object) Paquetes.get(row).getPrecio();
                break;
            }
            return object;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        public void agregarPaquete(Paquete Paquete) {
            Paquetes.add(Paquete);
            fireTableDataChanged();
        }

        public Paquete getPaquete(int index) {
            return Paquetes.get(index);
        }

        public void actualizarPaquete(int index, Paquete PaqueteActualizado) {
            Paquetes.set(index, PaqueteActualizado);
            fireTableDataChanged();
        }

        public void eliminarPaquete(int index) {
            Paquetes.remove(index);
            fireTableDataChanged();
        }

        public ArrayList<Paquete> getPaquetes() {
            return Paquetes;
        }
    }
    public void aviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
    
}
