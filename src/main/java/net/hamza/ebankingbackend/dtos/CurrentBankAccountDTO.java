package net.hamza.ebankingbackend.dtos;

import lombok.Data;
import net.hamza.ebankingbackend.enums.AccountStatus;

import java.util.Date;

@Data
public class CurrentBankAccountDTO extends BankAccountDTO {
    private String id;
    private double balance;
    private Date createdAt;
    private AccountStatus Status;
    private CustomerDTO customerDTO;
    private double intrestRate;

}
