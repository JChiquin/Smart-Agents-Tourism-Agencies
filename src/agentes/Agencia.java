/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import jade.proto.ContractNetInitiator;
import jade.proto.ContractNetResponder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import modelos.Paquete;

/**
 *
 * @author Jorge
 */
public class Agencia extends Agent {
    private vistas.Agencia gui;
    private Paquete paquete;
    
    protected void setup() {
        gui = new vistas.Agencia(this);
        gui.setVisible(true);
        
         DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(this.getAID());

        ServiceDescription sd = new ServiceDescription();       
        sd = new ServiceDescription();
        sd.setType("Agencia");
        sd.setName(this.getLocalName());
        dfd.addServices(sd);

        // Actualizar registro y actualizar GUI
        //DFService.modify(this, dfd);


        // Agregar comportamiento ContractNetResponder (Venta de libros)
        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
                MessageTemplate.MatchPerformative(ACLMessage.CFP)
        );
        addBehaviour(new ContractNetResponder(this, template) {
            protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
                ArrayList<Paquete> paquetes = gui.getPaquetes();
                paquete = new Paquete(cfp.getContent());
                if (paquetes.contains(paquete)) {
                    ACLMessage propose = cfp.createReply();
                    propose.setPerformative(ACLMessage.PROPOSE);
                    propose.setContent(String.valueOf(paquetes.get(paquetes.indexOf(paquete)).getPrecio()));
                    return propose;
                } else {
                    System.out.println("Agent " + getLocalName() + ": Refuse");
                    throw new RefuseException("No tengo el paquete");
                }
            }

            protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
                ACLMessage inform = accept.createReply();
                inform.setPerformative(ACLMessage.INFORM);
                return inform;
            }
        });

        System.out.println(this.getLocalName() + " iniciado");
    }

    protected void takeDown() {
        // Eliminar vista
        gui.dispose();
        // Eliminar agente del registro
        try {
            DFService.deregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(this.getLocalName() + " finalizado");
    }
    
    
}
