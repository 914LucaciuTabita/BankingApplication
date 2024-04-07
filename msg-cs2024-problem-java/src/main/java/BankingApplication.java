import domain.CurrencyType;
import domain.MoneyModel;
import domain.TransactionModel;
import exceptions.AccountNotFoundException;
import exceptions.TransferNotAllowedException;
import seed.SeedInitializer;
import services.SavingsManagerService;
import services.TransactionManagerService;

import static seed.AccountsSeedData.*;

public class BankingApplication {

    public static void main(String[] args) {
        System.out.println("[SYSTEM] Initialize Application \n");
        SeedInitializer.seedData();
        System.out.println("[SYSTEM] Running Application \n\n");

        // TRANSACTION MANAGER FUNCTIONALITY

        TransactionManagerService transactionManagerServiceInstance = new TransactionManagerService();
        SavingsManagerService savingsManagerServiceInstance = new SavingsManagerService();

        System.out.println("[Transaction Manager] 1. " + transactionManagerServiceInstance.checkFunds(savingsAccountA.getId()));
        System.out.println("[Transaction Manager] 2. " + transactionManagerServiceInstance.checkFunds(checkingAccountB.getId()));

        try {
            TransactionModel transaction1 = transactionManagerServiceInstance.transfer(
                    savingsAccountA.getId(),
                    checkingAccountB.getId(),
                    new MoneyModel(-600, CurrencyType.EUR)
            );
            System.out.println("[Transaction Manager] 3. " + transaction1);
        } catch (RuntimeException exception) {
            System.out.println("[Transaction Manager] 3. FAILED: " + exception.getMessage());
        }

        System.out.println("[Transaction Manager] 4. " + transactionManagerServiceInstance.checkFunds(savingsAccountA.getId()));
        System.out.println("[Transaction Manager] 5. " + transactionManagerServiceInstance.checkFunds(checkingAccountB.getId()));

        System.out.println("[Transaction Manager] 6. " + transactionManagerServiceInstance.checkFunds(checkingAccountC.getId()));

        // Uncomment the following lines if the withdrawal method is available in the TransactionManagerService
        try {
            System.out.println("[Transaction Manager] 7. " +
                    transactionManagerServiceInstance.withdraw(
                            checkingAccountC.getId(),
                            new MoneyModel(50000, CurrencyType.EUR)
                    )
            );
        } catch (RuntimeException exception) {
            System.out.println("[Transaction Manager] 7. FAILED: " + exception.getMessage());
        }

        System.out.println("[Transaction Manager] 8. " + transactionManagerServiceInstance.checkFunds(checkingAccountC.getId()));

        System.out.println("\n------------------------------------\n");

        // SAVINGS MANAGER FUNCTIONALITY

        System.out.println("[Saving Manager] 1. " + transactionManagerServiceInstance.checkFunds(savingsAccountA.getId()));

        savingsManagerServiceInstance.passTime();
        System.out.println("[Saving Manager] 2. " + transactionManagerServiceInstance.checkFunds(savingsAccountA.getId()));

        savingsManagerServiceInstance.passTime();
        System.out.println("[Saving Manager] 3. " + transactionManagerServiceInstance.checkFunds(savingsAccountA.getId()));
        System.out.println("[Saving Manager] 4. " + transactionManagerServiceInstance.checkFunds(savingsAccountB.getId()));
        System.out.println("[Saving Manager] 5. " + transactionManagerServiceInstance.checkFunds(checkingAccountA.getId()));

        System.out.println("\n[SYSTEM] Application closed\n");
    }
}