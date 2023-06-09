package jsoncomms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import model.order.order;
import model.order.rawPieces;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class Server implements Runnable{

    private static Server instance = null;

    private rawPieces pieces_read;

    private boolean client_values_updated = false;

    public synchronized static Server getInstance(){
        if(instance == null){
            instance = new Server();
        }
        return instance;
    }

    public rawPieces getPieces_read() {
        client_values_updated = false;
        return pieces_read;
    }

    public synchronized void set_client_values_read(){
        client_values_updated = !client_values_updated;
    }
    public synchronized boolean client_has_new_pieces(){
        return client_values_updated;
    }

    @Override
    public void run() {
        int port = 12345;
        try (ServerSocket serverSocket = new ServerSocket(port)){

            System.out.println("Receiver is listening on port " + port);
            while (true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);
                Thread thread = new Thread(() -> {
                    try {
//                        if (!client_has_new_pieces()) {
                            // Receive message from client
                            while(client_has_new_pieces()){
                            }

                            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                            String jsonRequest = (String) inputStream.readObject();

                            // Convert the JSON object to a JsonObject
                            JsonObject jsonObject = new Gson().fromJson(jsonRequest, JsonObject.class);

                            // Extract the request and checksum from the JSON object
                            String request = jsonObject.get("request").getAsString();
                            String receivedChecksum = jsonObject.get("checksum").getAsString();

                            // Convert the JSON object to a JsonObject
                            rawPieces order_received = new Gson().fromJson(request, rawPieces.class);
                            //System.out.println("Received message from client: " + receivedMessage +"||"+ receivedChecksum);
                            // Validate the checksum
                            //todo: processar o recebimento de novas peças
                            if (validateChecksum(request, receivedChecksum)) {
                                System.out.println("Received ArrayList:");
                                System.out.println(order_received.getpieceType());
                                System.out.println(order_received.getnumberOfPieces());
                                System.out.println(order_received.getdaysToArrive());
                                System.out.println(order_received.getprice());
                                pieces_read = order_received;
                                set_client_values_read();
                            /*for (Object element : request) {
                                System.out.println(element);
                            }*/
                            } else {
                                System.out.println("Checksum validation failed. Data may be corrupted or lost.");
                            }

                            // Process the message (do some operations)

                            // Prepare the response message, resposta a receber pelo cliente
                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
                            String jsonRespt = gson.toJson("orderRequest complete");

                            // Send response to client
                            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                            outputStream.writeObject(jsonRespt);
                            outputStream.flush();

                            // Close connections
                            inputStream.close();
                            outputStream.close();
                            clientSocket.close();
                            System.out.println("FIM SERVER");
//                        }else{
//                            sleep(7);
//                        }
                        } catch(IOException | ClassNotFoundException e){
                            e.printStackTrace();
                        } catch(NoSuchAlgorithmException e){
                            throw new RuntimeException(e);
                    }
                });
                thread.start();
            }

        }catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private static boolean validateChecksum(String arrayList, String checksum) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        StringBuilder sb = new StringBuilder();

        /*for (Object element : arrayList) {
            sb.append(element);
        }*/
        sb = new StringBuilder(arrayList);
        byte[] hash = md.digest(sb.toString().getBytes());
        StringBuilder calculatedChecksum = new StringBuilder();

        for (byte b : hash) {
            calculatedChecksum.append(String.format("%02x", b));
        }

        return calculatedChecksum.toString().equals(checksum);
    }
}
