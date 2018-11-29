/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miniproyectoagentes;

import javax.swing.UIManager;
import jade.core.Runtime; 
import jade.core.Profile; 
import jade.core.ProfileImpl; 
import jade.wrapper.*;

/**
 *
 * @author Jorge
 */
public class MiniProyectoAgentes {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       try {
            // Usar look & feel nativo en las vistas
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        // Plataforma JADE
        Runtime runtime = Runtime.instance();

        // Perfil predetermiando (localhost:1099)
        Profile profile = new ProfileImpl();

        // Contenedor principal
        AgentContainer mainContainer = runtime.createMainContainer(profile);

        // Crear agentes
        try {
            // RMA (Jade Boot GUI)
            AgentController ac = mainContainer.createNewAgent("rma",
                    "jade.tools.rma.rma", null);
            ac.start();

            // Planificador
            ac = mainContainer.createNewAgent("Cliente",
                    "agentes.Cliente", null);
            ac.start();

            // 4 personas
            String[] personas = {"Eleazar", "Stefan", "German", "Bachaquero"};
            for(int i = 0; i < personas.length; i++) {
                ac = mainContainer.createNewAgent(personas[i],
                        "agentes.Agencia", null);
                ac.start();
            }
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
    
}
