package com.example.javafx_test;


import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;


public class Subscriptions {

    OpcUa n = OpcUa.getInstance();
    OpcUaClient client = n.getMyClient();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Subscriptions() throws Exception {
        // create a subscription @ 15ms
        double samplingInterval = 13.0;
        UaSubscription subscription = client.getSubscriptionManager().createSubscription(15.0).get();

        // subscribe to the Value attribute of the server's CurrentTime node
        ReadValueId fim_Maq1_sinal = new ReadValueId(
                n.fim_Maq1_Sinal.getNodeId(),
                AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE
        );
        UInteger fim_Maq1_sinal_Handle = subscription.getSubscriptionId();

        ReadValueId fim_Maq2_sinal = new ReadValueId(
                n.fim_Maq2_Sinal.getNodeId(),
                AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE
        );
        UInteger fim_Maq2_sinal_Handle = subscription.getSubscriptionId();

        ReadValueId pospec1_Sinal = new ReadValueId(
                n.pospec1_Sinal.getNodeId(),
                AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE
        );
        UInteger pospec1_Sinal_Handle = subscription.getSubscriptionId();

        ReadValueId pospec2_Sinal = new ReadValueId(
                n.pospec2_Sinal.getNodeId(),
                AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE
        );
        UInteger pospec2_Sinal_Handle = subscription.getSubscriptionId();


        MonitoringParameters fim_Maq1_sinal_parameters = new MonitoringParameters(
                fim_Maq1_sinal_Handle,
                samplingInterval,     // sampling interval
                null,       // filter, null means use default
                uint(10),   // queue size
                true        // discard oldest
        );
        MonitoringParameters fim_Maq2_sinal_parameters = new MonitoringParameters(
                fim_Maq2_sinal_Handle,
                samplingInterval,     // sampling interval
                null,       // filter, null means use default
                uint(10),   // queue size
                true        // discard oldest
        );
        MonitoringParameters pospec1_Sinal_parameters = new MonitoringParameters(
                pospec1_Sinal_Handle,
                samplingInterval,     // sampling interval
                null,       // filter, null means use default
                uint(10),   // queue size
                true        // discard oldest
        );
        MonitoringParameters pospec2_Sinal_parameters = new MonitoringParameters(
                pospec2_Sinal_Handle,
                samplingInterval,     // sampling interval
                null,       // filter, null means use default
                uint(10),   // queue size
                true        // discard oldest
        );

        List<MonitoredItemCreateRequest> requests = new ArrayList<>();
        MonitoredItemCreateRequest fim_Maq1_sinal_request = new MonitoredItemCreateRequest(
                fim_Maq1_sinal,
                MonitoringMode.Reporting,
                fim_Maq1_sinal_parameters
        );
        requests.add(fim_Maq1_sinal_request);

        MonitoredItemCreateRequest fim_Maq2_sinal_request = new MonitoredItemCreateRequest(
                fim_Maq2_sinal,
                MonitoringMode.Reporting,
                fim_Maq2_sinal_parameters
        );
        requests.add(fim_Maq2_sinal_request);

        MonitoredItemCreateRequest pospec1_Sinal_request = new MonitoredItemCreateRequest(
                pospec1_Sinal,
                MonitoringMode.Reporting,
                pospec1_Sinal_parameters
        );
        requests.add(pospec1_Sinal_request);

        MonitoredItemCreateRequest pospec2_Sinal_request = new MonitoredItemCreateRequest(
                pospec2_Sinal,
                MonitoringMode.Reporting,
                pospec2_Sinal_parameters
        );
        requests.add(pospec2_Sinal_request);

        UaSubscription.ItemCreationCallback onItemCreated = (item, id) -> {
            if (item.getReadValueId().getNodeId().equals(n.fim_Maq1_Sinal.getNodeId())) {
                System.out.println("1");
                item.setValueConsumer(this::Fim_Maq1_sinal_onSubscriptionValue);
            } else if (item.getReadValueId().getNodeId().equals(n.fim_Maq2_Sinal.getNodeId())) {
                System.out.println("2");
                item.setValueConsumer(this::Fim_Maq2_sinal_onSubscriptionValue);
            } else if (item.getReadValueId().getNodeId().equals(n.pospec1_Sinal.getNodeId())) {
                System.out.println("3");
                item.setValueConsumer(this::pospec1_Sinal_onSubscriptionValue);
            } else if (item.getReadValueId().getNodeId().equals(n.pospec2_Sinal.getNodeId())) {
                System.out.println("4");
                item.setValueConsumer(this::pospec2_Sinal_onSubscriptionValue);
            }

            // Add more conditions for additional variables
        };

        List<UaMonitoredItem> items = subscription.createMonitoredItems(
                TimestampsToReturn.Both,
                requests,
                onItemCreated
        ).get();

        for (UaMonitoredItem item : items) {
            if (item.getStatusCode().isGood()) {
                System.out.println("0000000000000000:item created for nodeId={"+ item.getReadValueId().getNodeId()+"}");

            } else {
                System.out.println("33333333:failed to create item for nodeId={"+ item.getReadValueId().getNodeId()+"}" + " (status={" +item.getStatusCode());

            }
        }


    }


    // Define the sampling interval (e.g., 1 second = 1000.0)
    //double samplingInterval = 15.0;
    //ManagedSubscription subscription = ManagedSubscription.create(client, samplingInterval); //inicio for creating Subscriptions and MonitoredItems.

    //specify a publishing interval when creating the subscription:
    //ManagedSubscription subscription = ManagedSubscription.create(client, 250.0);

    //Once the Subscription is created you can add a data or event listener to it. This listener will receive
    // all data changes for all items belonging to the subscription.

    //This method is called automatically by the subscription whenever a new value is received.
    private void Fim_Maq1_sinal_onSubscriptionValue(UaMonitoredItem item, DataValue value) {

        System.out.println(
                "1999999999999999999999999999:subscription value received: item={" +
                        item.getReadValueId().getNodeId()
                        +"}, value={"
                        + value.getValue() +"};");


    }
    private void Fim_Maq2_sinal_onSubscriptionValue(UaMonitoredItem item, DataValue value) {

        System.out.println(
                "2999999999999999999999999999:subscription value received: item={" +
                        item.getReadValueId().getNodeId()
                        +"}, value={"
                        + value.getValue() +"};");


    }
    private void pospec1_Sinal_onSubscriptionValue(UaMonitoredItem item, DataValue value) {

        System.out.println(
                "3999999999999999999999999999:subscription value received: item={" +
                        item.getReadValueId().getNodeId()
                        +"}, value={"
                        + value.getValue() +"};");


    }
    private void pospec2_Sinal_onSubscriptionValue(UaMonitoredItem item, DataValue value) {

        System.out.println(
                "4999999999999999999999999999:subscription value received: item={" +
                        item.getReadValueId().getNodeId()
                        +"}, value={"
                        + value.getValue() +"};");


    }
}
