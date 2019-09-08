package de.warofgods.kitpvp.teams;

import de.warofgods.kitpvp.God;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Team {

    private static int idCounter;
    int id;
    String name;
    String dispName;

    List<Team> friendly;
    List<God> members;

    public Team(String name, String dispName, Team ... friendly) {
        this.id = ++idCounter;
        this.name = name;
        this.dispName = dispName;
        if (friendly != null) {
            this.friendly = new LinkedList(Arrays.asList(friendly));
        } else {
            this.friendly = new LinkedList<>();
        }
        this.friendly.add(this);
        members = new ArrayList<>();
    }

    public Team addFriendly(Team team) {
        friendly.add(team);
        return this;
    }

    /**
     * removes a team from the friendly list
     * @param name the name of the Team that shall be removed
     * @return this Team, not the Team that was removed!
     */
    public Team removeFriendly(String name) {
        for(int i = 0; i < friendly.size(); i++) {
            if (friendly.get(i).getName().equals(name)) {
                friendly.remove(i);
                return this;
            }
        }
        return this;
    }

    public Team removeFriendly(Team team){
        friendly.remove(team);
        return this;
    }

    public void join(God god) {
        members.add(god);
    }

    public boolean canHit(God member, God other) {
        return members.contains(member) && !isFriendly(other.getTeam());
    }

    private boolean isFriendly(Team t) {
        return friendly.contains(t);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Team) {
            return ((Team) obj).id == id;
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
