package com.padsof.muflix.exceptions;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.padsof.muflix.core.Muflix;
import com.padsof.muflix.utils.ConstantesGlobales;


/**
 * Clase de excepcion para indicar y poder capturar si un usuario intenta
 * iniciar sesion con un nombre de un miembro que se encuentra bloqueado.
 * @author Grupo 4 - PADSOF 2261
 *
 */
public class UsuarioBloqueado extends Exception {
	
	private static final long serialVersionUID = 1L;
	private String usuarioID;
	private String usuarioNombre;
	private long expira;
	
	/**
	 * Constructor de la excepcion.
	 * @param usuarioID ID del usuario que inicia sesion.
	 * @throws MiembroIDNoEncontrado si el miembro no se encuentra registrado en el sistema.
	 *
	 */
	public UsuarioBloqueado(String usuarioID) throws MiembroIDNoEncontrado {
		this.usuarioID = usuarioID;
		this.usuarioNombre =  Muflix.getInstance().getMiembroById(usuarioID).getNombreUsuario();
		this.expira = ChronoUnit.DAYS.between(LocalDate.now(), Muflix.getInstance().getMiembroById(usuarioID).getFechaBloqueo().plusDays(Muflix.getInstance().getDiasBloqueoTemporal()));
		
		
	}
	
	/**
	 * Metodo para mostrar la excepcion como un String.
	 * @return String de la excepcion.
	 *
	 */
	public String toString () {
		return ConstantesGlobales.ERROR_PREFIX + "El usuario " + this.usuarioNombre + " (" + this.usuarioID + ") se encuentra bloqueado TEMPORALMENTE de Muflix. Expira en: " + this.expira + " dias.";
	}

}
