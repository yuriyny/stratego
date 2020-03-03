package caramel.stratego.user.controller;
import caramel.stratego.user.service.UserDomainService;
import caramel.stratego.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;


@Controller
@CrossOrigin
public class UserController {

    @Autowired
    UserDomainService userDomainService;

    @Autowired
    public UserController(UserDomainService userDomainService) {
        this.userDomainService = userDomainService;
    }

    @PostMapping(path="/register")
    public String addNewUser(@ModelAttribute("user") @Valid User newUser, BindingResult bindingResult, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            redirect.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
            redirect.addFlashAttribute("user", newUser);
            redirect.addFlashAttribute("pageType", "register");
            return "redirect:login";
        }

        if(userDomainService.userExists(newUser)) {
            return "redirect:login?usernameError";
        } else {
            userDomainService.saveUser(newUser);
            return "login";
        }
    }

    @GetMapping(path={"/login", "/logout"})
    public String getLoginPage() {
        //calling test class to populate tables
        //uncomment this in order to test it
        //****u have to uncomment this like in order to get access to test class
//        TestClass testClass = new TestClass(UserController.this);
//        //****populates table with data
//        testClass.populateTables();
//        //**testing queries, need to put correct id's for every table in order to test it
//        testClass.queryCalls();
//        //creates hash map for specific board from database
//        testClass.getboardMap();
//        System.out.println(testClass);
        return "login";
    }

    @PostMapping(path="/login")
    public String loginUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, RedirectAttributes redirect) {
        //This checks if login input is valid. If correct, bring to game, else stay on login
        if (bindingResult.hasErrors()) {
            redirect.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
            redirect.addFlashAttribute("user", user);
            redirect.addFlashAttribute("pageType", "login");
            return "redirect:login";
        }
        redirect.addFlashAttribute("userId", user.getUsername());
        return "redirect:game";
    }
}
