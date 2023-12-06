package com.padsof.muflix.utils;

/**
 * Clase auxiliar para almacenar y acceder a las constantes globales de la aplicacion.
 * @author Grupo 4 - PADSOF 2261
 *
 */
public class ConstantesGlobales {
	
	/**
	 * Constructor privado para no crear instancias de ConstantesGlobales.
	 * 
	 */
    private ConstantesGlobales () {} // No crear instancias
    
    //Directorio principal de guardado de datos.
    public static final String DATA_DIRECTORY = "data/";
    //Subdirectorio de guardado de archivos MP3.
    public static final String SONGS_DIRECTORY = "canciones/";
    
    //Ficheros TXT de bases de datos para cada objeto de la aplicacion.
    public static final String MEMBER_DB = "db_miembros.txt";
    public static final String SONG_DB = "db_canciones.txt";
    public static final String ALBUM_DB = "db_albumes.txt";
    public static final String PLAYLIST_DB = "db_playlists.txt";
    public static final String FOLLOW_DB = "db_follows.txt";
    public static final String REPORT_DB = "db_denuncias.txt";
    
    //Directorio principal de guardado de la configuracion del sistema.
    public static final String CONFIG_DIRECTORY = "config/";
    //Fichero de guardado de la configuracion del sistema.
    public static final String CONFIG_FILE = "config.txt";
    
    //Maximo de dias que una cancion puede estar en estado de validacion.
    public static final int VALIDATION_DAYS = 3;
    
    //Maximo de dias para buscar la actividad reciente de los miembros a los que sigues.
    public static final int ACTIVITY_DAYS = 3;
    
    //Cifra usada para identificar fechas nulas.
    public static final int NULL_DATE_YEAR = 9999;
    
    //Prefijo de error para mensajes y excepciones.
    public static final String ERROR_PREFIX = "[ERROR] ";
}