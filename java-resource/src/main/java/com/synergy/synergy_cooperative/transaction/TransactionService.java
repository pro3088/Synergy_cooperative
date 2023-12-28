package com.synergy.synergy_cooperative.transaction;

import com.synergy.synergy_cooperative.bank.Bank;
import com.synergy.synergy_cooperative.bank.BankRepository;
import com.synergy.synergy_cooperative.user.User;
import com.synergy.synergy_cooperative.user.UserDTO;
import com.synergy.synergy_cooperative.user.UserService;
import com.synergy.synergy_cooperative.util.NotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    UserService userService;
    @Autowired
    BankRepository bankRepository;

    ModelMapper mapper = new ModelMapper();

    public List<TransactionDTO> findAll() {
        final List<Transaction> transactions = transactionRepository.findAll(Sort.by("id"));
        return transactions.stream()
                .map(transaction -> mapToDTO(transaction, new TransactionDTO()))
                .collect(Collectors.toList());
    }

    public BigDecimal getTotalInvestment(){
        List<Transaction> transactions = transactionRepository.findAllByType(Type.INVESTMENT);
        return transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getInvestmentByUser(String userid){
        UserDTO user = userService.get(userid);
        List<Transaction> transactions = transactionRepository.findAllByTypeAndUser(Type.INVESTMENT, mapper.map(user, User.class));
        return transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalLoans(){
        List<Transaction> transactions = transactionRepository.findAllByType(Type.LOAN);
        return transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getLoanByUser(String userid){
        UserDTO user = userService.get(userid);
        List<Transaction> transactions = transactionRepository.findAllByTypeAndUser(Type.LOAN, mapper.map(user, User.class));
        return transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public TransactionDTO get(final String id) {
        return transactionRepository.findById(id)
                .map(transaction -> mapToDTO(transaction, new TransactionDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public int getLoanApplicationCount(int year, int month){
        return transactionRepository.countWithinMonthByType(Type.LOAN, year, month);
    }

    public int getInvestmentApplicationCount(int year, int month){
        return transactionRepository.countWithinMonthByType(Type.INVESTMENT, year, month);
    }

    public String create(final TransactionDTO transactionDTO) {
        final Transaction transaction = new Transaction();
        mapToEntity(transactionDTO, transaction);

        String uuid = UUID.randomUUID().toString();
        transaction.setId(uuid);

        transaction.setStatus(Status.PENDING);
        return transactionRepository.save(transaction).getId();
    }

    public void update(final String id, final TransactionDTO transactionDTO) {
        final Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(transactionDTO, transaction);
        transactionRepository.save(transaction);
    }

    public void delete(final String id) {
        transactionRepository.deleteById(id);
    }

    private TransactionDTO mapToDTO(final Transaction transaction,
            final TransactionDTO transactionDTO) {
        transactionDTO.setId(transaction.getId());
        transactionDTO.setAmount(transaction.getAmount());
        transactionDTO.setStatus(transaction.getStatus());
        transactionDTO.setType(transaction.getType());
        transactionDTO.setReferee(transaction.getReferee());
        transactionDTO.setUser(transaction.getUser() == null ? null : transaction.getUser().getId());
        transactionDTO.setBank(transaction.getBank() == null ? null : transaction.getBank().getId());
        transactionDTO.setDueDate(transaction.getDueDate());
        return transactionDTO;
    }

    private void mapToEntity(final TransactionDTO transactionDTO,
                             final Transaction transaction) {
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setStatus(transactionDTO.getStatus());
        transaction.setType(transactionDTO.getType());
        transaction.setReferee(transactionDTO.getReferee());
        final User user = transactionDTO.getUser() == null ? null : mapper.map(userService.get(transactionDTO.getUser()), User.class);
        transaction.setUser(user);
        final Bank bank = transactionDTO.getBank() == null ? null : bankRepository.findById(transactionDTO.getBank())
                .orElseThrow(() -> new NotFoundException("bank not found"));
        transaction.setBank(bank);
        transaction.setDueDate(transactionDTO.getDueDate());
    }

    public boolean bankExists(final String id) {
        return transactionRepository.existsByBankId(id);
    }

}
