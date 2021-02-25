package daoTests;

import daos.BankDaoImpl;
import entities.Account;
import entities.Client;
import daos.BankDAO;

import org.junit.jupiter.api.*;
//import utils.ConnectionUtil;

//import java.sql.Connection;
//import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BankDaoTest {
    private static Client c = null;
    private static final BankDAO dao = new BankDaoImpl();

    @Test
    @Order(1)
    void create_client() {
        // create a new client
        c = new Client();
        c.setName("Test Client");
        c.setId(0);
        c = dao.createClient(c);

        // assert that it was given a new id -- will be important for later tests
        Assertions.assertNotEquals(0, c.getId());
    }

    @Test
    @Order(2)
    void get_client() {
        // try to get the client we just put into the database in the last test
        Client client = dao.getClient(c.getId());
        Assertions.assertEquals(c.getId(), client.getId());
        Assertions.assertEquals(c.getName(), client.getName());
        Assertions.assertEquals(0,client.getAccounts().size());
    }

    @Test
    @Order(3)
    void create_account() {
        // create new accounts and add them to the persistent client
        Account a = dao.createAccount(c.getId(), 300);
        Account b = dao.createAccount(c.getId(), 500);
        c.addAccount(a);
        c.addAccount(b);

        // assert that the id's are different, the client id's are the same and that they
        // point to the client Id we gave them
        Assertions.assertNotEquals(a.getId(), b.getId());
        Assertions.assertEquals(a.getClientId(), b.getClientId());
        Assertions.assertEquals(c.getId(), a.getClientId());
    }

    @Test
    @Order(4)
    void get_client_2() {
        // check that get client returns the associated accounts
        Client client = dao.getClient(c.getId());
        Assertions.assertEquals(client.getId(), c.getId());
        Assertions.assertEquals(client.getAccounts().size(), c.getAccounts().size());
        Assertions.assertEquals(client.getName(), c.getName());

        // check that each account is returned and that the amounts are the same
        for(Account a : c.getAccounts()) {
            Account check = client.getAccountById(a.getId());
            Assertions.assertNotNull(check);
            Assertions.assertEquals(check.getClientId(), a.getClientId());
            Assertions.assertEquals(check.getAmount(), a.getAmount(), 0.1);
        }
    }

    @Test
    @Order(5)
    void get_all_clients() {
        // check that our client is included in get all clients
        // can't do much more than that without guaranteeing a fresh DB
        HashSet<Client> allClients = dao.getAllClients();
        Assertions.assertNotEquals(0, allClients.size());
        boolean found = false;
        for(Client client: allClients) {
            if(client.getId() == c.getId()) {
                Assertions.assertEquals(client.getName(), c.getName());
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found);
    }

    @Test
    @Order(6)
    void update_client() {
        // Change client name
        c.setName("Test Client Updated");
        Client client = dao.updateClient(c);

        // Assert that the returned client has updated fields
        Assertions.assertEquals(c.getId(), client.getId());
        Assertions.assertEquals(c.getName(), client.getName());

        // get the client from the database and assert that all fields are the same
        Client client2 = dao.getClient(c.getId());
        Assertions.assertEquals(c.getId(), client2.getId());
        Assertions.assertEquals(c.getName(), client2.getName());
        Assertions.assertEquals(c.getAccounts().size(), client2.getAccounts().size());
        for(Account a : c.getAccounts()) {
            Account check = client2.getAccountById(a.getId());
            Assertions.assertNotNull(check);
            Assertions.assertEquals(check.getClientId(), a.getClientId());
            Assertions.assertEquals(check.getAmount(), a.getAmount(), 0.1);
        }
    }

    @Test
    @Order(7)
    void update_account() {
        // Assumes validity of client-account id pair
        // Validity is checked in the service method
        HashMap<Integer, Account> accounts = new HashMap<>();
        for(Account a : c.getAccounts()) {
            // make a copy and store the copies
            Account ogAccount = new Account(a.getId(), a.getAmount(), a.getClientId());
            accounts.put(ogAccount.getId(), ogAccount);

            // increase the amount in the account and update with database
            a.setAmount(a.getAmount()+500);
            dao.updateAccount(a);
        }
        Client client = dao.getClient(c.getId());
        for(Account a : c.getAccounts()) {
            Account check = client.getAccountById(a.getId());
            Account original = accounts.get(a.getId());
            // compare to Client c
            Assertions.assertNotNull(check);
            Assertions.assertEquals(check.getClientId(), a.getClientId());
            Assertions.assertEquals(check.getAmount(), a.getAmount(), 0.1);

            // compare to original accounts before modifications
            Assertions.assertEquals(original.getId(), check.getId());
            Assertions.assertEquals(original.getAmount()+500, check.getAmount(), 0.1);
            Assertions.assertEquals(original.getClientId(), check.getClientId());
        }
    }

    @Test
    @Order(8)
    void delete_account() {
        // Assumes validity of client-account id pair
        // Validity is check in the service method
        Account toBeDeleted = new Account();
        for(Account a : c.getAccounts()) {
            toBeDeleted = a;
            break;
        }
        boolean deleteCheck = dao.deleteAccount(toBeDeleted.getClientId(), toBeDeleted.getId());
        Assertions.assertTrue(deleteCheck);

        // check that client now has 1 fewer account
        Client client = dao.getClient(c.getId());
        Assertions.assertNotEquals(client.getAccounts().size(), c.getAccounts().size());

        // check that the account was not returned by getClient
        Account notExistCheck = client.getAccountById(toBeDeleted.getId());
        Assertions.assertNull(notExistCheck);

        c = client; // setup for next test
    }

    @Test
    @Order(9)
    void delete_client() {
        // check that the function returns true
        boolean deleteCheck = dao.deleteClientById(c.getId());
        Assertions.assertTrue(deleteCheck);

        // check that you cannot get the client with getClient
        Client client = dao.getClient(c.getId());
        Assertions.assertNull(client);

        // check that accounts associated with the client have been deleted
        // need to access DB directly for this part unfortunately
        HashSet<Account> accounts = BankDaoImpl.getAccountsByClientId(c.getId());
        Assertions.assertEquals(0, accounts.size());
    }
}
