package net.hamza.ebankingbackend;

import net.hamza.ebankingbackend.entities.AccountOperation;
import net.hamza.ebankingbackend.entities.CurrentAccount;
import net.hamza.ebankingbackend.entities.Customer;
import net.hamza.ebankingbackend.entities.SavingAccount;
import net.hamza.ebankingbackend.enums.AccountStatus;
import net.hamza.ebankingbackend.enums.OperationType;
import net.hamza.ebankingbackend.repositories.AccountOperationRepository;
import net.hamza.ebankingbackend.repositories.BankAccountRepository;
import net.hamza.ebankingbackend.repositories.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankingBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner start(CustomerRepository customerRepository, BankAccountRepository bankAccountRepository, AccountOperationRepository accountOperationRepository) {
        return args -> {
            //creating customers
            Stream.of("name1","name2","name3").forEach(name -> {
                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                customerRepository.save(customer);
                System.out.println("here");
            });
            //Creating bankAccnount for each customer tha we created
            customerRepository.findAll().forEach(customer -> {
                //current account
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(10);
                currentAccount.setCreatedAt(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(customer);
                currentAccount.setCurrency("current account currency of " + customer.getName());
                currentAccount.setOverDraft(1000);
                bankAccountRepository.save(currentAccount);

                //saving account
                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setCustomer(customer);
                savingAccount.setBalance(100);
                savingAccount.setCreatedAt(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setInterestRate(1.1);
                savingAccount.setCurrency("saving account currency of " + customer.getName());
                bankAccountRepository.save(savingAccount);
            });
            bankAccountRepository.findAll().forEach(acc -> {
                for (int i = 0 ; i < 10 ; i++) {
                    AccountOperation accountOperation = new AccountOperation();
                    accountOperation.setDate(new Date());
                    accountOperation.setBankAccount(acc);
//                    if(i % 2 == 0) {
//                        accountOperation.setType(OperationType.CREDIT);
//                    } else {
//                        accountOperation.setType(OperationType.DEBIT);
//                    }
                    accountOperation.setType(Math.random() < 0.5 ? OperationType.CREDIT : OperationType.DEBIT);
                    accountOperation.setAmount(i*10);
                    accountOperation.setDescription("Descreption " + i);
                    accountOperationRepository.save(accountOperation);
                }
            });
        };
    }
}
