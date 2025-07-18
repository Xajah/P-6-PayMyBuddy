package com.paymybuddy.paymybuddy.service;

import com.paymybuddy.paymybuddy.model.Transaction;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserService userService;


    public Optional<Transaction> getTransactionById(int id) {
        return transactionRepository.findById(id);
    }

    public Iterable<Transaction> getTransactionsByUserId(int userId) {
        return transactionRepository.findAllByFromUser_Id(userId);
    }

    @Transactional
    public Optional<Transaction> addTransaction(Transaction t) {
        Optional<User> fromUserOpt = userService.getUserById(t.getFromUser().getId());
        Optional<User> toUserOpt = userService.getUserById(t.getToUser().getId());

        if (fromUserOpt.isEmpty() || toUserOpt.isEmpty()) {
            return Optional.empty();
        }
        if (t.getAmount() == null || t.getAmount().doubleValue() <= 0) {
            return Optional.empty();
        }
        User fromUser = fromUserOpt.get();
        if (fromUser.getSolde().compareTo(t.getAmount()) < 0) {
            return Optional.empty();
        }
        User toUser = toUserOpt.get();

        fromUser.subtractToSolde(t.getAmount());
        toUser.addToSolde(t.getAmount());
        userService.updateUser(fromUser);
        userService.updateUser(toUser);

        t.setFromUser(fromUser);
        t.setToUser(toUser);
        t.setId(null);
        return Optional.of(transactionRepository.save(t));
    }


}
