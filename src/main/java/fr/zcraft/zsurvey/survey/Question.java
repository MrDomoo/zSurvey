package fr.zcraft.zsurvey.survey;

import java.util.ArrayList;
import org.bukkit.entity.Player;

import fr.zcraft.zlib.components.i18n.I;
import fr.zcraft.zsurvey.Config;
import fr.zcraft.zsurvey.zSurveyException;

public class Question {

	private String question;
	private ArrayList<Answer> answers;
	
	public Question(String question) {
		this.question = question;
		this.answers = new ArrayList<Answer>();
	}
	
	  /***********/
	 /* SETTERS */
	/***********/
	
	public void setProposition(String question) {
		this.question = question;	
	}
	
	  /***********/
	 /* GETTERS */
	/***********/
	
	public String getProposition() {
		return this.question;
	}

	public ArrayList<Answer> getAnswers() {
		return this.answers;
	}
	
	  /************/
	 /* METHODES */
	/************/
	
	public void addAnswer(Player sender, String answer) {
		if(this.answers.size() >= Config.MAX_ANSWERS.get())		//Si le nombre max de reponses est atteint
			throw new zSurveyException(zSurveyException.Reason.MAX_ANSWERS);	//Exception
		
		answers.add(new Answer(answer));
	}
	
	public void vote(Player sender, int answer_number) {
		if(answer_number < 1 || this.answers.size() < answer_number)			//Si le numero de reponse n'existe pas
			throw new zSurveyException(zSurveyException.Reason.UNKNOW_ANSWER);	//Exception
		
		for(Answer answer:this.answers) {			//Pour chaque reponse
			if(answer.getVoters().contains(sender))	//Si la personne a d�j� vot�
				answer.cancelvote(sender);			//Annule le vote
		}
			
		this.answers.get(answer_number-1).vote(sender);	//Vote pour l'autre reponse
	}
	
	public void removeAnswer(Player sender, int answer_number) {
		if(answer_number < 1 || this.answers.size() < answer_number)			//Si le numero de reponse n'existe pas
			throw new zSurveyException(zSurveyException.Reason.UNKNOW_ANSWER);	//Exception
		
		this.answers.remove(answer_number-1);	//Supprime la reponse
	}
	
	public void resetVotes() {
		for(Answer answer:this.answers)
			answer.getVoters().clear();
	}
	
	public void resetVotes(Player player) {
		for(Answer answer:this.answers)
			answer.getVoters().remove(player);
	}
	
	private float statAnswer(int answer_number) {
		int total_voters = 0;
		
		for(Answer answer:this.answers)
			total_voters+= answer.getVoters().size();
	
		return this.answers.get(answer_number).getVoters().size()*100/total_voters;
	}
	
	  /************/
	 /* TOSTRING */
	/************/
	
	public String toString(Player sender, boolean result) {
		
		String message = I.t("{darkgreen}{0}", this.question);
				
		for (int i = 0; i < answers.size(); i++) {
			message += I.t("\n  {gray}{0} {gray}- {1}",Character.toString((char)('A' + i)), answers.get(i).toString(sender));
			
			if(result == true)
				message += I.t(" {gray}({0}%)", statAnswer(i));
		}
			
		return message;
	}
}