package pl.cyfronet.ltos.security;

import java.util.Collection;
import java.util.LinkedList;

import lombok.Getter;
import lombok.Setter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import pl.cyfronet.ltos.bean.UserAuth;
import pl.cyfronet.ltos.security.policy.Identity;

public class PortalUser extends User implements Identity {

    private static final long serialVersionUID = 1L;

    @Setter
    @Getter
    private UserAuth userAuth;

    public PortalUser(UserAuth details) {
        super(details.getLogin(), details.getPassword(), getAuthorities(details));
        this.userAuth = details;
    }

    @Override
    public Long getId() {
        return userAuth.getUser().getId();
    }
    
    static Collection<GrantedAuthority> getAuthorities(UserAuth details) {
        LinkedList<GrantedAuthority> list = new LinkedList<GrantedAuthority>();
        list.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (details.isAdmin()) {
            list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return list;
    }

}