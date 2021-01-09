import com.db4o.ObjectServer;
import com.db4o.cs.Db4oClientServer;

public class Servidor {
    public static void main(String[] args) {
        ObjectServer server =
                Db4oClientServer.openServer(
                        "db40.server", 7701
                );
        //Acesso de usuarios
        server.grantAccess("root", "root");

        try {
            while (true){
                System.out.println("Server on");
                Thread.sleep(6000);
            }
        }catch (Exception e){
            System.err.println(e.getMessage());
        }finally {
            server.close();
        }
    }
}
