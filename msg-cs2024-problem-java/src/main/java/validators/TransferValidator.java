package validators;

import domain.AccountModel;
import domain.CheckingAccountModel;
import domain.CurrencyType;
import domain.SavingsAccountModel;

import static domain.CurrencyType.EUR;
import static domain.CurrencyType.RON;

public class TransferValidator {
    public static boolean isTransferAllowed(AccountModel fromAccount, AccountModel toAccount) {
        // Returns FALSE for transfers between the following types of accounts:
        //    Savings Accounts => Checking Accounts
        //    Savings Accounts => Savings Accounts
        // Otherwise returns TRUE
        if (fromAccount instanceof SavingsAccountModel && toAccount instanceof CheckingAccountModel){
            return false;
        } else if (fromAccount instanceof SavingsAccountModel && toAccount instanceof SavingsAccountModel) {
            return false;
        }

        return true;
    }

}
