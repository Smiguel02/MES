package jsoncomms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class ERPClient implements Runnable{
    private ArrayList<String> array;

    public void getArray(ArrayList array){
        this.array = array;
    }

    @Override
    public void run() {
        try {
            System.out.println("cliente inicia thread");
            // Create an ArrayList to send
            // Create an instance of the Order request´, aqui vai ser o request
            Order_json orderRequest = new Order_json("ABC123", "XYZ789", "10:00 AM");


            // Convert the order request to a JSON string
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            String jsonRequest = gson.toJson(orderRequest);
            // Calculate the checksum
            String checksum = calculateChecksum(jsonRequest);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("request", jsonRequest);
            jsonObject.addProperty("checksum", checksum);
            System.out.println(jsonObject.get("request"));

            // TCP connection details
            //"localhost"
            String ip = "127.0.0.1";  // IP address of the receiver
            int port = 12345;  // Port number of the receiver


            // Create a TCP socket connection
            Socket socket = new Socket(ip, port);
            System.out.println("New socket created!");



            // Create an ObjectOutputStream to write objects to the socket
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            // Send the ArrayList
            outputStream.writeObject(jsonObject.toString());
            outputStream.flush();
            // Send the checksum
            outputStream.writeObject(checksum);
            outputStream.flush();

            // Receive response from server
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            String jsonResponse = (String) inputStream.readObject();
            System.out.println("Received response from server: " + jsonResponse);

            // Close connections
            outputStream.close();
            inputStream.close();
            socket.close();
            System.out.println("FIM CLIENT");
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }
    private static String calculateChecksum(String arrayList) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        StringBuilder sb = new StringBuilder();

       /* for (Object element : arrayList) {
            sb.append(element.toString());
        }*/
        sb = new StringBuilder(arrayList);
        byte[] hash = md.digest(sb.toString().getBytes());
        StringBuilder checksum = new StringBuilder();

        for (byte b : hash) {
            checksum.append(String.format("%02x", b));
        }

        return checksum.toString();
    }

}
