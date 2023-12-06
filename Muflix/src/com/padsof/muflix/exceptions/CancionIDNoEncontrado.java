package com.padsof.muflix.exceptions;

import com.padsof.muflix.utils.ConstantesGlobales;


/**
 * Clase de excepcion para indicar y poder capturar si una cancion con un ID
 * dado no se encuentra en el sistema.
 * @author Grupo 4 - PADSOF 2261
 *
 */
public class CancionIDNoEncontrado extends Exception {
	
	private static final long serialVersionUID = 1L;
	private String cancionID;
	
	/**
	 * Constructor de la excepcion.
	 * @param cancionID ID de la cancion en cuestion.
	 *
	 */
	public CancionIDNoEncontrado (String cancionID) {
		this.cancionID =  cancionID;
		
	}
	
	/**
	 * Metodo para mostrar la excepcion como un String.
	 * @return String de la excepcion.
	 *
	 */
	public String toString () {
		return ConstantesGlobales.ERROR_PREFIX + "No se ha podido encontrar una cancion con el ID " + this.cancionID + ".";
	}

}
