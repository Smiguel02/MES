package com.example.javafx_test;

public class Write1Var {
    /*
    *
    * //Write
        //usar OpcUaClient para poder confirmar se foi feito com sucesso
        // Define the node id for the variable
        NodeId nodeId = new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.global_INT_var");

// Create a data value with the new integer value you want to write
        DataValue dataValue = new DataValue(new Variant((short)42));

// Create a write value with the node id, attribute id, and data value
        WriteValue writeValue = new WriteValue(nodeId, AttributeId.Value.uid(), null, dataValue);

// Add the write value to a list of write values
        List<WriteValue> writeValues = new ArrayList<>();
        writeValues.add(writeValue);

// Call the write method on the client with the list of write values
        WriteResponse writeResponse = client.write(writeValues).get();

// Check the response for any errors
        if (writeResponse.getResults()[0].isGood()) {
            System.out.println("Write successful");
        } else {
            System.out.println("Write failed with status code " + writeResponse.getResults()[0].isGood());
        }


        //The last line of the code sends the write request to the server and waits for the response.
        /*
        client.write(writeValues) sends the write request to the server, and get() blocks until the response is received.
        The response is then stored in the writeResponse variable, which contains information such as the status code and diagnostic
        information for the write operation.
         By calling get() on the write() method, the program waits for the server to respond before proceeding to the next line of code
         *


         List<WriteValue> writeValues = new ArrayList<>();

            writeValues.add(
                    new WriteValue(
                            new NodeId(4, "|var|CODESYS Control Win V3 x64.Application.GVL.global_INT_var"),
                            AttributeId.Value.uid(),
                            null, // indexRange
                            new DataValue(
                                    new Variant(42), // set the integer value you want to write
                                    StatusCode.GOOD,
                                    DateTime.now()
                            )
                    )
            );

            WriteResponse writeResponse = client.write(writeValues).get();



            CompletableFuture<List<StatusCode>> future = client.writeValues(ImmutableList.of(writeValue));
            List<StatusCode> results = future.get();

            CompletableFuture<List<StatusCode>> future = client.writeValues(ImmutableList.of(writeValue));
            future.thenAccept(result -> {
                // handle the write result
                System.out.println(result);
            });

     */




}
