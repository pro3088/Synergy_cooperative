package com.synergy.synergy_cooperative.transaction.shares;

import com.synergy.synergy_cooperative.dto.TransactionInfo;
import com.synergy.synergy_cooperative.transaction.TransactionService;
import com.synergy.synergy_cooperative.transaction.enums.Type;
import com.synergy.synergy_cooperative.user.User;
import com.synergy.synergy_cooperative.user.UserDTO;
import com.synergy.synergy_cooperative.user.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class ShareService {

    @Autowired
    ShareRepository repository;

    @Autowired
    TransactionService transactionService;

    @Autowired
    UserService userService;

    protected static Logger log = LoggerFactory.getLogger(ShareService.class);

    ModelMapper mapper = new ModelMapper();

    public void addShare(ShareDTO shareDTO){
        Share share = new Share();
        mapToEntity(shareDTO, share);
        share.setId(UUID.randomUUID().toString());
        repository.save(share);
    }

    public TransactionInfo getTotalEarning(String userId){
        UserDTO user = userService.get(userId);
        List<Share> transactions = repository.findAllByUser(mapper.map(user, User.class));

        return new TransactionInfo(transactions.stream()
                .map(Share::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    public void calculateShares(BigDecimal loan){
        List<UserDTO> users = userService.findAll();
        users.forEach(userDTO -> {
            String id = userDTO.getId();
            String type = Type.INVESTMENT.toString();
            TransactionInfo transactionsCountByUser = transactionService.getTransactionCountByUser(id, type);
            if (transactionsCountByUser.getCount() > 1){
                BigDecimal amount = transactionService.getAmountByUserAndType(id, type).getAmount();
                BigDecimal total = transactionService.getTotalByType(type).getAmount();
                BigDecimal earning = (amount.divide(total, RoundingMode.HALF_EVEN).multiply(loan));
                addShare(new ShareDTO(UUID.randomUUID().toString(), id, earning));
            }
        });
    }

    private void mapToEntity(ShareDTO shareDTO, Share share){
        share.setId(shareDTO.getId());
        final User user = shareDTO.getUser() == null ? null : mapper.map(userService.get(shareDTO.getUser()), User.class);
        share.setUser(user);
        share.setAmount(shareDTO.getAmount());
    }
}
