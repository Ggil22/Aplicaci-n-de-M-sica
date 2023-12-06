package com.padsof.muflix.exceptions;

import com.padsof.muflix.utils.ConstantesGlobales;


/**
 * Clase de excepcion para indicar y poder capturar si un usuario intenta
 * iniciar sesion con una password incorrecta.
 * @author Grupo 4 - PADSOF 2261
 *
 */
public class PasswordIncorrecta extends Exception {
	
	private static final long serialVersionUID = 1L;
	private String usuarioNombre;
	
	
	/**
	 * Constructor de la excepcion.
	 * @param usuarioNombre Nombre del usuario que inicia sesion.
	 *
	 */
	public PasswordIncorrecta (String usuarioNombre) {
		this.usuarioNombre =  usuarioNombre;
		
	}
	
	/**
	 * Metodo para mostrar la excepcion como un String.
	 * @return String de la excepcion.
	 *
	 */
	public String toString () {
		return ConstantesGlobales.ERROR_PREFIX + "Password INCORRECTA para el usuario " + this.usuarioNombre + ".";
	}

}
