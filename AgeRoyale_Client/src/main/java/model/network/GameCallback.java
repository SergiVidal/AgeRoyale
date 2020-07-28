package model.network;

import model.entity.Game;
import model.entity.UserGame;
import model.entity.User;

/** Representa la interficie GameCallback, esta permite la comunicación entre la clase ServerCommunication y el GameController, una vez se recibe información del servidor */
public interface GameCallback {
    /**
     * Función encargada de obtener la información relacionada con la partida
     * @param game - Objeto Game que contiene todos los valores relacionados con la gestión de la partida
     */
    void getGameInfo(Game game);

    /**
     * Función encargada de notificar que la partida ha finalizado
     * @param game - Objeto Game que contiene todos los valores relacionados con la gestión de la partida
     */
    void onFinishGame(Game game);

    /**
     * Función encargada de obtener las nuevas estadisticas del jugador una vez ha terminado la partida, con una nueva victoria o derrota
     * @param user - Objeto User que contiene los valores del usuario
     */
    void onUpdateStatistics(User user);

    /**
     * Función encargada de obtener los datos de la tropa creada y de actualizar la vista
     * @param userGame - Objeto UserGame que gestiona la información recibida por el servidor
     */
    void onCreateTroop(UserGame userGame);

    /**
     * Función encargada de obtener los datos de la tropa colocada
     * @param userGame - Objeto UserGame que gestiona la información recibida por el servidor
     */
    void onLocateTroop(UserGame userGame);

    /**
     * Función encargada de notificar que el servidor ha caido y de cerrar el proceso del cliente
     */
    void stoppedServer();

    /**
     * Función encargada de obtener continuamente la información actual del juego
     * @param object - Objeto UserGame que gestiona la información recibida por el servidor
     */
    void onRefreshGame(UserGame object);
}
