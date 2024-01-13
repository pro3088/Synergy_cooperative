package com.synergy.synergy_cooperative.bank;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.synergy.synergy_cooperative.util.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class BankService {

    private final BankRepository bankRepository;

    public BankService(final BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

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

    public String create(final BankDTO bankDTO) {
        final Bank bank = new Bank();
        mapToEntity(bankDTO, bank);
        return bankRepository.save(bank).getId();
    }

    public void update(final String id, final BankDTO bankDTO) {
        final Bank bank = bankRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(bankDTO, bank);
        bankRepository.save(bank);
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
