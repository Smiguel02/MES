package com.example.javafx_test.opc_ua;

import org.eclipse.milo.opcua.sdk.client.AddressSpace;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;

import java.util.List;

public class BrowseNodes {/*
    //Browse al nodef my address space
    AddressSpace addressSpace = client.getAddressSpace();

    UaNode serverNode = addressSpace.getNode(Identifiers.Server);

    List<? extends UaNode> nodes = addressSpace.browseNodes(serverNode);
        System.out.println("Lista, size: " + nodes.size());
        for (UaNode node : nodes) { //igual a i=0 i<size
        // System.out.println(node);nao retorna nada util
        NodeId nodeId = node.getNodeId();
        String displayName = node.getDisplayName().getText();
        NodeClass nodeClass = node.getNodeClass();
        System.out.println("Browse name:" + node.getBrowseName().getName());
        System.out.println("Name:" + displayName);
        System.out.println("Node ID:" + nodeId);
        System.out.println("Node Class:" + nodeClass);

    }

    private BrowseNodes() throws UaException {
    }*/
}
