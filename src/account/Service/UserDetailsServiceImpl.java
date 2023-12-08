package account.Service;

import account.ExceptionHandler.UserNotFoundException;
import account.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetailsImpl user = new UserDetailsImpl(userRepo.findByEmailIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("User not found!")));

        if (user.getUsername() == null) throw new UsernameNotFoundException(username + " is not found");

        return user;
    }



}
