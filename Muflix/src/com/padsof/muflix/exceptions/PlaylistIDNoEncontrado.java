package com.padsof.muflix.exceptions;

import com.padsof.muflix.utils.ConstantesGlobales;


/**
 * Clase de excepcion para indicar y poder capturar si una playlist con un ID
 * dado no se encuentra en el sistema.
 * @author Grupo 4 - PADSOF 2261
 *
 */
public class PlaylistIDNoEncontrado extends Exception {
	
	private static final long serialVersionUID = 1L;
	private String playlistID;
	
	/**
	 * Constructor de la excepcion.
	 * @param playlistID ID de la playlist en cuestion.
	 *
	 */
	public PlaylistIDNoEncontrado (String playlistID) {
		this.playlistID =  playlistID;
		
	}
	
	/**
	 * Metodo para mostrar la excepcion como un String.
	 * @return String de la excepcion.
	 *
	 */
	public String toString () {
		return ConstantesGlobales.ERROR_PREFIX + "No se ha podido encontrar una playlist con el ID " + this.playlistID + ".";
	}

}
