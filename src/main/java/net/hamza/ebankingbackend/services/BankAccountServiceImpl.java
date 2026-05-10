package net.hamza.ebankingbackend.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hamza.ebankingbackend.dtos.CustomerDTO;
import net.hamza.ebankingbackend.entities.*;
import net.hamza.ebankingbackend.enums.AccountStatus;
import net.hamza.ebankingbackend.enums.OperationType;
import net.hamza.ebankingbackend.exceptions.BalanceNotSufficientException;
import net.hamza.ebankingbackend.exceptions.BankAccountNotFoundException;
import net.hamza.ebankingbackend.exceptions.CustomerNotFoundException;
import net.hamza.ebankingbackend.mappers.BankAccountMapperImpl;
import net.hamza.ebankingbackend.repositories.AccountOperationRepository;
import net.hamza.ebankingbackend.repositories.BankAccountRepository;
import net.hamza.ebankingbackend.repositories.CustomerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService{
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl mapper;

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new customer");
        Customer customer = mapper.fromCustomerDTO(customerDTO);
        CustomerDTO savedCustomer = mapper.fromCustomer(customerRepository.save(customer));
        return savedCustomer;
    }

    @Override
    public CurrentAccount saveCurrentBankAccount(double initialBalance, Long customerId, String currency, double overDraft) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer == null) {
//            log.error("Customer does not exist");
//            return null;
            throw new CustomerNotFoundException("Customer not found");
        }
        log.info("Saving currentAccount");
        CurrentAccount currentBankAccount = new CurrentAccount();;
        currentBankAccount.setId(UUID.randomUUID().toString());
        currentBankAccount.setCustomer(customer);
        currentBankAccount.setCreatedAt(new Date());
        currentBankAccount.setBalance(initialBalance);
        currentBankAccount.setCurrency(currency);
        currentBankAccount.setOverDraft(overDraft);
        currentBankAccount.setStatus(AccountStatus.CREATED);
        CurrentAccount savedCurrentAccount = bankAccountRepository.save(currentBankAccount);
        return savedCurrentAccount;
    }

    @Override
    public SavingAccount saveSavingBankAccount(double initialBalance, Long customerId, String currency, double interestRate) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        log.info("Saving saveAccount");
        SavingAccount savingBankAccount = new SavingAccount();;
        savingBankAccount.setId(UUID.randomUUID().toString());
        savingBankAccount.setCustomer(customer);
        savingBankAccount.setCreatedAt(new Date());
        savingBankAccount.setBalance(initialBalance);
        savingBankAccount.setCurrency(currency);
        savingBankAccount.setInterestRate(interestRate);
        savingBankAccount.setStatus(AccountStatus.CREATED);
        SavingAccount savedSavingAccount = bankAccountRepository.save(savingBankAccount);
        return savedSavingAccount;
    }

    @Override
    public List<CustomerDTO> listCustomer() {
        log.info("Loading customers");
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOS = new ArrayList<>();
        customers.stream().forEach(customer -> {
            customerDTOS.add(mapper.fromCustomer(customer));
        });
        return customerDTOS;
    }

    @Override
    public BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException {
        log.info("searching for the bank account");
        return bankAccountRepository.findById(accountId).orElseThrow(
                () -> new BankAccountNotFoundException("Bank account not found"));
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException{
        BankAccount bankAccount = getBankAccount(accountId);
        if(bankAccount.getBalance() < amount) {
            throw new BalanceNotSufficientException("balance is not sufficient !!!");
        }
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);



    }

    @Override
    public void credit(String accountId, double amount, String description) {
        BankAccount bankAccount = getBankAccount(accountId);
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException{
        log.info("transfering");
        debit(accountIdSource, amount, "Transfer to " + accountIdDestination );
        credit(accountIdDestination, amount, "transfer from " + accountIdSource);

    }

    @Override
    public List<BankAccount> bankAccountList() {
        return bankAccountRepository.findAll();
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException{
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found !!!"));
            return mapper.fromCustomer(customer);
    }
}
