package net.hamza.ebankingbackend.services;

import net.hamza.ebankingbackend.dtos.CustomerDTO;
import net.hamza.ebankingbackend.entities.BankAccount;
import net.hamza.ebankingbackend.entities.CurrentAccount;
import net.hamza.ebankingbackend.entities.Customer;
import net.hamza.ebankingbackend.entities.SavingAccount;
import net.hamza.ebankingbackend.exceptions.BalanceNotSufficientException;
import net.hamza.ebankingbackend.exceptions.BankAccountNotFoundException;
import net.hamza.ebankingbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
    Customer saveCustomer (Customer customer);
    CurrentAccount saveCurrentBankAccount(double initialBalance, Long customerId, String currency, double overDraft) throws CustomerNotFoundException;
    SavingAccount saveSavingBankAccount(double initialBalance, Long customerId, String currency, double interestRate) throws CustomerNotFoundException;
    List<CustomerDTO> listCustomer();
    BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debit(String accountId, double amount, String description) throws BankAccountNotFoundException;
    void credit(String accountId, double amount, String description);
    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;

    List<BankAccount> bankAccountList();
}
