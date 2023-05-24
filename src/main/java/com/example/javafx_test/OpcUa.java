package com.example.javafx_test;


import com.google.common.collect.ImmutableList;
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
import org.eclipse.milo.opcua.stack.core.types.structured.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;

public class OpcUa {

    private static OpcUaClient client;
    private static AddressSpace addressSpace;
    private static OpcUa instance = null;
    UaVariableNode Gvlprod1;
    UaVariableNode Gvlprod2;
    UaVariableNode GvlSaida;
    UaVariableNode GvlWarehouse;
    UaVariableNode GvlAt1SensP;
    UaVariableNode at1_Livre;
    UaVariableNode maq1_Livre;
    UaVariableNode maq2_Livre;
    UaVariableNode fim_Maq1_Sinal;
    UaVariableNode fim_Maq2_Sinal;
    UaVariableNode pospec1_Sinal;
    UaVariableNode pospec2_Sinal;
    UaVariableNode Gvl_pedir_peca_Ware;
    UaVariableNode Gvl_sai_peca_Ware;
    private Boolean first_Order = true;

    List<ReadValueId> Ids_mandarFazerPeca = new ArrayList<>();
    List<ReadValueId> Ids_mandarSairPeca = new ArrayList<>();

    private OpcUa() throws UaException {
    }

    // Static method to create instance of Singleton class
    public static OpcUa getInstance() throws Exception {
        if (instance == null) {
            instance = new OpcUa();
            instance.connectToServer();
        }
        return instance;
    }

    private void connectToServer() throws Exception {
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
            subs(); //Inicializa
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OpcUaClient getMyClient(){
        return client;
    }


    public AddressSpace getMyAddressSpace(){
        return addressSpace;
    }

    private void InicializarNodes () throws UaException {
        Gvlprod1 = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.prod1") //mandar fazer peca maq1
        );
        Gvlprod2 = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.prod2") //mandar fazer peca maq2
        );
        GvlWarehouse = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.AT1RWarO") //armazem
        );
        GvlAt1SensP = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.AT1ISensP")
        );
        at1_Livre = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.AT1.livre")//ver AT1 se está livre
        );
        //*******************
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
        //******************
        Gvl_pedir_peca_Ware = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.entrada")
        );
        Gvl_sai_peca_Ware = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.saida")
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


        Ids_mandarSairPeca.add(
                new ReadValueId(
                        at1_Livre.getNodeId(),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );
        Ids_mandarSairPeca.add(
                new ReadValueId(
                        Gvl_pedir_peca_Ware.getNodeId(),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );
        Ids_mandarSairPeca.add(
                new ReadValueId(
                        Gvl_sai_peca_Ware.getNodeId(),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );

    }

    public Object ReadOneVar (UaVariableNode nodeId) throws UaException {
        DataValue value = nodeId.readValue();
        DataValue descriptionValue = nodeId.readAttribute(AttributeId.Value); //vai retirar o valor da var
        System.out.println(descriptionValue.getValue().getValue()); //vai dar o valor da variavel
        assert descriptionValue.getSourceTime() != null;
        System.out.println(descriptionValue.getServerTime());
        return value.getValue().getValue(); //devolve o valor lido

    }

    public List<DataValue> ReadMultiVars (List<ReadValueId> lista_Ids_ler) throws ExecutionException, InterruptedException {
        ReadResponse readResponse = client.read(
                0.0, // maxAge é o mais recente
                TimestampsToReturn.Both,
                lista_Ids_ler
        ).get();
        List<DataValue> dataValues = List.of(readResponse.getResults());
        System.out.println(dataValues);
        for (DataValue dataValue : dataValues) {
            Variant variant = dataValue.getValue();
            System.out.println("Value: " + variant.getValue() );
        }
        return dataValues; //lê os valores da lista e retorna a lista datavalues por ordem de leitura
    }
    public int WriteMultiVars(List<WriteValue> writeValues ) throws ExecutionException, InterruptedException { //lista de variáveis a escrever
        WriteResponse writeResponse = client.write(writeValues).get();

        // Check the status of each write operation
        List<StatusCode> statusCodes = List.of(writeResponse.getResults());

        for (int i = 0; i < statusCodes.size(); i++) {
            if (statusCodes.get(i).isGood()) {
                System.out.println("Write operation succeeded for node " + writeValues.get(i).getNodeId() +" "+ writeValues.get(i).getValue().getValue());
            } else {
                System.out.println("Write operation failed for node " + writeValues.get(i).getNodeId() + ": " + statusCodes.get(i));
            }
        }

        return 0;
    }


    //Manda fazer a peça. Fornecer peça inicial, peça a fabricar.
    //Vai fazer na maq1, return1; senão vê maq2, return 2; senão return -1 pq nao mandou fazer
    public int mandarFazerPeca (int pecaWarehouse, int pecaFabricar) throws UaException, ExecutionException, InterruptedException {
        /*
            ver se at1 está livre, ver maqX livre, meter prodX cenas, warO peça que quero
            Ids_mandarFazerPeca
            At1, Maq1, Maq2 (true true true)
        */

        UShort value = UShort.valueOf(pecaWarehouse);

        DataValue pec_war = new DataValue(new Variant(value)); //valores a escrever
        DataValue pec_fab = new DataValue(new Variant((short)pecaFabricar));
        UaVariableNode node_Maquina_livre, node_Maquina_Prod; //indicar que variavesi de máuqina ler e escrever
        List<DataValue> dataValues = ReadMultiVars(Ids_mandarFazerPeca);
        //0 At1, 1 maq1, 2 maq2
        //vai ler as variáveis

        //dataValues.get(qm).getValue().getValue().equals((short)0)

        //se At1 está livre, 1
        //verifica a maq1 e depois a maq2
        if(dataValues.get(0).getValue().getValue().equals(true) && dataValues.get(1).getValue().getValue().equals((short)0)){
            System.out.println("Está livre posso meter na maq1");
            List<WriteValue> writeValues = new ArrayList<>();
            writeValues.add(new WriteValue(Gvlprod1.getNodeId(), AttributeId.Value.uid(), null, pec_fab));
            writeValues.add(new WriteValue(Gvl_pedir_peca_Ware.getNodeId(), AttributeId.Value.uid(), null, pec_war));
            WriteMultiVars(writeValues);
            writeValues.clear();
            return 1;
        } else if (dataValues.get(0).getValue().getValue().equals(true) && dataValues.get(2).getValue().getValue().equals((short)0)) {
            System.out.println("Está livre posso meter na maq2");
            List<WriteValue> writeValues = new ArrayList<>();
            writeValues.add(new WriteValue(Gvlprod2.getNodeId(), AttributeId.Value.uid(), null, pec_fab));
            writeValues.add(new WriteValue(Gvl_pedir_peca_Ware.getNodeId(), AttributeId.Value.uid(), null, pec_war));
            WriteMultiVars(writeValues);
            writeValues.clear();
            return 2;
        }else{
            return -1;
            //não pode mandar fazer a peça
        }


    }
    public int mandarSairPeca (int pecaWarehouseSair) throws UaException, ExecutionException, InterruptedException {
        /*
            Mandar sair uma peça do armazém, Preciso de ver o At1 se está livre
            gvl.saida meter a 1 e depois mudar com WarO através do gvl.entrada
        */

        UShort value = UShort.valueOf(pecaWarehouseSair); //USHORT PARA UINT ---- SHORT PARA INT
        DataValue pec_sair = new DataValue(new Variant(value)); //entrada
        UShort verificacao = UShort.valueOf(0);
        DataValue ok_sair = new DataValue(new Variant(Boolean.TRUE));


        List<DataValue> dataValues = ReadMultiVars(Ids_mandarSairPeca);
        //0 At1, , 1 entrada, 2 gvl.saida

        if(dataValues.get(0).getValue().getValue().equals(true) && dataValues.get(2).getValue().getValue().equals(false) && dataValues.get(1).getValue().getValue().equals(verificacao)){
            System.out.println("Está livre posso mandar sair");
            List<WriteValue> writeValues = new ArrayList<>();
            writeValues.add(new WriteValue(Gvl_sai_peca_Ware.getNodeId(), AttributeId.Value.uid(), null, ok_sair));
            writeValues.add(new WriteValue(Gvl_pedir_peca_Ware.getNodeId(), AttributeId.Value.uid(), null, pec_sair));
            WriteMultiVars(writeValues);
            writeValues.clear();
            return 0;
        }


        //erro
        return -1;

    }
    public void subs() throws Exception {
        Subscriptions subs = new Subscriptions();

    }



}