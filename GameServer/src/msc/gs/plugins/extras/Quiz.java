package msc.gs.plugins.extras;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Timer;

import msc.gs.Instance;
import msc.gs.model.Player;
import msc.gs.model.World;
import msc.gs.util.Logger;

public class Quiz extends Thread {

    private static final int timeBetweenQuestions = 30; // Seconds.

    static final World world = Instance.getWorld();

    int phaze = 0;

    int question = 0;
    // Question, Option1(A), Option2(B), Option3(C), Option4(D), Correct Option
    private final String[][] Quiz = { { "What is another name for sodium chloride?", "Salt", "Flour", "Sugar", "Oil", "a" }, { "What was Adam Sandler's first film called?", "Airheads", "Coneheads", "Going Overboard", "Happy Gilmore", "c" }, { "In terms of the ancient Roman empire, how was Nero related to Caligula?", "His Uncle", "His Brother", "His Nephew", "His Dad", "b" }, { "What do the Danish call Danish pastries?", "Danish Pastries", "Flatbread", "Viennese Bread", "Alsatian Cakes", "c" }, { "Which of these was once the national drink of Scotland?", "Claret", "Amaretto", "Vodka", "Scotch", "a" }, { "Which of the following was a predecessor of the Milky Way bar?", "Penny Plump", "Fat Emma", "Lardy Larry", "Milky Max", "b" }, { "Which is the deepest loch in Scotland?", "Loch Ness", "Loch Lomand", "Loch Morar", "Loch Ollie", "c" }, { "What was the first of Earth's supercontinents?", "Pangaea", "Gondwanaland", "Asia", "Rodinia", "d" }, { "In which country was the Can-Can invented?", "USA", "UK", "Canada", "England", "b" }, { "1987 saw the first Rugby Union World Cup. Who won it?", "New Zealand", "USA", "Fiji", "Japan", "a" }, { "What will a male lion often do in order to establish his authority when taking over a new pride?", "Kills The Lioness", "Mates and Leaves", "Kills the Cubs", "Hunts with the Pride", "c" }, { "Who is known as the father of modern day printing?", "Aristotle", "Mendel", "Theophrastus", "Gutenberg", "d" }, { "What group had the one hit wonder in the 90s by the name of 'Lovefool'?", "The Cardigans", "Creed", "The Coors", "Chumbawumba", "a" }, { "The color blue is said to represent which of the following emotions?", "Embarrassment", "Sadness", "Envy", "Anger", "b" }, { "Asteroids that are always closer to the Sun than the Earth are called?", "Argon", "Amor", "Aten", "Apollo", "c" }, { "During the Black Death, or Bubonic Plague, in mid 1300's Europe (approx.), what percentage of the population perished?", "One Tenth", "One Quarter", "One Third", "One Half", "c" }, { "What is the capital of Australia?", "Sydney", "Melbourne", "Canberra", "Liverpool", "c" }, { "A brogue is a type of what?", "Hat", "Shoe", "Guitar", "Shirt", "b" }, { "How many Wonders of the Ancient World were there?", "6", "7", "8", "9", "b" }, { "Which African nation did Winston Churchill once call 'the pearl of Africa?'", "Uganda", "Congo", "Chad", "Ethiopia", "a" }, { "What is the atomic number for Neon?", "1", "10", "30", "17", "b" }, { "An animal that eats only meat is called?", "A Canine", "An Omnivore", "A Carnivore", "None of the above", "c" }, { "According to the calendar of the time, when was Sir Isaac Newton born?", "November 4, 1642", "October 1, 1642", "December 25, 1642", "July 4, 1642", "c" }, { "Which fictional character lived in the Hundred Acre Wood?", "Winnie The Pooh", "Bambi", "Snow White", "Snoopy", "a" }, { "Which of the following does NOT Belong?", "Lincoln", "Mercury", "Ford", "Saturn", "d" }, { "Which of the following does NOT Belong?", "Pontiac", "Cadillac", "Hummer", "Mazda", "d" }, { "What does the term 'GUI' stands for?", "Graphics Unused Input", "Graphical User Interface", "Graphing Ultimate Interface", "Graph Unit Input", "b" }, { "In medicine, to examine a body part by listening to it is called what?", "Audiology", "Palpation", "Auscultation", "Radiology", "c" }, { "Of what is Botany the study?", "Plants", "Rocks", "Bugs", "Animals", "a" }, { "What element has a symbol on the periodic table that comes from the Latin word Aurum?", "Platinum", "Gold", "Silver", "Aluminum", "b" }, { "Which one of the following human bones is found in the wrist?", "Cochlea", "Cranium", "Femur", "Capitate", "d" }, { "", "", "", "", "", "" } // Leave
																																																																																																																																																																																																																																																																																																																																																																																																																																																																														      // this
																																																																																																																																																																																																																																																																																																																																																																																																																																																																														      // one
																																																																																																																																																																																																																																																																																																																																																																																																																																																																														      // Blank.
																																																																																																																																																																																																																																																																																																																																																																																																																																																																														      // It
																																																																																																																																																																																																																																																																																																																																																																																																																																																																														      // won't
    // include. (Make sure its the last one)
    };

    void endQuiz() {

	try {
	    // Clean's up the shit, reset's variables etc.

	    world.Quiz = false;
	    world.QuizSignup = false;
	    world.lastAnswer = null;

	    for (Player p : world.getPlayers()) {
		p.lastAnswer = null;
		p.hasAnswered = false;
		p.quizPoints = 0;
	    }
	    Logger.println("Destroying Thread, will create a stack.. ignore it.");
	    Thread.currentThread().destroy();
	} catch (Exception e) {
	    Error(e);
	}
    }

    void Error(Exception e) {
    	e.printStackTrace();
    }

    public void handleAnswer(Player p) throws Exception {

	if (!world.Quiz) {
	    p.getActionSender().sendMessage("Sorry, It's not Quiz time just yet.");
	    return;
	}

	if (p.hasAnswered) {
	    p.getActionSender().sendMessage("You have already answered, please wait for the next question");
	    return;
	}

	p.hasAnswered = true;

	if (p.lastAnswer.equalsIgnoreCase(world.lastAnswer)) {
	    p.quizPoints++;
	}

	p.getActionSender().sendMessage("You have answered @gre@(@whi@" + p.lastAnswer + "@gre@)");

	p.lastAnswer = null;

    }

    /*
     * void removePlayer(Player p) throws Exception {
     * 
     * try {
     * 
     * int index = -1; for(int i=0; i < players.size(); i++) { if
     * (players.get(i).equals(p.getUsername())) index = i;
     * 
     * if(i == -1) { System.out.println("Error removing " + p.getUsername() +
     * " from the Quiz list."); return; } else { System.out.println("[QUIZ] " +
     * players.get(index) + " has left the Quiz"); players.remove(index);
     * p.getActionSender().sendMessage("You have Quit the Quiz Queue"); } }
     * 
     * } catch (Exception e) { Error(e); } }
     */

    /*
     * public void addPlayer(Player p) {
     * 
     * try {
     * 
     * if(!world.QuizSignup) return; if(p == null) return; if(!world.Quiz)
     * return; if(players.contains(p.getUsername())) return;
     * 
     * players.add(p.getUsername());
     * p.getActionSender().sendMessage("You have Joined the Quiz Queue");
     * 
     * } catch (Exception e) { Error(e); } }
     */

    public void run() {

	try {
	    if (world.Quiz)
		return;

	    world.QuizSignup = true;
	    world.Quiz = false;

	    sayAll("@red@[QUIZ]@whi@ Quiz will be starting in @gre@" + timeBetweenQuestions + " @whi@seconds. Write @gre@::quiz@whi@ to join the quiz.", false, false);
	    sayAll("@red@[QUIZ]@whi@ You will have @gre@" + timeBetweenQuestions + "@whi@ seconds to lock in the answers of each quiz.", false, false);
	    sayAll("@red@[QUIZ]@whi@ Quiz results are tally'd up at the end. Use ::a ::b ::c ::d to answer.", false, false);

	    ActionListener phaze1 = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
		    try {

			if (phaze == 0) { // the start of the quiz.
			    phaze++;
			}

			if (phaze == 1) {
			    world.Quiz = true;
			    if (question == 999) {
				tallyQuiz();
				endQuiz();
			    }

			    for (Player p : world.getPlayers())
				p.hasAnswered = false;

			    sayAll("@red@[QUIZ]@gre@ " + question + "/" + (Quiz.length) + "@yel@ " + Quiz[question][0] + "    @cya@(A) @whi@" + Quiz[question][1] + "   @cya@(B)@whi@ " + Quiz[question][2] + "   @cya@(C)@whi@ " + Quiz[question][3] + "   @cya@(D) @whi@ " + Quiz[question][4], true, true);
			    sayAll("@red@[QUIZ]@gre@ " + question + "/" + (Quiz.length) + "@yel@ " + Quiz[question][0] + "    @cya@(A) @whi@" + Quiz[question][1] + "   @cya@(B)@whi@ " + Quiz[question][2] + "   @cya@(C)@whi@ " + Quiz[question][3] + "   @cya@(D) @whi@ " + Quiz[question][4], false, true);
			    world.lastAnswer = Quiz[question][5];
			    question++;
			    if (question == Quiz.length)
				question = 999;
			}
		    }

		    catch (Exception e) {
			Error(e);
		    }
		}
	    };
	    Timer timer = new Timer(timeBetweenQuestions * 1000, phaze1);
	    timer.start();

	} catch (Exception e) {
	    Error(e);
	}
    }

    void sayAll(String message, boolean Alert, boolean inQuiz) throws Exception {
	if (inQuiz) {
	    ArrayList playersToSend = new ArrayList();
	    Player p;
	    for (Iterator i$ = world.getPlayers().iterator(); i$.hasNext(); playersToSend.add(p))
		p = (Player) i$.next();
	    Player pl;
	    for (Iterator i$ = playersToSend.iterator(); i$.hasNext();) {
		pl = (Player) i$.next();
		if (Alert && pl.inQuiz)
		    pl.getActionSender().sendAlert(message, false);
		else if (!Alert && pl.inQuiz)
		    pl.getActionSender().sendMessage(message);
	    }
	} else {
	    ArrayList playersToSend = new ArrayList();
	    Player p;
	    for (Iterator i$ = world.getPlayers().iterator(); i$.hasNext(); playersToSend.add(p))
		p = (Player) i$.next();
	    Player pl;
	    for (Iterator i$ = playersToSend.iterator(); i$.hasNext();) {
		pl = (Player) i$.next();
		if (Alert)
		    pl.getActionSender().sendAlert(message, false);
		else
		    pl.getActionSender().sendMessage(message);
	    }
	}
    }

    // Rofl.. couldent be fucked figuring out another way to do this at the
    // time.
    void tallyQuiz() {
	try {

	    String First = null;
	    int first = -1;
	    int second = -1;
	    int third = -1;
	    String Second = null;
	    String Third = null;
	    int temp = 0;
	    int count = 0;
	    for (Player p : world.getPlayers()) {
		if (p.quizPoints != 0)
		    count++;

		if (p.quizPoints >= temp) {
		    temp = p.quizPoints;
		    First = p.getUsername();
		    first = temp;
		}
	    }
	    temp = 0;
	    for (Player p : world.getPlayers()) {
		if (p.quizPoints >= temp && p.getUsername() != First) {
		    temp = p.quizPoints;
		    Second = p.getUsername();
		    second = temp;
		}
	    }
	    temp = 0;
	    for (Player p : world.getPlayers()) {
		if (p.quizPoints >= temp && (p.getUsername() != First && p.getUsername() != Second)) {
		    temp = p.quizPoints;
		    Third = p.getUsername();
		    third = temp;
		}
	    }
	    sayAll("Thank you all for Playing, the Quiz is over. " + count + " Players submitted Answers.", false, true);
	    sayAll("@yel@Your Quiz Winners Are: @whi@" + First + "@gre@(" + first + ")@whi@, " + Second + "@gre@(" + second + ")@whi@, " + Third + "(@gre@" + third + ")", true, true);
	    sayAll("@yel@Your Quiz Winners Are: @whi@" + First + "@gre@(" + first + ")@whi@, " + Second + "@gre@(" + second + ")@whi@, " + Third + "(@gre@" + third + ")", false, true);

	    /**
	     * 
	     * Add SQL Here.
	     * 
	     */

	    for (Player p : world.getPlayers()) {
		if (p.quizPoints != 0) {
		    int points = p.quizPoints; // Each players total questions
		    // correct from lsat round of
		    // Quiz

		    // Code here for adding to SQL etc.
		}
	    }

	} catch (Exception e) {
	    Error(e);
	}
    }

}
