import instance.ConnectableInstance;

public class Main {

    public static void main(String[] args) {
        try {
            new ConnectableInstance(args[0]);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

}
