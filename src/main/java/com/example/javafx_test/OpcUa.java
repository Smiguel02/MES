package com.example.javafx_test;


import org.apache.log4j.BasicConfigurator;
import org.eclipse.milo.opcua.sdk.client.AddressSpace;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadResponse;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.eclipse.milo.opcua.stack.core.types.structured.WriteResponse;
import org.eclipse.milo.opcua.stack.core.types.structured.WriteValue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class OpcUa extends Thread{


    private static OpcUaClient client;
    private static AddressSpace addressSpace;
    private static OpcUa instance = null;
    UaVariableNode Gvlprod1;
    UaVariableNode Gvlprod2;
    UaVariableNode GvlSaida;
    UaVariableNode GvlAt1Warehouse;
    UaVariableNode GvlAt1SensP;
    UaVariableNode at1_Livre;
    UaVariableNode maq1_Livre;
    UaVariableNode maq2_Livre;
    UaVariableNode fim_Maq1_Sinal;
    UaVariableNode fim_Maq2_Sinal;
    UaVariableNode pospec1_Sinal;
    UaVariableNode pospec2_Sinal;
    UaVariableNode Gvl_pedir_peca_Ware;
    private Boolean first_Order = true;

    List<ReadValueId> Ids_mandarFazerPeca = new ArrayList<>();

    public OpcUa() throws UaException{
    }




        // Static method to create instance of Singleton class
        public static OpcUa getInstance () throws Exception {
            if (instance == null) {
                instance = new OpcUa();
                instance.connectToServer();
            }
            return instance;
        }

        private void connectToServer () throws Exception {
            //Endpoint URL do PLC: opc.tcp://localhost:4840
            try {
                BasicConfigurator.configure(); //preciso por causa de erro log4j
                client = OpcUaClient.create("opc.tcp://localhost:4840",
                        endpoints ->
                                endpoints.stream()
                                        .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                                        .findFirst(),
                        OpcUaClientConfigBuilder::build
                );
                client.connect().get();
                System.out.println("*****connected**********");
                addressSpace = client.getAddressSpace();
                //opcuaclient instance will automatically attempt to reconnect any time the connection is lost, até disconnect is called
                InicializarNodes();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public OpcUaClient getMyClient () {
            return client;
        }


        public AddressSpace getMyAddressSpace () {
            return addressSpace;
        }

        private void InicializarNodes () throws UaException {
            Gvlprod1 = (UaVariableNode) addressSpace.getNode(
                    new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.prod1")
            );
            Gvlprod2 = (UaVariableNode) addressSpace.getNode(
                    new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.prod2")
            );
            GvlSaida = (UaVariableNode) addressSpace.getNode(
                    new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.saida")
            );
            GvlAt1Warehouse = (UaVariableNode) addressSpace.getNode(
                    new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.AT1RWarO")
            );
            GvlAt1SensP = (UaVariableNode) addressSpace.getNode(
                    new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.AT1ISensP")
            );
            at1_Livre = (UaVariableNode) addressSpace.getNode(
                    new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.AT1.livre")
            );

            fim_Maq1_Sinal = (UaVariableNode) addressSpace.getNode(
                    new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.posmaq1.sinal")
            );
            fim_Maq2_Sinal = (UaVariableNode) addressSpace.getNode(
                    new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.posmaq2.sinal")
            );
            pospec1_Sinal = (UaVariableNode) addressSpace.getNode(
                    new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.pospec1.sinal")
            );
            pospec2_Sinal = (UaVariableNode) addressSpace.getNode(
                    new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.pospec2.sinal")
            );
            Gvl_pedir_peca_Ware = (UaVariableNode) addressSpace.getNode(
                    new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.entrada")
            );

            Ids_mandarFazerPeca.add(
                    new ReadValueId(
                            at1_Livre.getNodeId(),
                            AttributeId.Value.uid(),
                            null, // indexRange
                            QualifiedName.NULL_VALUE
                    )
            );
            Ids_mandarFazerPeca.add(
                    new ReadValueId(
                            Gvlprod1.getNodeId(),
                            AttributeId.Value.uid(),
                            null, // indexRange
                            QualifiedName.NULL_VALUE
                    )
            );
            Ids_mandarFazerPeca.add(
                    new ReadValueId(
                            Gvlprod2.getNodeId(),
                            AttributeId.Value.uid(),
                            null, // indexRange
                            QualifiedName.NULL_VALUE
                    )
            );
        /*Ids_mandarFazerPeca.add(
                new ReadValueId(
                        Gvl_pedir_peca_Ware.getNodeId(),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );*/

        }

        public Object ReadOneVar (UaVariableNode nodeId) throws UaException {
            DataValue value = nodeId.readValue();
            DataValue descriptionValue = nodeId.readAttribute(AttributeId.Value); //vai retirar o valor da var
            System.out.println(descriptionValue.getValue().getValue()); //vai dar o valor da variavel
            assert descriptionValue.getSourceTime() != null;
            System.out.println(descriptionValue.getServerTime());
            return value.getValue().getValue(); //devolve o valor lido

        }

        public List<DataValue> ReadMultiVars (List < ReadValueId > lista_Ids_ler) throws
        ExecutionException, InterruptedException {
            ReadResponse readResponse = client.read(
                    0.0, // maxAge é o mais recente
                    TimestampsToReturn.Both,
                    lista_Ids_ler
            ).get();
            List<DataValue> dataValues = List.of(readResponse.getResults());
            System.out.println(dataValues);
            for (DataValue dataValue : dataValues) {
                Variant variant = dataValue.getValue();
                System.out.println("Value: " + variant.getValue());
            }
            return dataValues; //lê os valores da lista e retorna a lista datavalues por ordem de leitura
        }
        public int WriteMultiVars (List < WriteValue > writeValues) throws ExecutionException, InterruptedException
        { //lista de variáveis a escrever
            WriteResponse writeResponse = client.write(writeValues).get();

            // Check the status of each write operation
            List<StatusCode> statusCodes = List.of(writeResponse.getResults());

            for (int i = 0; i < statusCodes.size(); i++) {
                if (statusCodes.get(i).isGood()) {
                    System.out.println("Write operation succeeded for node " + writeValues.get(i).getNodeId() + " " + writeValues.get(i).getValue().getValue());
                } else {
                    System.out.println("Write operation failed for node " + writeValues.get(i).getNodeId() + ": " + statusCodes.get(i));
                }
            }

            return 0;
        }

        //FUNÇÃO ALTERADA PARA TESTAR PLC, VERSÃO "NORMAL" ABAIXO COMENTADO**************
        int qm = 1;

        public int mandarFazerPeca ( int pecaWarehouse, int pecaFabricar, int maquina) throws
        UaException, ExecutionException, InterruptedException {
        /*
            ver se at1 está livre, ver maqX livre, meter prodX cenas, warO peça que quero
            Ids_mandarFazerPeca
            At1, Maq1, Maq2 (true true true)
        */
            NodeId nodeid;
            System.out.println("qm" + qm);
            if (qm == 1) {
                nodeid = Gvlprod1.getNodeId();
            } else if (qm == 2) {
                nodeid = Gvlprod2.getNodeId();
            } else {
                return 1;
            }
            UShort value = UShort.valueOf(pecaWarehouse);
            System.out.println(nodeid);

            DataValue pec_war = new DataValue(new Variant(value));
            DataValue pec_fab = new DataValue(new Variant((short) pecaFabricar));
            UaVariableNode node_Maquina_livre, node_Maquina_Prod; //indicar que variavesi de máuqina ler e escrever
            if (first_Order == Boolean.TRUE) {
                //pode mandar fazer logo
                System.out.println("first order");
                List<WriteValue> writeValues = new ArrayList<>();
                writeValues.add(new WriteValue(nodeid, AttributeId.Value.uid(), null, pec_fab));
                writeValues.add(new WriteValue(Gvl_pedir_peca_Ware.getNodeId(), AttributeId.Value.uid(), null, pec_war));
                WriteMultiVars(writeValues);
                writeValues.clear();
                first_Order = Boolean.FALSE;
                return 0;
            } else {
                List<DataValue> dataValues = ReadMultiVars(Ids_mandarFazerPeca);
                //0 At1, 1 maq1, 2 maq2
                System.out.println("prod" + qm + ":" + dataValues.get(qm).getValue().getValue().equals((short) 0));

                if (dataValues.get(0).getValue().getValue().equals(true) && dataValues.get(qm).getValue().getValue().equals((short) 0)) {
                    System.out.println("Está livre posso meter");
                    List<WriteValue> writeValues = new ArrayList<>();
                    writeValues.add(new WriteValue(nodeid, AttributeId.Value.uid(), null, pec_fab));
                    writeValues.add(new WriteValue(Gvl_pedir_peca_Ware.getNodeId(), AttributeId.Value.uid(), null, pec_war));
                    WriteMultiVars(writeValues);
                    writeValues.clear();
                }

            }
            if (qm == 1) {
                qm = 2;
            } else if (qm == 2) {
                qm = 1;
            }
            //Object Atp1, maq;

            return 1;


        }






        ///*++++++++++++++++

    /*public int mandarFazerPeca (int pecaWarehouse, int pecaFabricar, int maquina) throws UaException, ExecutionException, InterruptedException {
        *
            ver se at1 está livre, ver maqX livre, meter prodX cenas, warO peça que quero
            Ids_mandarFazerPeca
            At1, Maq1, Maq2 (true true true)
        *
        NodeId nodeid;
        if(maquina == 1){
            nodeid = Gvlprod1.getNodeId();

        }else if(maquina == 2){
            nodeid = Gvlprod2.getNodeId();
        }else {
            return 1;
        }
        UShort value = UShort.valueOf(pecaWarehouse);
        System.out.println(value);

        DataValue pec_war = new DataValue(new Variant(value));
        DataValue pec_fab = new DataValue(new Variant((short)pecaFabricar));
        UaVariableNode node_Maquina_livre, node_Maquina_Prod; //indicar que variavesi de máuqina ler e escrever
        if(first_Order == Boolean.TRUE){
            //pode mandar fazer logo
            System.out.println("first order");
            List<WriteValue> writeValues = new ArrayList<>();
            writeValues.add(new WriteValue(nodeid, AttributeId.Value.uid(), null, pec_fab));
            writeValues.add(new WriteValue(Gvl_pedir_peca_Ware.getNodeId(), AttributeId.Value.uid(), null, pec_war));
            WriteMultiVars(writeValues);
            writeValues.clear();
            first_Order = Boolean.FALSE;
            return 0;
        }else{
            List<DataValue> dataValues = ReadMultiVars(Ids_mandarFazerPeca);
            //0 At1, 1 maq1, 2 maq2
            if(dataValues.get(0).getValue().getValue().equals(true) && dataValues.get(maquina).getValue().getValue().equals((short)0)){
                System.out.println("Está livre posso meter");
                List<WriteValue> writeValues = new ArrayList<>();
                writeValues.add(new WriteValue(nodeid, AttributeId.Value.uid(), null, pec_fab));
                writeValues.add(new WriteValue(Gvl_pedir_peca_Ware.getNodeId(), AttributeId.Value.uid(), null, pec_war));
                WriteMultiVars(writeValues);
                writeValues.clear();
            }

        }
        //Object Atp1, maq;

        return 1;


    }*/
    }

