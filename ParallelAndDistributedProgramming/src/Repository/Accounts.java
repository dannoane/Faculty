package Repository;

import Model.Account;

import java.util.ArrayList;
import java.util.List;

public class Accounts {

    private static int id = 0;
    private List<Account> accounts;

    public Accounts() {
        this.accounts = new ArrayList<>();
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public int getSize() {
        return accounts.size();
    }

    public Account getAccount(int index) {
        return accounts.get(index);
    }

    public void addAccount(Account account) {

        account.setId(id++);
        accounts.add(account);
    }
}
