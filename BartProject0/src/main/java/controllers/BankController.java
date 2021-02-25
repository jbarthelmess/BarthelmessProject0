package controllers;

import com.google.gson.Gson;
import entities.Client;
import entities.Account;
import exceptions.InvalidIdException;
import exceptions.InvalidNameException;
//import org.apache.log4j.Logger;
import services.BankService;
import io.javalin.http.Handler;

//import java.util.HashMap;
//import java.util.InputMismatchException;
import java.util.HashSet;
//import java.util.Set;

public class BankController {
    private BankService bankService;
    private final Gson gson = new Gson();
    //static Logger logger = Logger.getLogger(BankController.class.getName());

    public BankController(BankService b) {
        this.bankService = b;
    }

    public Handler createClient = (ctx) -> {
        try {
            Client client = gson.fromJson(ctx.body(), Client.class);
            if(client == null) {
                ctx.status(400);
                ctx.result("No client info given");
            }
            else {
                client = bankService.createClient(client);
                ctx.status(201);
                ctx.result(gson.toJson(client));
            }
        }
        catch (InvalidNameException i) {
            ctx.status(400);
            ctx.result("Name cannot be empty");
        }
    };

    public Handler getAllClients = (ctx) -> {
        HashSet<Client> clients = bankService.getAllClients();
        ctx.status(200);
        ctx.result(gson.toJson(clients));
    };

    public Handler deleteClient = (ctx) -> {
        try {
            if(bankService.deleteClientById(Integer.parseInt(ctx.pathParam("id")))) {
                ctx.status(200);
                ctx.result("Successfully deleted client");
            }
            else {
                ctx.status(404);
                ctx.result("No such client");
            }
        }
        catch(InvalidIdException e) {
            ctx.status(400);
            ctx.result("Invalid ID provided");
        }
        catch(NumberFormatException e) {
            ctx.status(400);
            ctx.result("Non-integer ID is invalid");
        }
    };

    public Handler getClientById = (ctx) -> {
        try {
            Client c = bankService.getClient(Integer.parseInt(ctx.pathParam("id")));
            if(c == null) {
                ctx.status(404);
                ctx.result("No such client");
            }
            else {
                ctx.status(200);
                ctx.result(gson.toJson(c));
            }
        }
        catch(NumberFormatException e) {
            ctx.status(400);
            ctx.result("Non-integer ID is invalid");
        }
    };

    public Handler updateClient = (ctx) -> {
        try {
            Client client = gson.fromJson(ctx.body(), Client.class);
            if(client == null) {
                ctx.status(400);
                ctx.result("No new client info given");
            }
            else {
                client.setId(Integer.parseInt(ctx.pathParam("id")));
                client = this.bankService.updateClient(client);
                if (client == null) {
                    ctx.status(404);
                    ctx.result("No such client");
                } else {
                    ctx.status(200);
                    ctx.result(gson.toJson(client));
                }
            }
        }
        catch(InvalidIdException e) {
            ctx.status(400);
            ctx.result("Client ID's cannot be negative");
        }
        catch(NumberFormatException n) {
            ctx.status(400);
            ctx.result("Non-integer ID is invalid");
        }
        catch(InvalidNameException i) {
            ctx.status(400);
            ctx.result("Client name cannot be null or empty string");
        }
    };

    public Handler createAccount = (ctx) -> {
        try {
            Account a = gson.fromJson(ctx.body(), Account.class);
            if(a == null) a = new Account();
            a = bankService.createAccount(Integer.parseInt(ctx.pathParam("id")), a.getAmount());
            if(a == null) {
                ctx.status(404);
                ctx.result("No such client");
            }
            else {
                ctx.status(201);
                ctx.result(gson.toJson(a));
            }
        }
        catch(InvalidIdException e) {
            ctx.status(400);
            ctx.result("Client ID cannot be negative");
        }
        catch(NumberFormatException e) {
            ctx.status(400);
            ctx.result("Non numeric input is not valid");
        }
        catch(IllegalArgumentException e) {
            ctx.status(400);
            ctx.result("Amount cannot be negative");
        }
    };

    public Handler getAccounts = (ctx) -> {
        try {
            String floor = ctx.queryParam("amountGreaterThan", "0");
            String ceiling = ctx.queryParam("amountLessThan", "-1");
            double ceilingNum = Double.parseDouble(ceiling);
            double floorNum = Double.parseDouble(floor);
            HashSet<Account> accounts = bankService.getAccountsInRange(Integer.parseInt(ctx.pathParam("id")), floorNum, ceilingNum);
            if(accounts == null) {
                ctx.status(404);
                ctx.result("No such client");
            }
            else {
                ctx.status(200);
                ctx.result(gson.toJson(accounts));
            }
        }
        catch(NumberFormatException e) {
            ctx.status(400);
            ctx.result("Non number provided is invalid");
        }
    };

    public Handler getAccountById = (ctx) -> {
        try {
            Account a = bankService.getAccount(Integer.parseInt(ctx.pathParam("id")), Integer.parseInt(ctx.pathParam("acc_id")));
            if(a == null) {
                ctx.status(404);
                ctx.result("Client or Account not found");
            }
            else {
                ctx.status(200);
                ctx.result(gson.toJson(a));
            }
        }
        catch(InvalidIdException e) {
            ctx.status(400);
            ctx.result("Client and Account IDs cannot be negative");
        }
        catch(NumberFormatException e) {
            ctx.status(400);
            ctx.result("Non-integer ID provided is invalid");
        }

    };

    public Handler updateAccount = (ctx) -> {
        try {
            Account a = gson.fromJson(ctx.body(), Account.class);
            if(a == null) {
                ctx.status(400);
                ctx.result("No new account info given");
            }
            else {
                a.setId(Integer.parseInt(ctx.pathParam("acc_id")));
                a.setClientId(Integer.parseInt(ctx.pathParam("id")));
                a = bankService.updateAccount(a);
                if (a == null) {
                    ctx.status(404);
                    ctx.result("Client and Account ID's don't match or don't exist");
                } else {
                    ctx.status(200);
                    ctx.result(gson.toJson(a));
                }
            }
        }
        catch(InvalidIdException i) {
            ctx.status(400);
            ctx.result("Client and Account ID's cannot be negative");
        }
        catch(NumberFormatException n) {
            ctx.status(400);
            ctx.result("Non-integer ID values not valid");
        }
        catch(IllegalArgumentException e) {
            ctx.status(400);
            ctx.result("Amount cannot be negative");
        }
    };

    public Handler deleteAccount = (ctx) -> {
        try {
            if (bankService.deleteAccount(Integer.parseInt(ctx.pathParam("id")), Integer.parseInt(ctx.pathParam(":acc_id")))) {
                ctx.status(200);
                ctx.result("Successfully deleted account");
            } else {
                ctx.status(400);
                ctx.result("Could not delete");
            }
        }
        catch(InvalidIdException e) {
            ctx.status(400);
            ctx.result("Client and Account IDs cannot be negative");
        }
        catch(NumberFormatException e) {
            ctx.status(400);
            ctx.result("Non-integer ID provided is invalid");
        }
    };
}
