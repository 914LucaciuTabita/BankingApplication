package services;

import domain.*;
import exceptions.AccountNotFoundException;
import exceptions.InsufficientFundsException;
import exceptions.TransferNotAllowedException;
import repository.AccountsRepository;
import utils.MoneyUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static domain.CurrencyType.EUR;
import static domain.CurrencyType.RON;
import static validators.TransferValidator.isTransferAllowed;

public class TransactionManagerService {

    public TransactionModel transfer(String fromAccountId, String toAccountId, MoneyModel value) {
        AccountModel fromAccount = AccountsRepository.INSTANCE.get(fromAccountId);
        AccountModel toAccount = AccountsRepository.INSTANCE.get(toAccountId);

        if (fromAccount == null || toAccount == null) {
            throw new AccountNotFoundException("Specified account does not exist");
        }

        // Check the types of the accounts and if the transaction between them is allowed or not
        if (!isTransferAllowed(fromAccount, toAccount)) {
            throw new TransferNotAllowedException("Transfer not allowed between these account types");
        }

        if (value.getAmount() <= 0) {
            throw new IllegalArgumentException("The amount must be greater than 0");
        }

        // A transaction final amount must be in the same currency as the target account (currency conversion).
        // Ensure that value currency and fromAccount currency are the same as for the toAccount currency
        MoneyModel convertedValue = MoneyUtils.convert(value, toAccount.getBalance().getCurrency());
        MoneyModel convertedFromAccountValue = MoneyUtils.convert(fromAccount.getBalance(), toAccount.getBalance().getCurrency());


        // Check if there's sufficient money
        if (convertedFromAccountValue.getAmount() < convertedValue.getAmount()) {
            throw new RuntimeException("There's not enough money for this transaction");
        }

        TransactionModel transaction = new TransactionModel(
                UUID.randomUUID(),
                fromAccountId,
                toAccountId,
                value,
                LocalDate.now()
        );

        // check if the currency of fromAccount is different before conversion
        if (fromAccount.getBalance().getCurrency() != convertedFromAccountValue.getCurrency()) {
            // check if initial currencies of fromAccount and value are the same
            if (fromAccount.getBalance().getCurrency() == value.getCurrency()){
                fromAccount.getBalance().setAmount(fromAccount.getBalance().getAmount() - value.getAmount());
            } else if (fromAccount.getBalance().getCurrency() != value.getCurrency()) {
                MoneyModel fromAccountCurrencyValue = MoneyUtils.convert(value, fromAccount.getBalance().getCurrency());
                fromAccount.getBalance().setAmount(fromAccount.getBalance().getAmount() - fromAccountCurrencyValue.getAmount());
            }
        // if the currency of fromAccount is the same before conversion, it means that the currency of toAccount is the same as the currency of fromAccount
        } else {
            fromAccount.getBalance().setAmount(convertedFromAccountValue.getAmount() - convertedValue.getAmount());
        }
        fromAccount.getTransactions().add(transaction);

        toAccount.getBalance().setAmount(toAccount.getBalance().getAmount() + convertedValue.getAmount());
        toAccount.getTransactions().add(transaction);

        return transaction;
    }

    public TransactionModel withdraw(String accountId, MoneyModel amount) {
        AccountModel account = AccountsRepository.INSTANCE.get(accountId);

        if (account == null) {
            throw new AccountNotFoundException("Specified account does not exist");
        }

        // Ensure that amount currency and account currency are the same
        MoneyModel convertedAmount = MoneyUtils.convert(amount, account.getBalance().getCurrency());

        // Check if there's sufficient money for withdrawal
        double withdrawalAmount = convertedAmount.getAmount();
        double accountBalance = account.getBalance().getAmount();

        if (withdrawalAmount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than 0");
        }

        if (withdrawalAmount > accountBalance) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal");
        }

        TransactionModel transaction = new TransactionModel(
                UUID.randomUUID(),
                accountId,
                accountId,
                amount,
                LocalDate.now()
        );

        account.getBalance().setAmount(accountBalance - withdrawalAmount);
        account.getTransactions().add(transaction);

        return transaction;
    }

    public MoneyModel checkFunds(String accountId) {
        if (!AccountsRepository.INSTANCE.exist(accountId)) {
            throw new RuntimeException("Specified account does not exist");
        }
        return AccountsRepository.INSTANCE.get(accountId).getBalance();
    }

    public List<TransactionModel> retrieveTransactions(String accountId) {
        if (!AccountsRepository.INSTANCE.exist(accountId)) {
            throw new RuntimeException("Specified account does not exist");
        }
        return new ArrayList<>(AccountsRepository.INSTANCE.get(accountId).getTransactions());
    }
}

