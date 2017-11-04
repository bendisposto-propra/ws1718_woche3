package de.hhu.stups.propra.gruppen.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ConcurrentModificationException;

@Entity
public class Teilnehmer {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    private String github;
    private String realname;
    private String login;
    private String matrikelnummer;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		if (this.id != null) {
			throw new ConcurrentModificationException("Cannot change the id once it has been set");
		}
		this.id = id;
	}
	public String getGithub() {
		return github;
	}
	public void setGithub(String github) {
		this.github = github;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getMatrikelnummer() {
		return matrikelnummer;
	}
	public void setMatrikelnummer(String matrikelnummer) {
		this.matrikelnummer = matrikelnummer;
	}



}
