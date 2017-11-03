package de.hhu.stups.propra.gruppen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import de.hhu.stups.propra.gruppen.model.Teilnehmer;
import de.hhu.stups.propra.gruppen.repository.TeilnehmerRepository;

@Controller
@RequestMapping(path = "/")
public class TeilnehmerController {

	@Autowired
	private TeilnehmerRepository teilnehmer;

	@GetMapping(path = "/add")
	public RedirectView addNewUser(@RequestParam String github, @RequestParam String login,
			@RequestParam String realname, @RequestParam String matrikelnummer) {
		Teilnehmer n = new Teilnehmer();
		n.setGithub(github);
		n.setLogin(login);
		n.setRealname(realname);
		n.setMatrikelnummer(matrikelnummer);
		teilnehmer.save(n);
		return new RedirectView("/"); // redirect after adding the entry
	}


	@GetMapping(path = "/addform")
	public String addForm(Model model) {
		model.addAttribute("teilnehmerliste", teilnehmer.findAll());
		return "addform"; // use the template named addform.html
	}


	@GetMapping(path = "/")
	public String getAllUsers(Model model) {
		// the model Object is automatically injected by springframework
		// it will be passed to the templating engine
		model.addAttribute("teilnehmerliste", teilnehmer.findAll());
		return "teilnehmerlist"; // use the template named teilnehmerlist.html
	}
}
