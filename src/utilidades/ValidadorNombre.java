package utilidades;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

public class ValidadorNombre extends InputVerifier {
    @Override
    public boolean verify(JComponent comp) {
        JTextField textField = (JTextField) comp;

        boolean valido = textField.getText().trim().length() > 0;

        Color color = valido ? null : Color.red;
        comp.setBorder(BorderFactory.createLineBorder(color));

        return valido;
    }
}