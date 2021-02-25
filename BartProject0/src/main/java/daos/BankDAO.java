package daos;

import entities.Account;
import entities.Client;

//import java.util.HashMap;
import java.util.HashSet;

public interface BankDAO {
    // CREATE
    Client createClient(Client c);
    Account createAccount(int clientId, double amount);

    // READ
    Client getClient(int clientId);
    HashSet<Client> getAllClients();

    // UPDATE
    Client updateClient(Client c);
    Account updateAccount(Account account);

    // DELETE
    boolean deleteClientById(int id);
    boolean deleteAccount(int clientId, int id);
}
