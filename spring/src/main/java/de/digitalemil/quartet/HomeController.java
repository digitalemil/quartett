package de.digitalemil.quartet;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

	@GetMapping("/home")
	public String home(@RequestParam(name="name", required=false, defaultValue="Quartet") String name, Model model) {
		model.addAttribute("name", name);
		return "home";
	}

}