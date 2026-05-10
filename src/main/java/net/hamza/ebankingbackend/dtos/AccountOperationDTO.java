package net.hamza.ebankingbackend.dtos;

import lombok.Data;
import net.hamza.ebankingbackend.enums.OperationType;

import java.util.Date;

@Data
public class AccountOperationDTO {
    private Long id;
    private Date date;
    private double amount;
    private OperationType type;
    private String description;
}
