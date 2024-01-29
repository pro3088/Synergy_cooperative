package com.synergy.synergy_cooperative.referral;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.synergy.synergy_cooperative.dto.ReferralInfo;
import com.synergy.synergy_cooperative.transaction.TransactionService;
import com.synergy.synergy_cooperative.user.*;
import com.synergy.synergy_cooperative.util.NotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ReferralService {

    @Autowired
    private ReferralRepository referralRepository;

    @Autowired
    UserService userService;

    ModelMapper mapper = new ModelMapper();

    protected static Logger log = LoggerFactory.getLogger(ReferralService.class);

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

    public ReferralInfo getCountByUser(String userId){
        User user = mapper.map(userService.get(userId), User.class);
        return new ReferralInfo(referralRepository.countAllByUser(user));
    }

    public ReferralDTO create(final ReferralDTO referralDTO, String status) {
        log.info("Creating new referral code");
        final Referral referral = new Referral();
        UserStatus userStatus = UserStatus.valueOf(status);
        mapToEntity(referralDTO, referral);
        referral.setId(UUID.randomUUID().toString());
        if (referral.getUsers() != null) {
            UserDTO user = userService.get(referralDTO.getUsers());
            referral.setCode(userStatus.getCode() + "-" + UUID.randomUUID()+ "-" + user.getId().substring(0, 4));
        }
        else{
            referral.setCode(userStatus.getCode()+ "-" + UUID.randomUUID());
        }
        log.info("Created referral code");
        referralRepository.save(referral);

        return mapToDTO(referral, new ReferralDTO());
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
        final User user = referralDTO.getUsers() == null ? null : mapper.map(userService.get(referralDTO.getUsers()), User.class);
        referral.setUsers(user);
    }

    public boolean idExists(final String id) {
        return referralRepository.existsByIdIgnoreCase(id);
    }

    public boolean referralCodeExists(final String referralCode) {
        return referralRepository.existsByCode(referralCode);
    }

}
