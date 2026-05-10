package net.hamza.ebankingbackend;

import net.hamza.ebankingbackend.entities.*;
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

                BankAccount bankAccount = bankAccountRepository.findById("03630f41-f92f-4aac-a9ab-4ebed2f41703").orElse(null);
                System.out.println("*********************************************************");
                System.out.println(bankAccount.getId());
                System.out.println(bankAccount.getBalance());
                System.out.println(bankAccount.getStatus());
                System.out.println(bankAccount.getCreatedAt());
                System.out.println(bankAccount.getCustomer().getName());
                //pour afficher le nom de la compte
                System.out.println(bankAccount.getClass().getSimpleName());
                if(bankAccount instanceof CurrentAccount) {
                    System.out.println("OverDraft = " + ((CurrentAccount) bankAccount).getOverDraft());
                } else if (bankAccount instanceof SavingAccount) {
                    System.out.println("IntrestRate = " + ((SavingAccount) bankAccount).getInterestRate());
                }
                System.out.println("Operations : ");
                final int[] i = {1};
                bankAccount.getAccountOperations().forEach(operation -> {
                    System.out.println("operation "+ i[0] + "===================== " );
                    System.out.println(operation.getType());
                    System.out.println(operation.getDate());
                    System.out.println(operation.getAmount());
                    System.out.println(operation.getDescription());
                    i[0]++;
                });

                System.out.println("*********************************************************");
            });
        };
    }
}
