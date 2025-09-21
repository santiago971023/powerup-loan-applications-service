package co.com.pragma.model.notification;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Notification {

    private String userEmail;
    private String userName;
    private Long loanAppId;
    private String newStatus;
    private BigDecimal loanAmount;

}
