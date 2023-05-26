package jsoncomms;


public class MainApplication {
    public static void main(String[] args) {

        // Create instances of the classes
        ERPClient class1 = new ERPClient();
        MessServer class2 = new MessServer();

        //class1.getArray(xxx);

        // Create Thread objects and pass the instances of the classes
        Thread thread1 = new Thread(class1);
        Thread thread2 = new Thread(class2);

        // Start the threads
        thread1.start();
        thread2.start();

        // Continue with other tasks in the main thread if needed
        System.out.println("Main thread continues to run");
    }
}
