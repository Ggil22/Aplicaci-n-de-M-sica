package com.padsof.muflix.exceptions;

import com.padsof.muflix.utils.ConstantesGlobales;


/**
 * Clase de excepcion para indicar y poder capturar si un miembro con un ID
 * dado no se encuentra en el sistema.
 * @author Grupo 4 - PADSOF 2261
 *
 */
public class MiembroIDNoEncontrado extends Exception {
	
	private static final long serialVersionUID = 1L;
	private String miembroID;
	
	
	/**
	 * Constructor de la excepcion.
	 * @param miembroID ID del usuario en cuestion.
	 *
	 */
	public MiembroIDNoEncontrado (String miembroID) {
		this.miembroID =  miembroID;
		
	}
	
	/**
	 * Metodo para mostrar la excepcion como un String.
	 * @return String de la excepcion.
	 *
	 */
	public String toString () {
		return ConstantesGlobales.ERROR_PREFIX + "No se ha podido encontrar un miembro con el ID " + this.miembroID + ".";
	}

}
