package services;

import daos.BankDAO;
import entities.Account;
import entities.Client;
import exceptions.InvalidIdException;
import exceptions.InvalidNameException;

//import java.util.HashMap;
import java.util.HashSet;
//import java.util.Set;

public class BankServiceImpl implements BankService{
    private final BankDAO bankDAO;

    public BankServiceImpl(BankDAO b) {
        this.bankDAO = b;
    }

    @Override
    public Client getClient(int id) {
        return bankDAO.getClient(id);
    }

    @Override
    public Client getClient(Client client) {
        return bankDAO.getClient(client.getId());
    }

    @Override
    public HashSet<Client> getAllClients() {
        return bankDAO.getAllClients();
    }

    @Override
    public Client updateClient(Client client) {
        if(client.getName().equals("")) {
            throw new InvalidNameException("Client Name cannot be empty");
        }
        return bankDAO.updateClient(client);
    }

    @Override
    public Account updateAccount(Account account) {
        if(account.getAmount() < 0) {
            throw new IllegalArgumentException("Account amount cannot be negative");
        }
        Client c = bankDAO.getClient(account.getClientId());
        if(c == null) return null;
        for(Account a : c.getAccounts()) {
            if(a.getId() == account.getId()) {
                return bankDAO.updateAccount(account);
            }
        }
        return null;
    }

    @Override
    public boolean deleteClientById(int id) {
        if(id < 0) throw new InvalidIdException("Client ID cannot be negative");
        return bankDAO.deleteClientById(id);
    }

    @Override
    public Client createClient(Client client) {
        if(client.getName().equals("")) {
            throw new InvalidNameException("Name cannot be empty");
        }
        client.setAccounts(null);
        return bankDAO.createClient(client);
    }

    @Override
    public Account createAccount(int clientId) {
        return this.createAccount(clientId, 0);
    }

    @Override
    public Account createAccount(int clientId, double amount) {
        if(amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        // check to see if the client exists
        Client c = bankDAO.getClient(clientId);

        // If not return null, otherwise return the Account
        if(c == null) return null;
        return bankDAO.createAccount(clientId, amount);
    }

    @Override
    public HashSet<Account> getAccountsInRange(int clientId, double floor, double ceiling) {
        Client c = bankDAO.getClient(clientId);
        if(c == null) return null;
        HashSet<Account> ret = new HashSet<>();
        if(floor < 0) floor = 0;
        if(ceiling < 0) ceiling = Double.MAX_VALUE;
        for(Account a: c.getAccounts()) {
            if(a.getAmount() >= floor && a.getAmount() <= ceiling) {
                ret.add(a);
            }
        }
        return ret;
    }

    @Override
    public Account getAccount(int clientId, int accountId) {
        Client c = bankDAO.getClient(clientId);
        if(c == null) return null;
        return c.getAccountById(accountId);
    }

    @Override
    public boolean deleteAccount(int clientId, int id) {
        return bankDAO.deleteAccount(clientId, id);
    }

}
