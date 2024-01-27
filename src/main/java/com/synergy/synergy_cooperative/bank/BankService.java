package com.synergy.synergy_cooperative.bank;

import java.util.List;
import java.util.stream.Collectors;

import com.synergy.synergy_cooperative.bank.interest.InterestDTO;
import com.synergy.synergy_cooperative.bank.interest.InterestService;
import com.synergy.synergy_cooperative.util.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BankService {

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private InterestService interestService;

    ModelMapper mapper = new ModelMapper();

    public List<BankDTO> findAll() {
        final List<Bank> banks = bankRepository.findAll(Sort.by("id"));
        return banks.stream()
                .map(bank -> mapToDTO(bank, new BankDTO()))
                .collect(Collectors.toList());
    }

    public BankDTO get(final String id) {
        return bankRepository.findById(id)
                .map(bank -> mapToDTO(bank, new BankDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public BankDTO create(final BankDTO bankDTO) {
        final Bank bank = new Bank();
        mapToEntity(bankDTO, bank);
        bankRepository.save(bank);
        return bankDTO;
    }

    public BankDTO update(final String id, final BankDTO bankDTO) {
        final Bank bank = bankRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(bankDTO, bank);
        bankRepository.save(bank);
        return bankDTO;
    }

    public void delete(final String id) {
        bankRepository.deleteById(id);
    }

    private BankDTO mapToDTO(final Bank bank, final BankDTO bankDTO) {
        bankDTO.setId(bank.getId());
        bankDTO.setName(bank.getName());
        bankDTO.setAccountNumber(bank.getAccountNumber());
        bankDTO.setAccountName(bank.getAccountName());
        return bankDTO;
    }

    private Bank mapToEntity(final BankDTO bankDTO, final Bank bank) {
        bank.setName(bankDTO.getName());
        bank.setAccountNumber(bankDTO.getAccountNumber());
        bank.setAccountName(bankDTO.getAccountName());
        return bank;
    }

    public boolean accountNumberExists(final Integer accountNumber) {
        return bankRepository.existsByAccountNumber(accountNumber);
    }

}
