package com.padsof.muflix.usuario;

import com.padsof.muflix.core.Muflix;


/**
 * Clase abstracta para agrupar los atributos y metodos comunes a
 * los diferentes tipos de usuario Registrado que se podran encontrar en la aplicacion.
 * @author Grupo 4 - PADSOF 2261
 *
 */
public abstract class Registrado extends Usuario {
	
	protected String email;
	protected String password;
	private boolean reproduccionAutomatica;
	
	/**
	 * Obtener el email del usuario Registrado.
	 * @return Email del usuario Registrado. 
	 * 
	 */
	public String getEmail() {
		return this.email;
	}
	
	/**
	 * Obtener la password del usuario Registrado.
	 * @return Password del usuario Registrado. 
	 * 
	 */
	public String getPassword() {
		return this.password;
	}
	
	/**
	 * Cambiar la password del usuario Registrado.
	 * @param password Nueva password para el usuario Registrado. 
	 * 
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Obtener el estado de activacion de la reproduccion automatica para el usuario Registrado.
	 * @return true si tiene activada la reproduccion automatica, false si no la tiene activada.
	 * 
	 */
	public boolean isReproduccionAutomatica() {
		return this.reproduccionAutomatica;
	}
	
	/**
	 * Activar la reproduccion automatica para el usuario Registrado.
	 * 
	 */
	public void activarReproduccionAutomatica() {
		if(this instanceof Miembro) {
			if(((Miembro) this).esPremium()) {
				this.reproduccionAutomatica = true;
			}
		} else {
			this.reproduccionAutomatica = true;
		}
	}
	
	/**
	 * Desactivar la reproduccion automatica para el usuario Registrado.
	 * 
	 */
	public void desactivarReproduccionAutomatica() {
		this.reproduccionAutomatica = false;
	}
	
	/**
	 * Cerrar la sesion del usuario Registrado actual.
	 * Se establece el usuario actual de la aplicacion como un nuevo anonimo.
	 * Se lleva a cabo un guardado de todos los datos en la aplicacion.
	 * 
	 */
	public void hacerLogOut() {
		Muflix.getInstance().setUsuarioActual(new Anonimo("INV"));
		//Muflix.getInstance().guardarDatos();
	}
	
}
