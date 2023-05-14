package com.example.javafx_test.opc_ua;

public class ReadMultVarPlusName {
    /*
    //Read multiple
        // Assuming you have a UaClient object named 'client' and a NodeId named 'nodeId'
        UaVariableNode testNode = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.global_BOOL_var")
        );
        List<ReadValueId> readValueIds = new ArrayList<>();
        readValueIds.add(
                new ReadValueId(
                        new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.PLC_PRG.BOOL_var"),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );
        readValueIds.add(
                new ReadValueId(
                        new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.PLC_PRG.int_var"),
                        AttributeId.Value.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );
        ReadResponse readResponse = client.read(
                0.0, // maxAge é o mais recente
                TimestampsToReturn.Both,
                readValueIds
        ).get();
        List<DataValue> dataValues = List.of(readResponse.getResults());
        System.out.println(dataValues);
        for (DataValue dataValue : dataValues) {
            Variant variant = dataValue.getValue();
            System.out.println("Value: " + variant.getValue() );
        }
        //não consigo o nome aqui

        //sacar o nome da varaivel a ler
        List<ReadValueId> readValueIdsName = new ArrayList<>();
        // Assume you have a node ID already defined as nodeId
        readValueIdsName.add(
                new ReadValueId(
                        new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.PLC_PRG.BOOL_var"),
                        AttributeId.DisplayName.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );
        readValueIdsName.add(
                new ReadValueId(
                        new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.PLC_PRG.int_var"),
                        AttributeId.DisplayName.uid(),
                        null, // indexRange
                        QualifiedName.NULL_VALUE
                )
        );
        ReadResponse readResponseNames = client.read(
                0.0, // maxAge é o mais recente
                TimestampsToReturn.Both,
                readValueIdsName
        ).get();
        List<DataValue> dataValuesNames = List.of(readResponseNames.getResults());
        System.out.println("ooo ->"+dataValuesNames);
        for (DataValue dataValueName : dataValuesNames) {
            String displayName = dataValueName.getValue().getValue().toString();
            System.out.println("Value name: " + displayName);
            displayName = displayName.substring(displayName.indexOf("=") + 1);
            displayName = displayName.substring(0, displayName.indexOf(","));
            System.out.println(displayName);

        }

     */
}
