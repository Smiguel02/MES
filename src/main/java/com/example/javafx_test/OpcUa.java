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
    UaVariableNode PLCTime;     // FIXME: new vairable, verify
    UaVariableNode time_maq1, time_maq2, time_maq3, time_maq4;
    UaVariableNode p1_counter,p2_counter,p3_counter,p4_counter,p5_counter,p6_counter,p7_counter,p8_counter,p9_counter;
    UaVariableNode PM1c_Sensor, PM2c_Sensor;
    UaVariableNode Mach1_signal,Mach2_signal,Mach3_signal,Mach4_signal;

    UaVariableNode Gvlprod1;
    UaVariableNode Gvlprod2;
    UaVariableNode GvlSaida;
    UaVariableNode GvlWarehouse;
    UaVariableNode GvlAt1SensP;
    UaVariableNode at1_Livre;
    UaVariableNode at2_Livre;   //FIXME: create this variable lol
    UaVariableNode st1_Livre;
    UaVariableNode pt1_Livre;
    UaVariableNode ct8_Livre;   //FIXME: create this variable lol
    UaVariableNode ct3_Livre;   //FIXME: create this variable lol

    UaVariableNode maq1_Livre;
    UaVariableNode maq2_Livre;
    UaVariableNode fim_Maq1_Sinal;
    UaVariableNode fim_Maq2_Sinal;
    UaVariableNode pospec1_Sinal;
    UaVariableNode pospec2_Sinal;
    UaVariableNode Gvl_pedir_peca_Ware;
    UaVariableNode Gvl_sai_peca_Ware;
    UaVariableNode Gvl_irmaq1, Gvl_irmaq2;

    UaVariableNode poscaminho1_ocupado, poscaminho2_ocupado;
    private Boolean first_Order = true;

    List<ReadValueId> Ids_mandarFazerPeca = new ArrayList<>();
    List<ReadValueId> Ids_mandarSairPeca = new ArrayList<>();
    List<ReadValueId> PieceCounter = new ArrayList<>();
    List<ReadValueId> Machs_signal = new ArrayList<>();


    private OpcUa() throws UaException {
    }

    // Static method to create instance of Singleton class
    public synchronized static OpcUa getInstance() throws Exception {
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
//            subs(); //Inicializa
        } catch (Exception e) {
            System.out.println("OH NO SOMETHING WRONG");
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
        PLCTime = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.ficheirotexto.tempo") //tempo do PLC
        );
        Mach1_signal = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.ST3ISens") //tempo do PLC
        );
        Mach2_signal = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.ST5ISens") //tempo do PLC
        );
        Mach3_signal = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.PT5ISens") //tempo do PLC
        );
        Mach4_signal = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.PT6ISens") //tempo do PLC
        );
        time_maq1 = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.ficheirotexto.tempomaq1") //tempo do PLC
        );
        time_maq2 = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.ficheirotexto.tempomaq2") //tempo do PLC
        );
        time_maq3 = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.ficheirotexto.tempomaq3") //tempo do PLC
        );
        time_maq4 = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.ficheirotexto.tempomaq4") //tempo do PLC
        );

        p1_counter = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.ficheirotexto.p1") //tempo do PLC
        );
        p2_counter = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.ficheirotexto.p2") //tempo do PLC
        );
        p3_counter = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.ficheirotexto.p3") //tempo do PLC
        );
        p4_counter = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.ficheirotexto.p4") //tempo do PLC
        );
        p5_counter = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.ficheirotexto.p5") //tempo do PLC
        );
        p6_counter = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.ficheirotexto.p6") //tempo do PLC
        );
        p7_counter = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.ficheirotexto.p7") //tempo do PLC
        );
        p8_counter = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.ficheirotexto.p8") //tempo do PLC
        );
        p9_counter = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.ficheirotexto.p9") //tempo do PLC
        );
        PM1c_Sensor = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.PM1cISens") //tempo do PLC
        );
        PM2c_Sensor = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.PM2cISens") //tempo do PLC
        );

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
        st1_Livre = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.ST1.livre")//ver AT1 se está livre
        );
        pt1_Livre = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.PT1.livre")//ver AT1 se está livre
        );
        at2_Livre = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.AT2.livre")//ver AT2 se está livre
        );

        ct8_Livre = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.CT8.livre")//ver AT2 se está livre
        );

        ct3_Livre = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.CT3.livre")//ver AT2 se está livre
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

        poscaminho1_ocupado = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.poscaminho1.ocupado")
        );
        poscaminho2_ocupado = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.poscaminho2.ocupado")
        );
        Gvl_irmaq1 = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.irmaq1")
        );
        Gvl_irmaq2 = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.irmaq2")
        );

        Machs_signal.add(
                new ReadValueId(
                        Mach1_signal.getNodeId(),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );
        Machs_signal.add(
                new ReadValueId(
                        Mach2_signal.getNodeId(),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );
        Machs_signal.add(
                new ReadValueId(
                        Mach3_signal.getNodeId(),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );
        Machs_signal.add(
                new ReadValueId(
                        Mach4_signal.getNodeId(),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );

        PieceCounter.add(
                new ReadValueId(
                        p1_counter.getNodeId(),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );
        PieceCounter.add(
                new ReadValueId(
                        p2_counter.getNodeId(),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );
        PieceCounter.add(
                new ReadValueId(
                        p3_counter.getNodeId(),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );
        PieceCounter.add(
                new ReadValueId(
                        p4_counter.getNodeId(),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );
        PieceCounter.add(
                new ReadValueId(
                        p5_counter.getNodeId(),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );
        PieceCounter.add(
                new ReadValueId(
                        p6_counter.getNodeId(),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );
        PieceCounter.add(
                new ReadValueId(
                        p7_counter.getNodeId(),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );
        PieceCounter.add(
                new ReadValueId(
                        p8_counter.getNodeId(),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );
        PieceCounter.add(
                new ReadValueId(
                        p9_counter.getNodeId(),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );

        Ids_mandarFazerPeca.add(
                new ReadValueId(
                        poscaminho1_ocupado.getNodeId(),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );

        Ids_mandarFazerPeca.add(
                new ReadValueId(
                        poscaminho2_ocupado.getNodeId(),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );

        Ids_mandarFazerPeca.add(
                new ReadValueId(
                        at1_Livre.getNodeId(),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );

        Ids_mandarSairPeca.add(
                new ReadValueId(
                        poscaminho1_ocupado.getNodeId(),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );

        Ids_mandarSairPeca.add(
                new ReadValueId(
                        poscaminho2_ocupado.getNodeId(),
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
//        System.out.println(descriptionValue.getValue().getValue()); //vai dar o valor da variavel
        assert descriptionValue.getSourceTime() != null;
//        System.out.println(descriptionValue.getServerTime());
        return value.getValue().getValue(); //devolve o valor lido

    }

    public List<DataValue> ReadMultiVars (List<ReadValueId> lista_Ids_ler) throws ExecutionException, InterruptedException {
        ReadResponse readResponse = client.read(
                0.0, // maxAge é o mais recente
                TimestampsToReturn.Both,
                lista_Ids_ler
        ).get();
        List<DataValue> dataValues = List.of(readResponse.getResults());
//        System.out.println(dataValues);
        for (DataValue dataValue : dataValues) {
            Variant variant = dataValue.getValue();
//            System.out.println("Value: " + variant.getValue() );
        }
        return dataValues; //lê os valores da lista e retorna a lista datavalues por ordem de leitura
    }
    public int WriteMultiVars(List<WriteValue> writeValues ) throws ExecutionException, InterruptedException { //lista de variáveis a escrever
        WriteResponse writeResponse = client.write(writeValues).get();

        // Check the status of each write operation
        List<StatusCode> statusCodes = List.of(writeResponse.getResults());

        for (int i = 0; i < statusCodes.size(); i++) {
            if (statusCodes.get(i).isGood()) {
//                System.out.println("Write operation succeeded for node " + writeValues.get(i).getNodeId() +" "+ writeValues.get(i).getValue().getValue());
            } else {
//                System.out.println("Write operation failed for node " + writeValues.get(i).getNodeId() + ": " + statusCodes.get(i));
            }
        }

        return 0;
    }


    public int mandarFazerPeca (int pecaWarehouse, int pecaFabricar, int MachineToUse) throws UaException, ExecutionException, InterruptedException {
        /*
            ver se at1 está livre, ver maqX livre, meter prodX cenas, warO peça que quero
            Ids_mandarFazerPeca
            At1, Maq1, Maq2 (true true true)
        */

        UShort value = UShort.valueOf(pecaWarehouse);

        DataValue pec_war = new DataValue(new Variant(value)); //valores a escrever
        DataValue pec_fab = new DataValue(new Variant((short)pecaFabricar));
        DataValue ir = new DataValue(new Variant(Boolean.TRUE));
        //0 At1, 1 maq1, 2 maq2
        //vai ler as variáveis
        List<DataValue> dataValues = ReadMultiVars(Ids_mandarFazerPeca);
        //dataValues.get(qm).getValue().getValue().equals((short)0)

        //se At1 está livre, 1
        //verifica a maq1 e depois a maq2
        if(dataValues.get(0).getValue().getValue().equals(false)  && dataValues.get(2).getValue().getValue().equals(true) && MachineToUse == 1){
            System.out.println("Está livre, a meter na maq1");
            List<WriteValue> writeValues = new ArrayList<>();
            writeValues.add(new WriteValue(Gvlprod1.getNodeId(), AttributeId.Value.uid(), null, pec_fab));
            writeValues.add(new WriteValue(Gvl_irmaq1.getNodeId(), AttributeId.Value.uid(), null, ir));
            writeValues.add(new WriteValue(Gvl_pedir_peca_Ware.getNodeId(), AttributeId.Value.uid(), null, pec_war));
            WriteMultiVars(writeValues);
            writeValues.clear();
            System.out.println("Sent values on OPCUA!");
            return 1;
        } else if (dataValues.get(0).getValue().getValue().equals(false) && dataValues.get(1).getValue().getValue().equals(false)  && dataValues.get(2).getValue().getValue().equals(true) && MachineToUse ==2) {
            System.out.println("Está livre, a meter na maq2");
            List<WriteValue> writeValues = new ArrayList<>();
            writeValues.add(new WriteValue(Gvlprod2.getNodeId(), AttributeId.Value.uid(), null, pec_fab));
            writeValues.add(new WriteValue(Gvl_irmaq2.getNodeId(), AttributeId.Value.uid(), null, ir));
            writeValues.add(new WriteValue(Gvl_pedir_peca_Ware.getNodeId(), AttributeId.Value.uid(), null, pec_war));
            WriteMultiVars(writeValues);
            writeValues.clear();
            return 2;
        }else{

            System.out.println("NAO FIZ NENHUMA PEÇA BROOOOO!");
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

        if(dataValues.get(0).getValue().getValue().equals(false) && dataValues.get(1).getValue().getValue().equals(false) && dataValues.get(3).getValue().getValue().equals(false) && dataValues.get(2).getValue().getValue().equals(verificacao)) {
            System.out.println("A mandar peça sair!");
            List<WriteValue> writeValues = new ArrayList<>();
            writeValues.add(new WriteValue(Gvl_sai_peca_Ware.getNodeId(), AttributeId.Value.uid(), null, ok_sair));
            writeValues.add(new WriteValue(Gvl_pedir_peca_Ware.getNodeId(), AttributeId.Value.uid(), null, pec_sair));
            WriteMultiVars(writeValues);
            writeValues.clear();
            return 0;
        }

        System.out.println("PIECE DIDN'T LEAVE");
        return -1;
    }


//    public void subs() throws Exception {
//        Subscriptions subs = new Subscriptions();
//
//    }



}