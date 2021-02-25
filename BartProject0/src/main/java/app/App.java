package app;

import controllers.BankController;
import daos.BankDAO;
import daos.BankDaoImpl;
import io.javalin.Javalin;
import services.BankService;
import services.BankServiceImpl;

//import java.util.Map;

public class App {
    public static void main(String [] args){

        Javalin app = Javalin.create();

        BankDAO bankDAO = new BankDaoImpl();

        BankService bankService = new BankServiceImpl(bankDAO);

        BankController bankController = new BankController(bankService);

        app.post("/clients", bankController.createClient);
        app.get("/clients", bankController.getAllClients);
        app.get("/clients/:id", bankController.getClientById);
        app.put("/clients/:id", bankController.updateClient);
        app.delete("/clients/:id", bankController.deleteClient);
        app.post("/clients/:id/accounts", bankController.createAccount);
        app.get("/clients/:id/accounts", bankController.getAccounts); // both all, and in a range
        app.get("/clients/:id/accounts/:acc_id", bankController.getAccountById);
        app.put("/clients/:id/accounts/:acc_id", bankController.updateAccount);
        app.delete("/clients/:id/accounts/:acc_id", bankController.deleteAccount);

        app.start();
    }
}
