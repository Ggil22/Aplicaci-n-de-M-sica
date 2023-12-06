package com.padsof.muflix.exceptions;

import com.padsof.muflix.utils.ConstantesGlobales;


/**
 * Clase de excepcion para indicar y poder capturar si un album con un ID
 * dado no se encuentra en el sistema.
 * @author Grupo 4 - PADSOF 2261
 *
 */
public class AlbumIDNoEncontrado extends Exception {
	
	private static final long serialVersionUID = 1L;
	private String albumID;
	
	/**
	 * Constructor de la excepcion.
	 * @param albumID ID del album en cuestion.
	 *
	 */
	public AlbumIDNoEncontrado (String albumID) {
		this.albumID =  albumID;
		
	}
	
	/**
	 * Metodo para mostrar la excepcion como un String.
	 * @return String de la excepcion.
	 *
	 */
	public String toString () {
		return ConstantesGlobales.ERROR_PREFIX + "No se ha podido encontrar un album con el ID " + this.albumID + ".";
	}

}
