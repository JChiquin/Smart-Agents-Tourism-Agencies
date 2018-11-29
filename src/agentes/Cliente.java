package agentes;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import jade.core.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import jade.proto.ContractNetInitiator;
import jade.proto.ContractNetResponder;

import modelos.Paquete;

@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class Cliente extends Agent {
    private vistas.Cliente gui;
    private Paquete paquete;
    private int dineroDisponible;
    private int mejorPrecio;
    private ArrayList<String> vendedores;

    protected void setup() {
        gui = new vistas.Cliente(this);
        gui.setVisible(true);
         DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(this.getAID());

        ServiceDescription sd = new ServiceDescription();       
        sd = new ServiceDescription();
        sd.setType("Cliente");
        sd.setName(this.getLocalName());
        dfd.addServices(sd);

        // Actualizar registro y actualizar GUI
        //DFService.modify(this, dfd);

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


    public ArrayList<String> buscarVendedores() {
        ArrayList<String> vendedores = new ArrayList<String>();

        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Vendedor");
        dfd.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, dfd);
            for(int i = 0; i < result.length; i++) {
                vendedores.add(result[i].getName().getLocalName());
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        return vendedores;
    }
    
    public void buscarPaquete(Paquete paquete) {
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        vendedores = buscarVendedores();
        Iterator<String> it = vendedores.iterator();
        while(it.hasNext()) {
            msg.addReceiver(new AID(it.next(), AID.ISLOCALNAME));
        }
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        msg.setReplyByDate(new Date(System.currentTimeMillis() + 5000));
        msg.setContent(paquete.getNombre());
        dineroDisponible = paquete.getPrecio();

        addBehaviour(new ContractNetInitiator(this, msg) {
            protected void handlePropose(ACLMessage propose, Vector v) {
                System.out.println("Vendedor " + propose.getSender().getLocalName() +
                        " ofrece el paquete en Bs." + propose.getContent());
            }

            protected void handleRefuse(ACLMessage refuse) {
                System.out.println(refuse.getSender().getLocalName() +
                        ": " + refuse.getContent());
            }

            protected void handleFailure(ACLMessage failure) {
                if (failure.getSender().equals(myAgent.getAMS())) {
                    // Mensaje de la plataforma JADE: El destinatario no existe
                    System.out.println("El vendedor no existe");
                } else {
                    gui.aviso("Vendedor " + failure.getSender().getLocalName() +
                            " falló en realizar la venta");
                }
            }

            protected void handleAllResponses(Vector responses, Vector acceptances) {
                if(responses.size() > 0) { // Aceptar la mejor propuesta
                    mejorPrecio = dineroDisponible; // Comprueba que la propuesta entre en el presupuesto
                    @SuppressWarnings("unused")
                    AID bestProposer = null;
                    ACLMessage accept = null;
                    Enumeration e = responses.elements();
                    while (e.hasMoreElements()) {
                        ACLMessage response = (ACLMessage) e.nextElement();
                        if (response.getPerformative() == ACLMessage.PROPOSE) {
                            ACLMessage reply = response.createReply();
                            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                            acceptances.addElement(reply);
                            int precio = Integer.parseInt(response.getContent());
                            if(precio <= mejorPrecio) {
                                mejorPrecio = precio;
                                bestProposer = reply.getSender();
                                accept = reply;
                            }
                        }
                    }
                    // Aceptar propuesta del Vendedor mas economico
                    if(accept != null) {
                        accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    } else {
                        if (acceptances.isEmpty()){
                            gui.aviso("No se consiguió el paquete");
                        }
                        else {
                            gui.aviso("Dinero insuficiente para comprar el paquete");
                        }
                    }
                } else { // No hubo ninguna respuesta
                    gui.aviso("No se consiguieron vendedores");
                }
            }

            protected void handleInform(ACLMessage inform) {
                gui.aviso("Se realizó la compra del paquete al vendedor " + 
                        inform.getSender().getLocalName() +
                        " por " + mejorPrecio + " Bs.");
            }
        });
    }
}