package com.synergy.synergy_cooperative.user;

import com.synergy.synergy_cooperative.referral.ReferralDTO;
import com.synergy.synergy_cooperative.referral.ReferralService;
import com.synergy.synergy_cooperative.util.NotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Autowired
    UserRepository usersRepository;
    @Autowired
    ReferralService referralService;

    public List<UserDTO> findAll() {
        final List<User> users = usersRepository.findAll(Sort.by("id"));
        return users.stream()
                .map(user -> mapToDTO(user, new UserDTO()))
                .collect(Collectors.toList());
    }

    public List<UserStatus> getAllEnums(){
        return Arrays.asList(UserStatus.values());
    }

    public UserDTO get(final String id) {
        return usersRepository.findById(id)
                .map(users -> mapToDTO(users, new UserDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public String create(final UserDTO userDTO) {
        final User user = new User();
        mapToEntity(userDTO, user);

        user.setId(UUID.randomUUID().toString());
        user.setPassword(hashPassword(user.getPassword()));

        ReferralDTO referraldto = referralService.getByCode(user.getReferralCode());
        referraldto.setUsed(true);

        user.setStatus(UserStatus.getByCode(user.getReferralCode().substring(0,2)));

        referralService.update(referraldto.getId(), referraldto);
        return usersRepository.save(user).getId();
    }

    public void update(final String id, final UserDTO userDTO) {
        final User user = usersRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(userDTO, user);
        usersRepository.save(user);
    }

    public void delete(final String id) {
        usersRepository.deleteById(id);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setPassword(user.getPassword());
        userDTO.setReferralCode(hashString(user.getReferralCode()));
        userDTO.setStatus(user.getStatus());
        return userDTO;
    }

    private void mapToEntity(final UserDTO userDTO, final User user) {
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPassword(userDTO.getPassword());
        user.setReferralCode(userDTO.getReferralCode());
        user.setStatus(userDTO.getStatus());
    }

    private String hashString(String value){
        return DigestUtils.sha256Hex(value);
    }

    private String hashPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean verifyPassword(String password, String hashedPassword){
        return BCrypt.checkpw(password, hashedPassword);
    }

}
