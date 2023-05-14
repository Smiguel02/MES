package com.example.javafx_test.opc_ua;

public class ReadOneVar { /*
        //READING
        // Assuming you have a UaClient object named 'client' and a NodeId named 'nodeId'
        String nodeIdString = "|var|CODESYS Control Win V3 x64.Application.GVL.global_BOOL_var";
        NodeId nodeId = new NodeId(4, nodeIdString);
        UaVariableNode testNode = (UaVariableNode) addressSpace.getNode(
                new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.global_BOOL_var")
        );
        DataValue value = testNode.readValue(); -----> sincrono
        DataValue descriptionValue = testNode.readAttribute(AttributeId.Value);
        System.out.println(descriptionValue.getValue().getValue()); //vai dar o valor da variavel
        assert descriptionValue.getSourceTime() != null;
        System.out.println(descriptionValue.getServerTime());

                DataValue descriptionValue2 = testNode.readValue();
                System.out.println(descriptionValue2.getServerTime().getJavaDate());
                Thu May 04 16:12:59 WEST 2023 mas é a data que retirei

*/
}
