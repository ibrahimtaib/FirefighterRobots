package Simulation;

import java.awt.Color;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

import Robot.*;
import Strategie.ChefPompier;
import Carte.*;
import gui.GUISimulator;
import gui.Rectangle;
import gui.ImageElement;
import gui.Simulable;
import Events.AffectationIncendiesRobots;
import Events.Evenement;

class ComparatorEvenements implements Comparator<Evenement> {
    public int compare(Evenement o1, Evenement o2) {

        Evenement event1 = (Evenement) o1;
        Evenement event2 = (Evenement) o2;

        return Long.compare(event1.getDate(), event2.getDate());
    }
}

public class Simulateur implements Simulable {
    private long dateSimulation;
    private PriorityQueue<Evenement> scenario;
    private GUISimulator gui;
    private DonneesSimulation donnees;
    private ChefPompier chef;

    public Simulateur(GUISimulator gui, DonneesSimulation donnees, long dateSimulation) {
        this.gui = gui;
        gui.setSimulable(this);
        this.donnees = donnees;
        this.dateSimulation = dateSimulation;
        this.scenario = new PriorityQueue<Evenement>(100, new ComparatorEvenements());
        this.chef = new ChefPompier(this, this.donnees);
        // todo 100?
        this.ajouteEvenement(new AffectationIncendiesRobots(dateSimulation, null, this, 100));
        // Initialisation des couleurs
        this.draw();
    }

    public ChefPompier getChefPompier() {
        return this.chef;
    }

    public long getDateCourante() {
        return this.dateSimulation;
    }

    public void ajouteEvenement(Evenement e) {
        scenario.add(e);
    }

    void incrementeDate() {
        dateSimulation++;
        while (scenario.peek() != null && dateSimulation >= scenario.peek().getDate()) {
            // poll : récupère et supprime la tete de la queue
            scenario.poll().execute();
        }
    }

    // todo
    // return just the condition
    public boolean simulationTerminee() {
        if (scenario.peek() == null) {
            return true;
        }
        return false;
    }

    private Color NatureTerrainToColor(NatureTerrain nature) {
        switch (nature) {
            case EAU:
                return new Color(44, 163, 221);// (212, 241, 249);
            case FORET:
                return new Color(31, 61, 12);
            case HABITAT:
                return new Color(149, 131, 105);
            case ROCHE:
                return new Color(90, 77, 65);
            case TERRAIN_LIBRE:
                return new Color(141, 199, 64);// (144, 238, 144);
            default:
                return Color.BLACK;
        }
    }

    private void drawRobot(Robot robot, int tailleCases, GUISimulator gui) {
        Case caseRobot = robot.getPosition();
        int lig = caseRobot.getLigne();
        int col = caseRobot.getColonne();
        switch (robot.getType()) {
            case DRONE:
                gui.addGraphicalElement(new ImageElement(col * tailleCases, lig * tailleCases, "assets/drone.gif",
                        tailleCases, tailleCases, null));
                break;
            case PATTES:
                gui.addGraphicalElement(new ImageElement(col * tailleCases, lig * tailleCases, "assets/pattes.gif",
                        tailleCases, tailleCases, null));
                break;
            default:
                gui.addGraphicalElement(new ImageElement(col * tailleCases, lig * tailleCases, "assets/robobo.gif",
                        tailleCases, tailleCases, null));
                break;
        }
    }

    private void draw() {
        gui.reset(); // clear window
        Carte carte = donnees.getCarte();
        Case caseCourante = null;
        int tailleCases = carte.getTailleCases();
        Incendie incendie = null;
        NatureTerrain nature = null;
        for (int lig = 0; lig < carte.getNbLignes(); lig++) {
            for (int col = 0; col < carte.getNbColonnes(); col++) {
                caseCourante = carte.getCase(lig, col);
                incendie = donnees.getIncendie(caseCourante);
                nature = caseCourante.getNature();

                switch (nature) {
                    case TERRAIN_LIBRE:
                        // drawTerrainLibre(caseCourante, tailleCases, gui);
                        gui.addGraphicalElement(new ImageElement(col * tailleCases, lig * tailleCases,
                                "assets/grass2.jpg", tailleCases, tailleCases, null));
                        break;
                    case ROCHE:
                        gui.addGraphicalElement(new ImageElement(col * tailleCases, lig * tailleCases,
                                "assets/grass2.jpg", tailleCases, tailleCases, null));
                        gui.addGraphicalElement(new ImageElement(col * tailleCases, lig * tailleCases,
                                "assets/rock.png", tailleCases, tailleCases, null));
                        break;
                    case EAU:
                        gui.addGraphicalElement(new ImageElement(col * tailleCases, lig * tailleCases,
                                "assets/water2.gif", tailleCases, tailleCases, null));
                        break;
                    case HABITAT:
                        gui.addGraphicalElement(new ImageElement(col * tailleCases, lig * tailleCases,
                                "assets/grass2.jpg", tailleCases, tailleCases, null));
                        gui.addGraphicalElement(new ImageElement(col * tailleCases, lig * tailleCases,
                                "assets/habitat2.png", tailleCases, tailleCases, null));
                        break;
                    case FORET:
                        gui.addGraphicalElement(new ImageElement(col * tailleCases, lig * tailleCases,
                                "assets/grass2.jpg", tailleCases, tailleCases, null));
                        gui.addGraphicalElement(new ImageElement(col * tailleCases, lig * tailleCases,
                                "assets/forest.png", tailleCases, tailleCases, null));
                        break;
                    default:
                        gui.addGraphicalElement(new ImageElement(col * tailleCases, lig * tailleCases,
                                "assets/fire2.gif", tailleCases, tailleCases, null));
                        gui.addGraphicalElement(new Rectangle(tailleCases / 2 + col * carte.getTailleCases(),
                                tailleCases / 2 + lig * carte.getTailleCases(),
                                NatureTerrainToColor(caseCourante.getNature()),
                                NatureTerrainToColor(caseCourante.getNature()), carte.getTailleCases()));
                        break;
                }
                if (incendie != null && incendie.getIntensite() > 0) {
                    gui.addGraphicalElement(new ImageElement(col * tailleCases, lig * tailleCases, "assets/fire2.gif",
                            tailleCases, tailleCases, null));
                }
            }
        }

        for (Iterator<Robot> robots = donnees.getRobots(); robots.hasNext();) {
            Robot robot = robots.next();
            drawRobot(robot, tailleCases, gui);
        }
    }

    public DonneesSimulation getDonnees() {
        return this.donnees;
    }

    @Override
    public void next() {
        if (!simulationTerminee())
            incrementeDate();
        draw();
    }

    @Override
    public void restart() {
        draw();
    }
}