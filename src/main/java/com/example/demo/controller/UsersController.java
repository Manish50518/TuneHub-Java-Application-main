package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entities.Users;
import com.example.demo.services.SongsService;
import com.example.demo.services.UsersService;

import jakarta.servlet.http.HttpSession;



@Controller
public class UsersController 
{
	@Autowired
	UsersService userv;

	@Autowired
	SongsService songserv;

	@PostMapping("/register")
	public String addUser(@ModelAttribute Users user) {
		boolean userstatus = userv.emailExists(user.getEmail());
		if(userstatus == false) {
			userv.addUser(user);
			return "registersuccess";
		}
		else
		{
			return "registerfail";
		}
	}

	@PostMapping("/login")
	public String validateUser(@RequestParam String email,
			@RequestParam String password, HttpSession session)
	{
		//invoking validateUser() in service
		if(userv.validateUser(email, password) == true)
		{
			session.setAttribute("email", email);
			//checking whether the user is admin or customer
			if(userv.getRole(email).equals("admin"))
			{
				session.setAttribute("username", userv.getUser("email"));
				return "adminhome";
			}
			else
			{
				return "customerhome";
			}
		}
		else
		{
			return "loginfail";
		}
	}
	
	@GetMapping("/map-account")
	public String acc(Model model, HttpSession session) {
		String email=(String) session.getAttribute("email");
		Users u=userv.getUser(email);
		model.addAttribute("user", u);
		return "profile";
	}
	
	

	@GetMapping("exploreSongs")
	public String exploreSongs(HttpSession session) 
	{
		String email=(String) session.getAttribute("email");
		Users user=userv.getUser(email);

		System.out.println(user);
		boolean userStatus=user.isPremium();

		if(userStatus==true)
		{
			return "customerhome2";
		}
		else {
			return "payment";
		}

	}

	@GetMapping("/deactivate")
	public String deactivateAccount(@RequestParam("email") String email, HttpSession session) {
		// Retrieve email associated with the current session
		String sessionEmail = (String) session.getAttribute("email");

		// Check if session email and entered email match
		if (sessionEmail != null && sessionEmail.equals(email)) {
			userv.deactivateUser(email);
			return "homepage";
		} else {
			return "customerhome2";
		}
	}
	
	
	@GetMapping("/map-logout")
	public String getMethodName(HttpSession session) {
		session.invalidate();
		return "home";
	}
	
	@GetMapping("Admin")
	public String adminhome()
	{
		return "adminhome";
	}
	
	@GetMapping("customer")
	public String customerhome()
	{
		return "customerhome2";
	}

	

	@PostMapping("map-resetpass")
	public String resetPass(@RequestParam String email) {
		 boolean value = userv.emailExists(email);
		 if(value==true)
		 {
			 return "resetpass";
		 }
	     
	     return "forgetpass";
	}
	
	@PostMapping("setpass")
	public String setPass(@RequestParam String email, @RequestParam String password) {
		Users u=userv.getUser(email);
		if(u!=null)
		{
		u.setPassword(password);
		userv.updateUser(u);
		return "home";
		}
		//TODO: process POST request
		
		return "resetpass";
	}
	
}