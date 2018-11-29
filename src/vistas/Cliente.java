package vistas;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.BoxLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import modelos.Paquete;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;

import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import utilidades.ValidadorNombre;
import utilidades.ValidadorPrecio;

@SuppressWarnings({"serial", "unused"})
public class Cliente extends JFrame {

    private agentes.Cliente agente;
    private JPanel contentPane;
    private JTextField textFieldDineroDisponible;
    private JTextField textFieldNombre;
    private JTextField textFieldNombreAgencia;
    private JPanel panel;
    private JPanel panel_1;
    private JPanel panel_2;
    private JPanel panel_3;
    private JLabel lblDineroDisponible;
    private JLabel lblDineroDisponible_1;
    private JLabel lblDineroDisponible_2;
    private JLabel lblNewLabel;

    public Cliente(agentes.Cliente cliente) {
        agente = cliente;
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                agente.doDelete();
            }
        });

        setTitle(cliente.getLocalName());
        setBounds(100, 100, 400, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        

        

        JLabel lblNewLabel = new JLabel("Hola soy " + cliente.getLocalName());
        add(lblNewLabel);
        lblNewLabel = new JLabel("AID: " + cliente.getAID());
        add(lblNewLabel);
        lblNewLabel = new JLabel("Estado: " + cliente.getAgentState() );
        add(lblNewLabel);
        

        panel = new JPanel();
        add(panel);
        FormLayout fl_panel = new FormLayout(new ColumnSpec[] {
                FormFactory.RELATED_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,
                FormFactory.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),},
            new RowSpec[] {
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,});
        panel.setLayout(fl_panel);

        lblDineroDisponible = new JLabel("Nombre");
        panel.add(lblDineroDisponible, "2, 2, right, default");

        textFieldNombre = new JTextField();
        panel.add(textFieldNombre, "4, 2, fill, default");
        textFieldNombre.setInputVerifier(new ValidadorNombre());

        lblDineroDisponible_1 = new JLabel("Dinero disponible");
        panel.add(lblDineroDisponible_1, "2, 4");

        textFieldDineroDisponible = new JTextField();
        panel.add(textFieldDineroDisponible, "4, 4, fill, default");
        textFieldDineroDisponible.setInputVerifier(new ValidadorPrecio());

        panel_1 = new JPanel();
        add(panel_1);

        JButton btnComprar = new JButton("Comprar paquete");
        panel_1.add(btnComprar);
        btnComprar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(validarCampos()) {
                    Paquete paquete = new Paquete(textFieldNombre.getText().trim(),
                            Integer.parseInt(textFieldDineroDisponible.getText()));
                    agente.buscarPaquete(paquete);
                }
            }
        });
        

    }

    private boolean validarCampos() {
        return textFieldNombre.getInputVerifier().verify(textFieldNombre)
                && textFieldDineroDisponible.getInputVerifier().verify(textFieldDineroDisponible);
    }
    public void aviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
}
