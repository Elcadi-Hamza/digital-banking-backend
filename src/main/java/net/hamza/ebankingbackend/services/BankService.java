package net.hamza.ebankingbackend.services;

import jakarta.transaction.Transactional;
import net.hamza.ebankingbackend.entities.BankAccount;
import net.hamza.ebankingbackend.entities.CurrentAccount;
import net.hamza.ebankingbackend.entities.SavingAccount;
import net.hamza.ebankingbackend.repositories.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class BankService {
    @Autowired
    BankAccountRepository bankAccountRepository;
    public void consulter () {
        BankAccount bankAccount = bankAccountRepository.findById("3a44c83c-9b54-45a5-9385-7182012ce4a9").orElse(null);
        if(bankAccount != null) {
            System.out.println("*********************************************************");
            System.out.println(bankAccount.getId());
            System.out.println(bankAccount.getBalance());
            System.out.println(bankAccount.getStatus());
            System.out.println(bankAccount.getCreatedAt());
            System.out.println(bankAccount.getCustomer().getName());
            //pour afficher le nom de la compte
            System.out.println(bankAccount.getClass().getSimpleName());
            if (bankAccount instanceof CurrentAccount) {
                System.out.println("OverDraft = " + ((CurrentAccount) bankAccount).getOverDraft());
            } else if (bankAccount instanceof SavingAccount) {
                System.out.println("IntrestRate = " + ((SavingAccount) bankAccount).getInterestRate());
            }
            System.out.println("Operations : ");
            final int[] i = {1};
            bankAccount.getAccountOperations().forEach(operation -> {
                System.out.println("=================================");
                System.out.println("operation " + i[0]);
                System.out.println(operation.getType() + "\t" + operation.getDate() + "\t" + operation.getAmount() + "\t" + operation.getDescription());
                i[0]++;
            });

            System.out.println("*********************************************************");
        }
    }
}
