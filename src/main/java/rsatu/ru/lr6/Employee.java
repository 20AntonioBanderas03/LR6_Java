package rsatu.ru.lr6;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Employee extends Asset {
    private String fullName;
    private String position;
    private LocalDate hireDate;
    private double salary;

    public Employee(String inventoryNumber, String fullName, String position, double salary) {
        super(inventoryNumber);
        this.fullName = fullName;
        this.position = position;
        this.hireDate = LocalDate.now();
        this.salary = salary;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPosition() {
        return position;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    @Override
    public double getCurrentValue() {
        long yearsWorked = ChronoUnit.YEARS.between(hireDate, LocalDate.now());
        return salary * (yearsWorked * 0.1 + 1);
    }

    @Override
    public String getStatusReport() {
        return "Сотрудник: " + fullName + " (" + position + ") - Таб.номер: " + identificationNumber +
                " - З/п: " + String.format("%.2f", getCurrentValue()) + " руб.";
    }
}