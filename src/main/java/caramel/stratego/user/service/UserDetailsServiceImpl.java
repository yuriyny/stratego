// TEMPLATE FOR IMPLEMENTING CUSTOM DaoAuthenticationProvider in WebSecurityConfig
// more info
//https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/authentication/dao/DaoAuthenticationProvider.html
//
// package caramel.stratego.configuration;
//
//import caramel.stratego.user.Role;
//import caramel.stratego.user.User;
//import caramel.stratego.user.storage.RoleRepository;
//import caramel.stratego.user.storage.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.HashSet;
//
//@Service
//@DependsOn({"PASSWORD_ENCODER"})
//public class UserDetailsServiceImpl implements UserDetailsService{
//
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private RoleRepository roleRepository;
//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;
//
//    public void save(User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        user.set_roles(new HashSet<Role>());
//        userRepository.save(user);
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
//
//        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getDetails();
//        if (userDetails instanceof UserDetails) {
//            return (UserDetails) userDetails;
//        }
//
//        return null;
//    }
//}