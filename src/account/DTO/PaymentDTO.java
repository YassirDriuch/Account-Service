package account.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class PaymentDTO {


    @Email
    private String employee;
    @NotEmpty
    @Pattern(regexp = "(0[1-9]|1[0-2])-20[0-3][0-9]")
    private String period;

    @Min(0)
    private Long salary;

    public PaymentDTO() {
    }

    public PaymentDTO(String email, String period, Long salary) {
        this.employee = email;
        this.period = period;
        this.salary = salary;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String email) {
        this.employee = email;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "PaymentDTO{" +
                "employee='" + employee + '\'' +
                ", period='" + period + '\'' +
                ", salary=" + salary +
                '}';
    }
}
