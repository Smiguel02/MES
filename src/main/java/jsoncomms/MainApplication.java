package jsoncomms;


import model.order.pedidos;

import static java.lang.Thread.sleep;


public class MainApplication {
    public static void main(String[] args) throws InterruptedException {

        // Create instances of the classes
        pedidos r = new pedidos(1,0,0, 0);
        Client class1 = new Client(r);
        Server class2 = new Server();

        //class1.getArray(xxx);

        // Create Thread objects and pass the instances of the classes
        Thread thread2 = new Thread(class2);
        Thread thread1 = new Thread(class1);

        // Start the threads
        thread2.start();
        sleep(10);
        thread1.start();


        // Continue with other tasks in the main thread if needed
        System.out.println("Main thread continues to run");
    }
}
