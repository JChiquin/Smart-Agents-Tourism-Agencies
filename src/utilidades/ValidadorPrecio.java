package utilidades;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

public class ValidadorPrecio extends InputVerifier {
    @Override
    public boolean verify(JComponent comp) {
        JTextField textField = (JTextField) comp;

        boolean valido = false;
        try {
            int precio = Integer.parseInt(textField.getText());
            valido = precio >= 0;
        } catch (NumberFormatException e) {}

        Color color = valido ? null : Color.red;
        comp.setBorder(BorderFactory.createLineBorder(color));

        return valido;
    }
}