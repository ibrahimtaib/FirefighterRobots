package Robot;

import Carte.*;
import Simulation.DonneesSimulation;
import Exception.*;

public class Pattes extends Robot {
    static double vitesseDefaut = 30;

    public Pattes(Case position, double vitesse, DonneesSimulation donnees)
            throws VitesseIncorrectException
    {
        super(position, 0, vitesse, donnees);
        this.type = TypeRobot.PATTES;
        if (vitesse < 0) {
            throw new VitesseIncorrectException("La vitesse ne peut pas être négative");
        }
        /* Si la vitesse n'a pas été spécifiée, la mettre par défaut */
        if (Double.isNaN(vitesse)) {
            this.vitesse = vitesseDefaut;
        }
    }

    /**
     * Renvoie la vitesse du {@link Robot} selon le {@link NatureTerrain}.
     * Le parametre nature doit être non null.
     * 
     * @param nature
     * @return double
     */
    public double getVitesse(NatureTerrain nature) {
        switch (nature) {
            case EAU:
                // Ne peut pas se déplacer sur l'eau
                return 0;
            case ROCHE:
                // Vitesse réduite à 10 km/h
                return 10;
            case FORET:
            case TERRAIN_LIBRE:
            case HABITAT:
                return this.vitesse;
            default:
                // this should not happen
                return Double.NaN;
        }

    }

    public boolean peutRemplir() {
        return false;
    }

    public void remplirReservoir() {
        return;
    }

    public void deverserEau(int vol) throws VolumeEauIncorrectException {
        if (vol < 0)
            throw new VolumeEauIncorrectException("Le volume est incorrect");
        Incendie incendie = this.donnees.getIncendie(this.position);
        if (incendie != null)
            incendie.decreaseIntensite(vol);
    }

}