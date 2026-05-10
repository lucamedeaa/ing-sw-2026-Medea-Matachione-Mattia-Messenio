package it.polimi.ingsw.client.model;

/**
 * Holds local client identity data that is shared across UI states.
 */
public class ClientSession {
    private String nickname = "";

    /**
     * Returns the nickname currently associated with this client.
     *
     * @return client nickname, or an empty string before matchmaking
     */
    public String getNickname() { return nickname; }

    /**
     * Updates the nickname associated with this client session.
     *
     * @param nickname nickname selected by the user
     */
    public void setNickname(String nickname) { this.nickname = nickname; }
}