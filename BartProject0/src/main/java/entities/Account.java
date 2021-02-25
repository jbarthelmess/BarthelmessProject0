package entities;

import exceptions.InvalidIdException;

public class Account {
    private int id;
    private double amount;
    private int clientId;

    public Account() {
        this.id = -1;
        this.amount = 0;
        this.clientId = -1;
    }

    public Account(double amount) {
        if(amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.id = -1;
        this.amount = amount;
        this.clientId = -1;
    }

    public Account(int id, double amount) {
        if(id < 0) {
            throw new InvalidIdException("Account ID cannot be negative");
        }
        this.id = id;
        if(amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
        this.clientId = -1;
    }

    public Account(int id, double amount, int clientId) {
        if(id < 0) {
            throw new InvalidIdException("Account ID cannot be negative");
        }
        this.id = id;
        if(amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
        if(clientId < 0) {
            throw new InvalidIdException("Client ID cannot be negative");
        }
        this.clientId = clientId;
    }

    public int getId() {
        return this.id;
    }

    public double getAmount() {
        return this.amount;
    }

    public int getClientId() {
        return this.clientId;
    }

    public void setId(int id) {
        if(id < 0) {
            throw new InvalidIdException("Account ID cannot be negative");
        }
        this.id = id;
    }

    public void setAmount(double amount) {
        if(amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
    }

    public void setClientId(int clientId) {
        if(clientId < 0) {
            throw new InvalidIdException("Client ID cannot be negative");
        }
        this.clientId = clientId;
    }

    public boolean spend(double amount) {
        if(amount < 0) {
            throw new IllegalArgumentException("Cannot Spend negative money");
        }
        if(amount > this.amount) {
            return false;
        }
        this.amount = this.amount - amount;
        return true;
    }

    public boolean add(double amount) {
        if(amount < 0) {
            throw new IllegalArgumentException("Cannot add negative money");
        }
        this.amount = this.amount + amount;
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Account) {
            Account other = (Account) obj;
            if(other.id == this.id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}
