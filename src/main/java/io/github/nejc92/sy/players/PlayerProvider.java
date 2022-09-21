package io.github.nejc92.sy.players;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import io.github.nejc92.sy.strategies.CoalitionReduction;
import io.github.nejc92.sy.strategies.MoveFiltering;
import io.github.nejc92.sy.strategies.Playouts;

public class PlayerProvider {
    private static final String DEFAULT_HIDER_NAME = "Mr. X";
    private Stack<Player.Color> availableColors;
    private Set<String> usedNames;
    private List<Player> players;
    private Playouts.Uses playouts;
    private CoalitionReduction.Uses coalitionReduction;
    private MoveFiltering.Uses moveFiltering;

    public PlayerProvider() {
        usedNames = new HashSet<String>();
        players = new ArrayList<Player>();
        availableColors = new Stack<Player.Color>();
        for (Player.Color color : Player.Color.values()) {
            availableColors.add(color);
        }
        Collections.shuffle(availableColors);
    }

    public void addPlayer(Player.Type type, Player.Operator operator) throws Exception {
        addPlayer(type, operator, "");
    }

    public void addPlayer(Player.Type type, Player.Operator operator, String preferredName) throws Exception {
        String name;
        if (preferredName == null || preferredName.trim().length() <= 0) {
            if (type == Player.Type.HIDER) {
                name = DEFAULT_HIDER_NAME;
            } else {
                name = availableColors.pop().name();
            }
        } else {
            name = preferredName;
        }
        if (!usedNames.add(name)) {
            throw new Exception(String.format("Player with name '%s' already exists!", name));
        }
        switch (type) {
            case HIDER:
                players.add(new Hider(operator, name, playouts, coalitionReduction, moveFiltering));
                break;
            case SEEKER:
                players.add(new Seeker(operator, name, playouts, coalitionReduction, moveFiltering));
                break;
        }
    }

    public PlayerProvider setPlayouts(Playouts.Uses playouts) {
        this.playouts = playouts;
        return this;
    }

    public PlayerProvider setCoalitionReduction(CoalitionReduction.Uses coalitionReduction) {
        this.coalitionReduction = coalitionReduction;
        return this;
    }

    public PlayerProvider setMoveFiltering(MoveFiltering.Uses moveFiltering) {
        this.moveFiltering = moveFiltering;
        return this;
    }

    public Player[] initializePlayers() {
        return players.toArray(new Player[0]);
    }
}
