package model;

import java.io.Serializable;

public class ClientStat implements Serializable {
    private int id;
    private String period;
    private int newRegistrations;
    private int activeCustomers;
    private String transactionMethod;
    private int clientId;

    public ClientStat() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public int getNewRegistrations() { return newRegistrations; }
    public void setNewRegistrations(int newRegistrations) { this.newRegistrations = newRegistrations; }

    public int getActiveCustomers() { return activeCustomers; }
    public void setActiveCustomers(int activeCustomers) { this.activeCustomers = activeCustomers; }

    public String getTransactionMethod() { return transactionMethod; }
    public void setTransactionMethod(String transactionMethod) { this.transactionMethod = transactionMethod; }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }
}
