package it.polimi.ingsw.client;

public class ClientMain {

    public static void main(String[] args) {

        /* l'idea di base è che questo coso deve chiedere solo se vuoi mettere GUI o TUI. Poi si
        apre la GUI/TUI e scegli se usare socket o RMI. A quel punto usando networkClientFactory, vi darà
        un virtualServer. Questo è un virtualServer generico, voi vi dovete preoccupare soltanto di usare il metodo send d'ora in
        poi del tipo di messaggio che volete inviare sia per socket che per rmi su quel virtual server.
        Per questa fase inziale, ho creato i messaggi GetAvailableGameMessage, che vi da la lista di games disponibili
        JoinGameMessage che dandogli l' id e un nickname vi aggiunge al game e CreateGameMessage, che crea un game con numero di giocatori che gli dite voi.
        Per ogni errore in questa fase mando un semplice ErrorMessageDTO, che contiene una stringa con la spiegazione del motivo
        . Non penso che serva molto di più che visualizzare una stringa, sia nella GUI che nella TUI,
        per questo ne ho fatto uno unico per tutti e non ho fatto classi per ogni errore.
        In generale penso che per i messaggi di Errore basti fare un DTO così e scriverci il messagio, anche dopo ma ditemi cosa ne pensate (in modo da fare un unico visit).
        Se ho un successo dopo creazione o entrata ricevo un MatchMakingSuccessMessage, che significa che o ho creato la partita e sono entrato (anche qua una stringa mi dirà quale
        delle due, è una semplice stringa) o AvailableGamesResponseMessage, che semplicemente mi dice gli id disponibili.
         Ora ho scritto dei visit stupidi nel ClientMessageReceiver, ma ovviamente
        bisogna trovare un modo di delegare alla view (deve essere un modo coerente con le scelte fatte fino ad ora)
        il visit di questi. Una volta fatto questo inizia il game, quindi si può inizare a usare la vostra logica con il lightgamemodel.
        DAJE RAGA!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        TODO miei: aggiungere possibilità di uscire da una lobby e relativi messaggi. Gestire disconnessioni anche involontarie in fase di lobby, probabilmente facendo un ping ogni tot
        TODO (capisci come farlo bene). Mettere un timer per le mosse del player quando è il proprio turno. Creare eventuali eccezioni custom + messaggi di errore per eccezioni
        TODO nel controller. Riguardare e sistemare codice
         */
    }
}