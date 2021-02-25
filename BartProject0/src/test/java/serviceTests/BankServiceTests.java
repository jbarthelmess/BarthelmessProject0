package serviceTests;

//import com.google.gson.Gson;
import daos.BankDAO;
import daos.BankDaoImpl;
import entities.Account;
import entities.Client;
import exceptions.InvalidNameException;
import org.junit.jupiter.api.*;
import services.BankService;
import services.BankServiceImpl;

import java.util.HashSet;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BankServiceTests {
    //private static Gson gson = new Gson();
    private static final BankDAO dao = new BankDaoImpl();
    private static final BankService service = new BankServiceImpl(dao);
    private static Client client = null;
    private static Client client2 = null;

    @Test
    @Order(1)
    void create_client() {
        // Basic Create a client test
        client = new Client();
        client.setName("Test Client");
        Client c = service.createClient(client);
        Assertions.assertEquals(client.getName(), c.getName());
        Assertions.assertNotEquals(0, c.getId());
        client = c;
    }

    @Test
    @Order(2)
    void create_client_2() {
        // No-args constructor gives client an empty String for a name
        // trying to create a client with no name should throw an exception
        Client bad = new Client();
        InvalidNameException check = Assertions.assertThrows(InvalidNameException.class, ()-> service.createClient(bad));
        System.out.println(check.getMessage());
    }

    @Test
    @Order(3)
    void create_account() {
        Account a = service.createAccount(client.getId(), 300);
        Account b = service.createAccount(client.getId(), 500);
        Assertions.assertNotEquals(a.getId(), b.getId());
        Assertions.assertEquals(client.getId(), a.getClientId());
        Assertions.assertEquals(a.getClientId(), b.getClientId());
        Assertions.assertNotEquals(-1, a.getId());
        Assertions.assertNotEquals(-1, b.getId());
        client.addAccount(a);
        client.addAccount(b);
    }

    @Test
    @Order(4)
    void create_account_2() {
        // creates an account with zero for amount
        Account empty = service.createAccount(client.getId());
        Assertions.assertEquals(client.getId(), empty.getClientId());
        Assertions.assertNotEquals(-1, empty.getId());
        Assertions.assertEquals(0, empty.getAmount());
        client.addAccount(empty);
    }

    @Test
    @Order(5)
    void get_client() {
        Client c = service.getClient(client.getId());
        Assertions.assertEquals(client.getId(), c.getId());
        Assertions.assertEquals(client.getName(), c.getName());
        Assertions.assertEquals(client.getAccounts().size(), c.getAccounts().size());
        // check that client attributes are the same

        // check that each account is the same
        for(Account i : client.getAccounts()) {
            Account check = c.getAccountById(i.getId());
            Assertions.assertEquals(i.getAmount(), check.getAmount());
            Assertions.assertEquals(i.getClientId(), check.getClientId());
        }
    }

    @Test
    @Order(6)
    void get_client_2() {
        // test both versions of the method for completeness
        Client c = service.getClient(client);
        Assertions.assertEquals(client.getId(), c.getId());
        Assertions.assertEquals(client.getName(), c.getName());
        Assertions.assertEquals(client.getAccounts().size(), c.getAccounts().size());
        for(Account i : client.getAccounts()) {
            Account check = c.getAccountById(i.getId());
            Assertions.assertEquals(i.getAmount(), check.getAmount());
            Assertions.assertEquals(i.getClientId(), check.getClientId());
        }
    }

    @Test
    @Order(7)
    void get_all_clients() {
        HashSet<Client> allClients = service.getAllClients();
        Assertions.assertNotEquals(0, allClients.size());
        for(Client c : allClients) {
            if(c.getId() == client.getId()) {
                Assertions.assertEquals(client.getId(), c.getId());
                Assertions.assertEquals(client.getName(), c.getName());
                Assertions.assertNull(c.getAccounts());
                break;
            }
        }
    }

    @Test
    @Order(8)
    void get_account() {
        Account needle = client.getAccounts().iterator().next();

        Account check = service.getAccount(client.getId(), needle.getId());
        Assertions.assertNotNull(check);
        Assertions.assertEquals(needle.getId(),check.getId());
        Assertions.assertEquals(needle.getClientId(), check.getClientId());
        Assertions.assertEquals(needle.getAmount(), check.getAmount(), 0.1);
    }

    @Test
    @Order(9)
    void get_account_2() {
        int sumOfIds = 0; // finding the sum of the Account id's will give an account that our test client does not have
        for(Account a : client.getAccounts()) {
            sumOfIds += a.getId();
        }
        Account check = service.getAccount(client.getId(), sumOfIds);
        Assertions.assertNull(check);
    }

    @Test
    @Order(10)
    void get_account_3() {
        // same idea as above but for client ids, bad client should return null
        // in order for that to work tho, we need to guarantee there are at least
        // 2 clients in the database, so we make another client first
        client2 = service.createClient(new Client("Test 2"));
        int sumOfIds = 0;
        HashSet<Client> allClients = service.getAllClients();
        for(Client c : allClients) {
            sumOfIds += c.getId();
        }
        // want to give a valid account
        Account needle = client.getAccounts().iterator().next();

        Account check = service.getAccount(sumOfIds, needle.getId());
        Assertions.assertNull(check);

        // also want to return null in the event both are valid but there is a mismatch
        // client2 is a valid user and needle is a valid account but client2 doesn't own
        // needle, so should return null
        check = service.getAccount(client2.getId(), needle.getId());
        Assertions.assertNull(check);
    }

    @Test
    @Order(11)
    void get_accounts_in_range() {
        HashSet<Account> accounts = service.getAccountsInRange(client.getId(), 200, 1000);
        for(Account a : accounts) {
            Account check = client.getAccountById(a.getId());
            Assertions.assertNotNull(check);
            Assertions.assertEquals(check.getAmount(), a.getAmount(), 0.1);
            Assertions.assertEquals(check.getClientId(), a.getClientId());
            Assertions.assertTrue(a.getAmount() <= 1000);
            Assertions.assertTrue(a.getAmount() >= 0);
        }

        // when the floor is bigger than the ceiling, should get back no results
        accounts = service.getAccountsInRange(client.getId(), 500, 400);
        Assertions.assertEquals(0, accounts.size());

        // should return null if invalid client id is given;
        int sumOfIds = 0;
        HashSet<Client> allClients = service.getAllClients();
        for(Client c : allClients) {
            sumOfIds += c.getId();
        }
        accounts = service.getAccountsInRange(sumOfIds, 0, 10000);
        Assertions.assertNull(accounts);

        // if a negative number is given for ceiling, should return all accounts for a client, higher than the floor
        // if a negative number is given for floor it will be reset to zero

        // should return all accounts
        accounts = service.getAccountsInRange(client.getId(), -50000, -2);
        Assertions.assertNotNull(accounts);
        Assertions.assertEquals(client.getAccounts().size(), accounts.size());
    }

    @Test
    @Order(12)
    void update_client() {
        client.setName("Test Update Client");
        Client c = service.updateClient(client);
        Assertions.assertEquals(client.getName(), c.getName());

        Client d = service.getClient(client.getId());
        Assertions.assertEquals(client.getId(), d.getId());
        Assertions.assertEquals(client.getName(), d.getName());
        Assertions.assertEquals(client.getAccounts().size(), d.getAccounts().size());
        for(Account i : client.getAccounts()) {
            Account check = d.getAccountById(i.getId());
            Assertions.assertEquals(i.getAmount(), check.getAmount());
            Assertions.assertEquals(i.getClientId(), check.getClientId());
        }
    }

    @Test
    @Order(13)
    void update_account() {
        HashSet<Account> originalAccounts = new HashSet<>();
        // make and save a copy of the clients original account values
        for(Account a : client.getAccounts()) {
            Account copy = new Account(a.getId(), a.getAmount(), a.getClientId());
            originalAccounts.add(copy);
        }

        // change the values and update the accounts
        for(Account a : client.getAccounts()) {
            a.setAmount(a.getAmount()+400);
            service.updateAccount(a);
        }

        // get the client and accounts from the database and make sure they were changed correctly
        Client c = service.getClient(client);
        for(Account a : originalAccounts) {
            Account check = c.getAccountById(a.getId());
            Assertions.assertNotNull(check);
            Assertions.assertEquals(a.getClientId(), check.getClientId());
            Assertions.assertEquals(a.getAmount()+400, check.getAmount(), 0.1);
        }
    }

    @Test
    @Order(14)
    void update_account_2() {
        // need to make sure that the accounts can only be updated with the proper clientId, should fail otherwise
        Account first = client.getAccounts().iterator().next();

        // mismatch is a copy, but with different client Id
        // we use client2 because it's valid in the database
        Account mismatch = new Account(first.getId(), first.getAmount()+200, client2.getId());
        Account nullCheck = service.updateAccount(mismatch);
        Assertions.assertNull(nullCheck);

        // get the account with the id we used to check, should not have changed
        Account check = service.getAccount(client.getId(), mismatch.getId());
        Assertions.assertEquals(first.getAmount(), check.getAmount(), 0.1);
    }

    @Test
    @Order(15)
    void delete_account() {
        Account toBeDeleted = client.getAccounts().iterator().next();
        boolean isDeleted = service.deleteAccount(toBeDeleted.getClientId(), toBeDeleted.getId());
        Assertions.assertTrue(isDeleted);
        Account getDeleted = service.getAccount(client.getId(), toBeDeleted.getId());
        Assertions.assertNull(getDeleted);
        HashSet<Account> accounts = client.getAccounts();
        accounts.remove(toBeDeleted);
        client.setAccounts(accounts);
    }

    @Test
    @Order(16)
    void delete_account_2() {
        // need to have correct client id to delete account
        Account protect = client.getAccounts().iterator().next();
        boolean isDeleted = service.deleteAccount(client2.getId(), protect.getId());
        Assertions.assertFalse(isDeleted);
    }

    @Test
    @Order(17)
    void delete_client_not_in_database() {
        // try to delete a client that doesn't exist
        int sumOfIds = 0;
        HashSet<Client> allClients = service.getAllClients();
        for(Client c : allClients) {
            sumOfIds+=c.getId();
        }
        boolean isDeleted = service.deleteClientById(sumOfIds);
        Assertions.assertFalse(isDeleted);
    }

    @Test
    @Order(18)
    void delete_client() {
        // delete client 2 first, client with no accounts
        boolean isDeleted = service.deleteClientById(client2.getId());
        Assertions.assertTrue(isDeleted);

        Client c = service.getClient(client2.getId());
        Assertions.assertNull(c);

        service.deleteClientById(client.getId());
    }
}
