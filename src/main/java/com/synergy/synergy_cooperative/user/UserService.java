package com.synergy.synergy_cooperative.user;

import com.synergy.synergy_cooperative.authorization.pojo.AuthRequest;
import com.synergy.synergy_cooperative.authorization.pojo.UserInfoDetails;
import com.synergy.synergy_cooperative.dto.UserInfo;
import com.synergy.synergy_cooperative.referral.ReferralDTO;
import com.synergy.synergy_cooperative.referral.ReferralService;
import com.synergy.synergy_cooperative.util.NotFoundException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import groovy.transform.Undefined;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository usersRepository;
    @Autowired
    ReferralService referralService;
    @Autowired
    private PasswordEncoder encoder;
    protected static Logger log = LoggerFactory.getLogger(UserResource.class);

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

    public boolean userExists(final String address) {
        return usersRepository.findByEmailAddress(address).isPresent();
    }

    public UserDTO create(final UserDTO userDTO) {
        log.info("Creating a new user with name {}", userDTO.getFirstName());
        final User user = new User();
        mapToEntity(userDTO, user);

        user.setId(UUID.randomUUID().toString());
        user.setPassword(encoder.encode(user.getPassword()));

        log.info("Verifying user info");
        Optional.of(user)
                .filter(u -> !userExists(userDTO.getEmailAddress()))
                .orElseThrow(() -> new RuntimeException("Email has been used. Log in to your account."));

        log.info("Verifying referral code");
        ReferralDTO referraldto = Optional.of(referralService.getByCode(user.getReferralCode()))
                .filter(dto -> !dto.isUsed())
                .orElseThrow(() -> new RuntimeException("Code not valid"));

        referraldto.setUsed(true);

        user.setStatus(UserStatus.getByCode(user.getReferralCode().substring(0,3)));

        String roles = (user.getStatus().toString().equals("ADMIN")) ? "ROLE_ADMIN,ROLE_USER" : "ROLE_USER";

        user.setRoles(roles);

        referralService.update(referraldto.getId(), referraldto);
        usersRepository.save(user);
        log.info("User has been created");
        return mapToDTO(user, new UserDTO());
    }

    public UserDTO update(final String id, final UserDTO userDTO) {
        final User user = usersRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(userDTO, user);
        return mapToDTO(usersRepository.save(user), new UserDTO());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws NotFoundException {
        log.info("Getting user by email - {}", email);
        Optional<User> userDetail = usersRepository.findByEmailAddress(email);
        if (userDetail.isPresent()) {
            log.info("User is found");
            User user = userDetail.get();
            return new UserInfoDetails(user);
        }
        log.error("User not found");
        throw new UsernameNotFoundException("User not found");
    }

    public UserDTO validateUser(final AuthRequest authRequest){
        log.info("validating user");
        Optional<User> user = usersRepository.findByEmailAddress(authRequest.getUsername());
        if (user.isPresent()) {
            log.info("user has been validated");
            User response = new User();
            response.setId(user.get().getId());
            response.setStatus(user.get().getStatus());
            response.setDateCreated(user.get().getDateCreated());
            response.setFirstName(user.get().getFirstName());
            response.setLastName(user.get().getLastName());
            response.setEmailAddress(user.get().getEmailAddress());
            return mapToDTO(response, new UserDTO());
        }
        log.warn("User is not available");
        return null;
    }

    public UserInfo getCountByStatus(String status){
        UserStatus userStatus = UserStatus.valueOf(status);
        return new UserInfo(usersRepository.countAllByStatus(userStatus));
    }

    public void delete(final String id) {
        usersRepository.deleteById(id);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setPassword(user.getPassword());
        String code = (user.getReferralCode() != null) ? hashString(user.getReferralCode()) : null;
        userDTO.setReferralCode(code);
        userDTO.setStatus(user.getStatus());
        userDTO.setEmailAddress(user.getEmailAddress());
        userDTO.setRoles(user.getRoles());
        LocalDate date = (user.getDateCreated() != null) ? user.getDateCreated().toLocalDate() : null;
        userDTO.setDateJoined(date);
        return userDTO;
    }

    private void mapToEntity(final UserDTO userDTO, final User user) {
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPassword(userDTO.getPassword());
        user.setReferralCode(userDTO.getReferralCode());
        user.setStatus(userDTO.getStatus());
        user.setEmailAddress(userDTO.getEmailAddress());
        user.setRoles(userDTO.getRoles());
    }

    private String hashString(String value){
        return DigestUtils.sha256Hex(value);
    }

    private String hashPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
