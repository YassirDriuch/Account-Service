package account.Controller;

import account.DTO.PaymentDTO;
import account.DTO.SalaryDTO;
import account.Service.SalaryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class SalaryController {

    @Autowired
    SalaryService salaryService;

    @PostMapping("api/acct/payments")
    public ResponseEntity<Map<String, String>> saveSalaries(@RequestBody @Valid ArrayList<PaymentDTO> payments) {
        return salaryService.savePayments(payments);
    }

    @PutMapping("api/acct/payments")
    public ResponseEntity<Map<String, String>> updateSalary(@RequestBody @Valid PaymentDTO paymentDTO){
        return salaryService.updatePayment(paymentDTO);
    }

    @GetMapping("api/empl/payment")
    public ResponseEntity<?> getSalaries(@AuthenticationPrincipal UserDetails user, @RequestParam @Pattern(regexp = "(0[1-9]|1[0-2])-20[0-3][0-9]") @Valid Optional<String> period) {
        return period.isEmpty() ? salaryService.getPayments(user.getUsername()) : salaryService.getPayment(user.getUsername(), period.get());
    }

}
