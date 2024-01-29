package com.synergy.synergy_cooperative.transaction;

import com.synergy.synergy_cooperative.bank.Bank;
import com.synergy.synergy_cooperative.bank.BankDTO;
import com.synergy.synergy_cooperative.bank.BankRepository;
import com.synergy.synergy_cooperative.bank.BankService;
import com.synergy.synergy_cooperative.bank.interest.InterestService;
import com.synergy.synergy_cooperative.dto.TransactionInfo;
import com.synergy.synergy_cooperative.transaction.shares.ShareService;
import com.synergy.synergy_cooperative.transaction.enums.Applications;
import com.synergy.synergy_cooperative.transaction.enums.Currency;
import com.synergy.synergy_cooperative.transaction.enums.Status;
import com.synergy.synergy_cooperative.transaction.enums.Type;
import com.synergy.synergy_cooperative.user.User;
import com.synergy.synergy_cooperative.user.UserDTO;
import com.synergy.synergy_cooperative.user.UserService;
import com.synergy.synergy_cooperative.util.NotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    UserService userService;
    @Autowired
    BankService bankService;

    @Autowired
    InterestService interestService;

    @Autowired
    ShareService shareService;

    protected static Logger log = LoggerFactory.getLogger(TransactionService.class);

    ModelMapper mapper = new ModelMapper();

    public List<TransactionDTO> findAll() {
        final List<Transaction> transactions = transactionRepository.findAll(Sort.by("id"));
        return transactions.stream()
                .map(transaction -> {
                    TransactionDTO transactionDTO = mapToDTO(transaction, new TransactionDTO());
                    UserDTO userDTO = userService.get(transactionDTO.getUser());
                    transactionDTO.setUser(userDTO.getFirstName() + " " + userDTO.getLastName());
                    return transactionDTO;
                })
                .collect(Collectors.toList());
    }

    public Applications getApplicationsByUser(Integer offset, Integer limit, String userId, int pageSize) {
        Pageable pageable = PageRequest.of(offset == null ? 0 : offset, limit);
        User user = mapper.map(userService.get(userId), User.class);
        int total = transactionRepository.countAllByUser(user)/pageSize;
        total = Math.max(total, 1);
        Page<Transaction> all = transactionRepository.findAllByUser(user, pageable);
        List<TransactionDTO> transactions = all.stream()
                .map(transaction -> {
                    TransactionDTO transactionDTO = mapToDTO(transaction, new TransactionDTO());
                    UserDTO userDTO = userService.get(transactionDTO.getUser());
                    transactionDTO.setUser(userDTO.getFirstName() + " " + userDTO.getLastName());
                    return transactionDTO;
                })
                .collect(Collectors.toList());
        return new Applications(transactions, total);
    }

    public TransactionInfo getTotalByType(String type){
        log.info("Getting total sum by type: {}", type);
        List<Transaction> transactions = transactionRepository.findAllByType(Type.valueOf(type));
        log.info("Gotten total sum");
        return new TransactionInfo(transactions.stream()
                .filter(transaction -> Status.APPROVED.equals(transaction.getStatus()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    public TransactionInfo getAmountByUserAndType(String userid, String type){
        log.info("Getting amount for user {} of type {}", userid, type);
        UserDTO user = userService.get(userid);
        List<Transaction> transactions = transactionRepository.findAllByTypeAndUser(Type.valueOf(type), mapper.map(user, User.class));
        log.info("Gotten amount for user");
        return new TransactionInfo(transactions.stream()
                .filter(transaction -> Status.APPROVED.equals(transaction.getStatus()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    public TransactionInfo getDepositByUser(String userid){
        log.info("Getting total deposit to be made by {}", userid);
        UserDTO user = userService.get(userid);
        List<Transaction> transactions = transactionRepository.findAllByTypeAndUser(Type.LOAN, mapper.map(user, User.class));
        BigDecimal amount = transactions.stream()
                .filter(transaction -> Status.PENDING.equals(transaction.getStatus()))
                .map(Transaction::getDeposit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("Gotten total deposit");
        return new TransactionInfo(amount);
    }

    public TransactionInfo getTransactionCountByUser(String userId, String type){
        UserDTO user = userService.get(userId);
        Type trans = Type.valueOf(type);
        return new TransactionInfo(transactionRepository.countAllByTypeAndUser(trans, mapper.map(user, User.class)));
    }

    public TransactionInfo getDepositStatusByUser(String userId) {
        UserDTO user = userService.get(userId);
        List<Transaction> transactions = transactionRepository.findAllByTypeAndUser(Type.LOAN, mapper.map(user, User.class));

        return new TransactionInfo(transactions.stream()
                .anyMatch(transaction -> transaction.getStatus() == Status.PENDING)
                ? Status.PENDING
                : Status.COMPLETED);
    }

    public TransactionDTO get(final String id) {
        TransactionDTO transactionDTO = transactionRepository.findById(id)
                .map(transaction -> mapToDTO(transaction, new TransactionDTO()))
                .orElseThrow(NotFoundException::new);
        BankDTO bankDTO = bankService.get(transactionDTO.getBank());
        transactionDTO.setAccountName(bankDTO.getAccountName());
        transactionDTO.setAccountNumber(bankDTO.getAccountNumber());
        transactionDTO.setBankName(bankDTO.getName());
        return transactionDTO;
    }

    public TransactionDTO create(final TransactionDTO transactionDTO) {
        final Transaction transaction = new Transaction();
        mapToEntity(transactionDTO, transaction);

        String uuid = UUID.randomUUID().toString();

        transaction.setId(uuid);
        transaction.setCurrency(Currency.NAIRA);
        transaction.setNarration(transaction.getId().substring(0,8));
        transaction.setStatus(Status.PENDING);
        transaction.setDeposit(transaction.getAmount()
                .multiply(BigDecimal.valueOf(interestService.getInterest().getInterest()))
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_EVEN));

        return mapToDTO(transactionRepository.save(transaction), new TransactionDTO());
    }

    public TransactionDTO update(final String id, final TransactionDTO transactionDTO) {
        log.info("Updating Transaction with id {}", id);
        final Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        transaction.setStatus(transactionDTO.getStatus());
        if (transactionDTO.getStatus() == Status.APPROVED && transaction.getType() == Type.LOAN)
            shareService.calculateShares(transaction.getAmount());
        if (transactionDTO.getDate() != null)
            transaction.setDueDate(transactionDTO.getDate());
        log.info("Transaction has been updated");
        transactionRepository.save(transaction);
        return mapToDTO(transaction, new TransactionDTO());
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
        transactionDTO.setDeposit(transaction.getDeposit());
        transactionDTO.setUser(transaction.getUser() == null ? null : transaction.getUser().getId());
        transactionDTO.setBank(transaction.getBank() == null ? null : transaction.getBank().getId());
        LocalDate date = (transaction.getDueDate() != null) ? transaction.getDueDate().toLocalDate() : null;
        transactionDTO.setDueDate(date);
        transactionDTO.setNarration(transaction.getNarration());
        return transactionDTO;
    }

    private void mapToEntity(final TransactionDTO transactionDTO,
                             final Transaction transaction) {
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setStatus(transactionDTO.getStatus());
        transaction.setType(transactionDTO.getType());
        final BigDecimal payableAmount = transactionDTO.getDeposit() == null ? new BigDecimal("0.00") : transactionDTO.getDeposit();
        transaction.setDeposit(payableAmount);
        final User user = transactionDTO.getUser() == null ? null : mapper.map(userService.get(transactionDTO.getUser()), User.class);
        transaction.setUser(user);
        final Bank bank = transactionDTO.getBank() == null ? null : mapper.map(bankService.get(transactionDTO.getBank()), Bank.class);
        transaction.setBank(bank);
        transaction.setDueDate((LocalDateTime) transactionDTO.getDate());
    }

    public boolean bankExists(final String id) {
        return transactionRepository.existsByBankId(id);
    }

}
