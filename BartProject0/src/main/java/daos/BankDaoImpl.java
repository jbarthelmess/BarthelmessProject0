package daos;

import entities.Client;
import entities.Account;
import utils.ConnectionUtil;

import org.apache.log4j.Logger;

//import javax.xml.transform.Result;
import java.sql.*;
//import java.util.HashMap;
import java.util.HashSet;

public class BankDaoImpl implements BankDAO {

    //Database Instance id: bank-api-db
    //Database Password: xNcFL0HLa7zLooIq
    static Logger logger = Logger.getLogger(BankDaoImpl.class.getName());

    @Override
    public Client createClient(Client c) {
        try (Connection conn = ConnectionUtil.createConnection()) {
            String query = "insert into client (full_name) values (?)";
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, c.getName());
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();

            c.setId(rs.getInt("client_id"));
            return c;
        }
        catch (SQLException s) {
            s.printStackTrace();
            logger.error("Could not create client", s);
            return null;
        }
    }

    @Override
    public Account createAccount(int client_id, double amount) {
        try (Connection conn = ConnectionUtil.createConnection()){
            String query = "insert into account (client_id, amount) values (?,?)";
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, client_id);
            ps.setDouble(2, amount);
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();

            Account a = new Account();
            a.setClientId(client_id);
            a.setAmount(amount);
            a.setId(rs.getInt("account_id"));
            return a;
        }
        catch (SQLException s) {
            s.printStackTrace();
            logger.error("Could not create account", s);
            return null;
        }
    }

    @Override
    public Client getClient(int id) {
        try (Connection conn = ConnectionUtil.createConnection()) {
            String query = "select * from client where client_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()) return null;
            Client client = new Client();
            client.setName(rs.getString("full_name"));
            client.setId(id);
            client.setAccounts(BankDaoImpl.getAccountsByClientId(id));
            return client;
        }
        catch (SQLException s) {
            s.printStackTrace();
            logger.error("Could not retrieve client", s);
            return null;
        }
    }

    @Override
    public HashSet<Client> getAllClients() {
        try (Connection conn = ConnectionUtil.createConnection()) {
            String query = "select * from client";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            HashSet<Client> allClients = new HashSet<>();
            while(rs.next()) {
                Client client = new Client();
                client.setId(rs.getInt("client_id"));
                client.setName(rs.getString("full_name"));
                allClients.add(client);
            }
            return allClients;
        }
        catch (SQLException s) {
            s.printStackTrace();
            logger.error("Could not retrieve clients", s);
            return null;
        }
    }

    @Override
    public Client updateClient(Client c) {
        try (Connection conn = ConnectionUtil.createConnection()) {
            String query = "update client set full_name = ? where client_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, c.getName());
            ps.setInt(2, c.getId());
            int success = ps.executeUpdate();
            return success > 0? c : null;
        }
        catch (SQLException s) {
            s.printStackTrace();
            logger.error("Could not update client", s);
            return null;
        }
    }

    @Override
    public Account updateAccount(Account account) {
        try (Connection conn = ConnectionUtil.createConnection()) {
            String query = "update account set amount = ? where account_id = ? and client_id =?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setDouble(1, account.getAmount());
            ps.setInt(2, account.getId());
            ps.setInt(3, account.getClientId());
            ps.executeUpdate();
            int success = ps.executeUpdate();
            return success > 0? account : null;
        }
        catch (SQLException s) {
            s.printStackTrace();
            logger.error("Could not update account", s);
            return null;
        }
    }

    @Override
    public boolean deleteClientById(int id) {
        try (Connection conn = ConnectionUtil.createConnection()) {
            // Database has cascades for deleting associated accounts with the client
            String query = "delete from client where client_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            int success = ps.executeUpdate();
            return success > 0;
        }
        catch (SQLException s) {
            s.printStackTrace();
            logger.error("Could not delete client", s);
            return false;
        }
    }

    @Override
    public boolean deleteAccount(int clientId, int id) {
        try (Connection conn = ConnectionUtil.createConnection()) {
            String query = "delete from account where account_id = ? and client_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ps.setInt(2, clientId);
            int success = ps.executeUpdate();
            return success > 0;
        }
        catch (SQLException s) {
            s.printStackTrace();
            logger.error("Could not delete account",s);
            return false;
        }
    }

    public static HashSet<Account> getAccountsByClientId(int id) {
        try(Connection conn = ConnectionUtil.createConnection()) {
            String query = "select * from account where client_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            HashSet<Account> accounts = new HashSet<>();
            while (rs.next()) {
                Account a = new Account();
                a.setClientId(id);
                a.setId(rs.getInt("account_id"));
                a.setAmount(rs.getDouble("amount"));
                accounts.add(a);
            }
            return accounts;
        }
        catch(SQLException s) {
            s.printStackTrace();
            logger.error("Could not get accounts", s);
            return null;
        }
    }
}
