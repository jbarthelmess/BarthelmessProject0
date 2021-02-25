package entities;

import exceptions.InvalidIdException;

import java.util.*;
//import java.util.HashMap;


public class Client {
    private String name;
    private int id;
    private HashSet<Account> acc;
    private static final Comparator<Account> compareById = (a1, a2) -> {
        if(a1.getId() > a2.getId())
            return 1;
        else if(a2.getId() > a1.getId())
            return -1;
        return 0;
    };

    private static final Comparator<Account> compareByAmount = (a1, a2) -> {
        if(a1.getAmount() > a2.getAmount())
            return 1;
        else if(a2.getAmount() > a1.getAmount())
            return -1;
        return 0;
    };

    public Client(String name) {
        if(name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.name = name;
        this.acc = null;
        this.id = -1;
    }

    public Client() {
        this.name = "";
        this.id = -1;
        this.acc = null;
    }

    public Client(int id) {
        if(id < 0) {
            throw new IllegalArgumentException("ID cannot be negative");
        }
        this.id = id;
        this.name = "";
        this.acc = null;
    }

    public Client(int id, Set<Account> acc) {
        if(id < 0) {
            throw new IllegalArgumentException("ID cannot be negative");
        }
        this.id = id;
        if(acc == null) {
            throw new IllegalArgumentException("Account Map cannot be null");
        }
        this.acc = new HashSet<>(acc);
        this.name = "";
    }

    public Client(int id, Set<Account> acc, String name) {
        if(id < 0) {
            throw new IllegalArgumentException("ID cannot be negative");
        }
        this.id = id;
        if(acc == null) {
            throw new IllegalArgumentException("Account Map cannot be null");
        }
        this.acc = new HashSet<>(acc);
        if(name == null) {
            throw new IllegalArgumentException("Name cannot be Null");
        }
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public HashSet<Account> getAccounts() {
        return this.acc;
    }

    public String getName() {
        return this.name;
    }

    public void setId(int id) {
        if(id < 0) {
            throw new IllegalArgumentException("Client ID cannot be negative");
        }
        this.id = id;
    }

    public void setAccounts(Set<Account> accounts) {
        if(accounts == null) {
            this.acc = null;
        }
        else {
            this.acc = new HashSet<>(accounts);
        }
    }

    public void setName(String name) {
        if(name == null) {
            throw new NullPointerException();
        }
        this.name = name;
    }

    public Account getAccountById(int id) {
        if(this.acc == null) return null;
        for(Account a : this.acc) {
            if(a.getId() == id)
                return a;
        }
        return null;
    }

    public boolean addAccount(int id, double amount) {
        Account a = new Account(id, amount, this.id);
        return this.addAccount(a);
    }

    public boolean addAccount(Account a) {
        if(a == null) {
            throw new NullPointerException();
        }
        a.setClientId(this.id);
        if(this.acc == null) {
            this.acc = new HashSet<>();
        }
        int size = this.acc.size();
        this.acc.add(a);
        return this.acc.size() != size; // if the size didn't change, account already exists
    }

    @Override
    public String toString() {
        return "Client{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", acc=" + acc +
                '}';
    }
}
