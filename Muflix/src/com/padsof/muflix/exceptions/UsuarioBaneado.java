package com.padsof.muflix.exceptions;

import com.padsof.muflix.core.Muflix;
import com.padsof.muflix.utils.ConstantesGlobales;


/**
 * Clase de excepcion para indicar y poder capturar si un usuario intenta
 * iniciar sesion con un nombre de un miembro que se encuentra baneado.
 * @author Grupo 4 - PADSOF 2261
 *
 */
public class UsuarioBaneado extends Exception {
	
	private static final long serialVersionUID = 1L;
	private String usuarioID;
	private String usuarioNombre;
	
	/**
	 * Constructor de la excepcion.
	 * @param usuarioID ID del usuario que inicia sesion.
	 * @throws MiembroIDNoEncontrado si el miembro no se encuentra registrado en el sistema.
	 *
	 */
	public UsuarioBaneado (String usuarioID) throws MiembroIDNoEncontrado {
		this.usuarioID = usuarioID;
		this.usuarioNombre =  Muflix.getInstance().getMiembroById(usuarioID).getNombreUsuario();
		
	}
	
	/**
	 * Metodo para mostrar la excepcion como un String.
	 * @return String de la excepcion.
	 *
	 */
	public String toString () {
		return ConstantesGlobales.ERROR_PREFIX + "El usuario " + this.usuarioNombre + " (" + this.usuarioID + ") se encuentra baneado PERMANENTEMENTE de Muflix.";
	}

}
