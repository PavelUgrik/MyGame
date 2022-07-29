package ru.laurelin.game.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.laurelin.game.models.user.Role;
import ru.laurelin.game.models.user.User;
import ru.laurelin.game.repositories.UserRepository;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public Boolean createUser(User user) {
        User userFromDb = userRepository.findByUsername(user.getUsername());
        if (userFromDb != null)
            return false;
        else {
            Random random = new Random();
            user.setIsActive(true);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Collections.singleton(Role.USER));
            user.setDexterity(random.nextInt(5) + 3);
            user.setStrength(random.nextInt(5) + 3);
            user.setIntelligence(random.nextInt(5) + 3);
            user.setStamina(random.nextInt(5) + 3);
            user.setLastHit(LocalTime.now());
            refreshUser(user);
            return true;
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAllByOrderByIdAsc();
    }

    public User getUserById(Long id) {
        return userRepository.getReferenceById(id);
    }

    public boolean updateUserInfo(User user, Long id) {
        User userFromDB = getUserById(id);
        if (user != null) {
            userFromDB.setUsername(user.getUsername());
            if (!user.getPassword().isEmpty()) {
                userFromDB.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            userFromDB.setRoles(user.getRoles());

            userRepository.save(userFromDB);
            return true;
        }
        return false;
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public void refreshUser(User user) {
        long duration = 10L;
        int maxHp = user.getStamina() * 10;

        if (user.getHp() == null) {
            user.setHp(maxHp);
        } else {
            int hp = user.getHp();
            if (hp < maxHp) {
                if (user.getLastHit() == null) {
                    user.setLastHit(LocalTime.now());
                }
                long diff = ChronoUnit.SECONDS.between(user.getLastHit(), LocalTime.now());
                long ratio = diff / duration;
                if (ratio > 0) {
                    int modHp = (int) Math.min(ratio, maxHp - hp);
                    user.setHp(hp + modHp);
                    user.setLastHit(LocalTime.now());
//                    user.setLastHit(user.getLastHit().plusSeconds(duration * ratio));
                }
            }
        }
        user.setMaxHp(maxHp);
        userRepository.save(user);
    }

    public void updateUser(User user) {
        User userFromDb = userRepository.findByUsername(user.getUsername());
        if (userFromDb == null) {
            throw new UsernameNotFoundException("User not found");
        } else {
            userFromDb.setCurrentCombat(user.getCurrentCombat());
            userRepository.save(userFromDb);
        }
    }
}
