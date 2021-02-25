package services;

import entities.Account;
import entities.Client;
//import java.util.HashMap;
import java.util.HashSet;
//import java.util.Set;

// Contains logic to provide services
public interface BankService {
    Client getClient(int id);
    Client getClient(Client client);
    HashSet<Client> getAllClients();

    Client updateClient(Client client);
    Account updateAccount(Account account);

    boolean deleteClientById(int id);
    boolean deleteAccount(int client_id, int id);

    Client createClient(Client client);
    Account createAccount(int client_id);
    Account createAccount(int client_id, double amount);

    HashSet<Account> getAccountsInRange(int client_id, double floor, double ceiling);
    Account getAccount(int client_id, int accountId);
}
