package com.fyers.fyerstrading.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/ui")
public class AuthController {

	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}

	@PostMapping("/login")
	public String doLogin(@RequestParam String username, @RequestParam String password, HttpSession session) {
		// SIMPLE AUTH (replace with real auth later)
		if ("admin".equals(username) && "admin123".equals(password)) {
			session.setAttribute("USER", username);
			return "redirect:/dashboard";
		}
		return "redirect:/login?error=true";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}
}
