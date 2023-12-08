package account.Service;

import account.DTO.PaymentDTO;
import account.DTO.SalaryDTO;
import account.ExceptionHandler.ImportSalaryException;
import account.ExceptionHandler.UserNotFoundException;
import account.Repository.UserRepository;
import account.model.Payment;
import account.model.User;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
public class SalaryService {
    @Autowired
    UserRepository userRepo;

    private static final Logger log = LoggerFactory.getLogger(SalaryService.class);
    @Transactional
    public ResponseEntity<Map<String, String>> savePayments(ArrayList<PaymentDTO> payments){
        StringBuilder errorMessage = new StringBuilder();

        for (PaymentDTO paymentDTO : payments) {
            User user = getUser(paymentDTO.getEmployee());
            if (paymentDTO.getSalary() < 0) {
                errorMessage.append((errorMessage.length() == 0) ? "payments[" + payments.indexOf(paymentDTO) + "].salary: Salary must be non negative!" : ", payments[" + payments.indexOf(paymentDTO) + "].salary: Salary must be non negative!");
            } else if (!paymentDTO.getPeriod().matches("(0[1-9]|1[0-2])-20[0-3][0-9]") |
                    payments.stream().filter(p ->
                        p.getPeriod().equals(paymentDTO.getPeriod()) && p.getEmployee().equals(paymentDTO.getEmployee())).count() > 1) {
                    throw new ImportSalaryException("Error!");
            } else {
                boolean err = false;
                user.getSalaryList().forEach(payment -> log.info(payment.toString()));
                if (user.getSalaryList().size() > 0) {
                    for (Payment p : user.getSalaryList()) {
                        if (p.getPeriod().equals(paymentDTO.getPeriod()) && user.getEmail().equals(paymentDTO.getEmployee())) {
                            err = true;
                            errorMessage.append((errorMessage.length() == 0) ? "payments[" + payments.indexOf(paymentDTO) + "].period: Wrong date!" : ", payments[" + payments.indexOf(paymentDTO) + "].period: Wrong date!");
                        }
                    }
                }
                if (user.getSalaryList().size() == 0 | !err) {
                    user.addSalary(new Payment(paymentDTO.getPeriod(), paymentDTO.getSalary()));
                    userRepo.save(user);
                }
            }
        }
        if (errorMessage.length() > 0) throw new ImportSalaryException(errorMessage.toString());
        return new ResponseEntity<>(Map.of("status", "Added successfully!"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Map<String, String>> updatePayment(PaymentDTO paymentDTO) {
        User user = getUser(paymentDTO.getEmployee());
        for (Payment payment : user.getSalaryList()) {
            if (payment.getPeriod().equals(paymentDTO.getPeriod())){
                payment.setAmount(paymentDTO.getSalary());
                userRepo.save(user);
                return new ResponseEntity<>(Map.of("status", "Updated successfully!"), HttpStatus.OK);
            }
        }
        throw new RuntimeException("Period not found");
    }

    public ResponseEntity<List<SalaryDTO>> getPayments(String email) {
        List<SalaryDTO> listPayments = new ArrayList<>();
        User user = getUser(email);
        for (Payment p : user.getSalaryList()) {
            LocalDate date = LocalDate.parse("01-" + p.getPeriod(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            listPayments.add(0,new SalaryDTO(
                    user.getName(),
                    user.getLastname(),
                    date.format(DateTimeFormatter.ofPattern("MMMM-yyyy")),
                    String.format("%d dollar(s) %d cent(s)", (int) (double) p.getAmount() / 100, (int) (double) p.getAmount() % 100)
                    )
            );
        }
        return new ResponseEntity<>(listPayments, HttpStatus.OK);
    }

    public ResponseEntity<SalaryDTO> getPayment(String email, String period) {
        User user = getUser(email);
        for (Payment p : user.getSalaryList()) {
            if (p.getPeriod().equals(period)){
                LocalDate date = LocalDate.parse("01-" + p.getPeriod(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                return new ResponseEntity<>(new SalaryDTO(
                        user.getName(),
                        user.getLastname(),
                        date.format(DateTimeFormatter.ofPattern("MMMM-yyyy")),
                        String.format("%d dollar(s) %d cent(s)", (int) (double) p.getAmount() / 100, (int) (double) p.getAmount() % 100)
                ), HttpStatus.OK);
            }
        }
        throw new ImportSalaryException("Not found");
    }

    public User getUser(String email){
        return userRepo.findByEmailIgnoreCase(email).orElseThrow(() -> new UserNotFoundException("User not found!"));
    }
}
