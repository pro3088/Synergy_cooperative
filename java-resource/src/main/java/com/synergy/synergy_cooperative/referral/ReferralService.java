package com.synergy.synergy_cooperative.referral;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.synergy.synergy_cooperative.user.UserStatus;
import com.synergy.synergy_cooperative.user.User;
import com.synergy.synergy_cooperative.user.UserRepository;
import com.synergy.synergy_cooperative.util.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ReferralService {

    private final ReferralRepository referralRepository;
    private final UserRepository usersRepository;

    public ReferralService(final ReferralRepository referralRepository,
                           final UserRepository usersRepository) {
        this.referralRepository = referralRepository;
        this.usersRepository = usersRepository;
    }

    public List<ReferralDTO> findAll() {
        final List<Referral> referrals = referralRepository.findAll(Sort.by("id"));
        return referrals.stream()
                .map(referral -> mapToDTO(referral, new ReferralDTO()))
                .collect(Collectors.toList());
    }

    public ReferralDTO get(final String id) {
        return referralRepository.findById(id)
                .map(referral -> mapToDTO(referral, new ReferralDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public ReferralDTO getByCode(String code){
        return referralRepository.findByCode(code)
                .map(referral -> mapToDTO(referral, new ReferralDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public String create(final ReferralDTO referralDTO, String status) {
        final Referral referral = new Referral();
        UserStatus userStatus = UserStatus.valueOf(status);
        mapToEntity(referralDTO, referral);
        referral.setId(UUID.randomUUID().toString());
        if (referral.getUsers() != null) {
            Optional<User> user = usersRepository.findById(referralDTO.getUsers());
            user.ifPresent(value -> referral.setCode(userStatus.getCode() + UUID.randomUUID() + value.getId().substring(0, 4)));
        }
        else{
            referral.setCode(userStatus.getCode() + UUID.randomUUID());
        }
        return referralRepository.save(referral).getId();
    }

    public void update(final String id, final ReferralDTO referralDTO) {
        final Referral referral = referralRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(referralDTO, referral);
        referralRepository.save(referral);
    }

    public void delete(final String id) {
        referralRepository.deleteById(id);
    }

    private ReferralDTO mapToDTO(final Referral referral, final ReferralDTO referralDTO) {
        referralDTO.setId(referral.getId());
        referralDTO.setCode(referral.getCode());
        referralDTO.setUsed(referral.isUsed());
        referralDTO.setUsers(referral.getUsers() == null ? null : referral.getUsers().getId());
        return referralDTO;
    }

    private void mapToEntity(final ReferralDTO referralDTO, final Referral referral) {
        referral.setCode(referralDTO.getCode());
        referral.setUsed(referralDTO.isUsed());
        final User user = referralDTO.getUsers() == null ? null : usersRepository.findById(referralDTO.getUsers())
                .orElseThrow(() -> new NotFoundException("user not found"));
        referral.setUsers(user);
    }

    public boolean idExists(final String id) {
        return referralRepository.existsByIdIgnoreCase(id);
    }

    public boolean referralCodeExists(final String referralCode) {
        return referralRepository.existsByCode(referralCode);
    }

}
