package com.esc.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.ArrayList;

@Controller
public class UserController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ApplicationUserRepository ApplicationUserRepo;

    // Serves the home page (which is also the login page)
    @GetMapping(value="/")
    public ModelAndView serveHomePage(Principal p) {
        // It seems like you've left in a comment here about a one time change?
        // In general, try to prefer putting things like this in a commit message.
        // DRL: -> Changed mapping to return ModelAndView, redirected logged in users to their profile.
        if ( p != null && ((UsernamePasswordAuthenticationToken) p).isAuthenticated()) {
            return new ModelAndView("redirect:/profile");
        }
        return new ModelAndView("index");
    }

    // Serves the signup page
    @GetMapping(value="/signup")
    public ModelAndView serveSignUpPage(Principal p) {
        if ( p != null && ((UsernamePasswordAuthenticationToken) p).isAuthenticated()) {
            // DRL: -> Changed mapping to return ModelAndView, redirected logged in users to their profile.
            return new ModelAndView("redirect:/profile");
        }
        return new ModelAndView("signup");
    }

    // Creates a new user, logs the new user in and redirects the user to their profile page
    @PostMapping(value="/signup")
    public RedirectView createNewUser(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam String phoneNumber) {

        // Create a new user and saves the new user to the database
        ApplicationUser newUser = new ApplicationUser(username, bCryptPasswordEncoder.encode(password), phoneNumber);
        ApplicationUserRepo.save(newUser);


        // Logs new users in immediately after creating an account
        Authentication authentication = new UsernamePasswordAuthenticationToken(newUser, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new RedirectView("/profile");
    }

    // Serves the user's profile page
    @GetMapping(value="/profile")
    public String serveProfilePage(Model m, Principal p) {
        ApplicationUser user = (ApplicationUser)((UsernamePasswordAuthenticationToken) p).getPrincipal();
        m.addAttribute("messages", user.messages);
        System.out.println(user.messages);
        return "profile";
    }

    @GetMapping(value="/about")
    public String serveAboutUsPage() {
        return "about";
    }
}
