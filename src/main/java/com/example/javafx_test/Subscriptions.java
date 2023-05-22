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
        ReadValueId readValueId = new ReadValueId(
                n.fim_Maq1_Sinal.getNodeId(),
                AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE
        );
        // IMPORTANT: client handle must be unique per item within the context of a subscription.
        // You are not required to use the UaSubscription's client handle sequence; it is provided as a convenience.
        // Your application is free to assign client handles by whatever means necessary.
        UInteger clientHandle = subscription.nextClientHandle(); //client handle is an identifier that is used to associate the subscription
        MonitoringParameters parameters = new MonitoringParameters(
                clientHandle,
                samplingInterval,     // sampling interval
                null,       // filter, null means use default
                uint(30),   // queue size
                true        // discard oldest
        );
        MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(
                readValueId,
                MonitoringMode.Reporting,
                parameters
        ); //monitoring settings for a specific variable within a subscription.

        // when creating items in MonitoringMode.Reporting this callback is where each item needs to have its
        // value/event consumer hooked up. The alternative is to create the item in sampling mode, hook up the
        // consumer after the creation call completes, and then change the mode for all items to reporting.
        UaSubscription.ItemCreationCallback onItemCreated =
                (item, id) -> item.setValueConsumer(this::onSubscriptionValue);

        List<UaMonitoredItem> items = subscription.createMonitoredItems(
                TimestampsToReturn.Both,
                newArrayList(request),
                onItemCreated
        ).get();

        for (UaMonitoredItem item : items) {
            if (item.getStatusCode().isGood()) {
                System.out.println("0000000000000000:item created for nodeId={"+ item.getReadValueId().getNodeId()+"}");

            } else {
                System.out.println("33333333:failed to create item for nodeId={"+ item.getReadValueId().getNodeId()+"}" + " (status={" +item.getStatusCode());

            }
        }
        // let the example run for 5 seconds then terminate
        //Thread.sleep(5000);

    }


    // Define the sampling interval (e.g., 1 second = 1000.0)
    //double samplingInterval = 15.0;
    //ManagedSubscription subscription = ManagedSubscription.create(client, samplingInterval); //inicio for creating Subscriptions and MonitoredItems.

    //specify a publishing interval when creating the subscription:
    //ManagedSubscription subscription = ManagedSubscription.create(client, 250.0);

    //Once the Subscription is created you can add a data or event listener to it. This listener will receive
    // all data changes for all items belonging to the subscription.

    //This method is called automatically by the subscription whenever a new value is received.
    private void onSubscriptionValue(UaMonitoredItem item, DataValue value) {

            System.out.println(
                    "999999999999999999999999999:subscription value received: item={" +
                            item.getReadValueId().getNodeId()
                            +"}, value={"
                            + value.getValue() +"};");


    }
}
