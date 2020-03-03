package caramel.stratego.user.service;

import caramel.stratego.user.User;
import caramel.stratego.user.storage.RoleRepository;
import caramel.stratego.user.storage.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserDomainService { //removed id because when you put in credentials for login, they don't know your id .

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    public UserDomainService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean userExists (User user){
        return userRepository.findById(user.getUsername()).isPresent();  // if username is present, true
    }
    public User saveUser(User user){
        user.setPassword((new BCryptPasswordEncoder()).encode(user.getPassword()));
        user.setEnabled(true);
        userRepository.save(user);
        return user;
    }


}
