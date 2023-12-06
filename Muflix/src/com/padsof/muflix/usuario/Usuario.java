package com.padsof.muflix.usuario;

import com.padsof.muflix.core.Muflix;


/**
 * Clase abstracta para agrupar los atributos y metodos comunes a
 * los diferentes tipos de Usuario que se podran encontrar en la aplicacion.
 * @author Grupo 4 - PADSOF 2261
 *
 */
public abstract class Usuario {
	
	protected String id;
	protected String nombreUsuario;
	protected int numReproducciones;
	protected boolean limiteAlcanzado;
	
	/**
	 * Obtener ID del Usuario.
	 * @return ID del Usuario.
	 *
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * Obtener nombre de usuario del Usuario.
	 * @return Nombre del Usuario.
	 *
	 */
	public String getNombreUsuario() {
		return this.nombreUsuario;
	}
	
	/**
	 * Cambiar nombre de usuario del Usuario.
	 * @param nombre Nuevo nombre de usuario.
	 * @return true si se ha podido cambiar; false si no se ha podido cambiar.
	 *
	 */
	public boolean setNombreUsuario(String nombre) {
		if(Muflix.getInstance().checkNombreUsuario(nombre) == false) {
			this.nombreUsuario = nombre;
			return true;
		}
		return false;
	}
	
	/**
	 * Obtener el numero de reproducciones que el Usuario ha reproducido contenido multimedia.
	 * @return Numero de reproducciones total del Usuario.
	 * 
	 */
	public int getNumReproducciones() {
		return this.numReproducciones;
	}
	
	/**
	 * Obtener el estado de limitacion de reproduccion del Usuario.
	 * @return true si el Usuario ha alcanzado el limite; false si no lo ha alcanzado.
	 * 
	 */
	public boolean getLimiteAlcanzado() {
		return this.limiteAlcanzado;
	}
	
	/**
	 * Cambiar el numero total de reproducciones del Usuario.
	 * @param tiempo Nuevo tiempo de reproduccion.
	 * 
	 */
	public void setNumReproducciones(int num) {
		this.numReproducciones = num;
		if(this.numReproducciones >= Muflix.getInstance().getMaxReproducciones()) {
			this.limiteAlcanzado = true;
		}
	}
	
	/**
	 * Cambiar el estado de limitacion de reproduccion del Usuario.
	 * @param flag Flag que fija en true o false el estado.
	 * 
	 */
	public void setLimiteAlcanzado(boolean flag) {
		this.limiteAlcanzado = flag;
	}
	
	/**
	 * Aumentar el numero de reproducciones del Usuario segun el parametro pasado como argumento.
	 * @param tiempo Numero de reproducciones a aumentar.
	 * 
	 */
	public void aumentarNumReproducciones(int num) {
		this.numReproducciones += num;
		if(this.numReproducciones >= Muflix.getInstance().getMaxReproducciones()) {
			this.limiteAlcanzado = true;
		}
	}
	
	/**
	 * Incrementar en 1 el numero de reproducciones del Usuario segun el parametro pasado como argumento.
	 * 
	 */
	public void incrementarNumReproducciones() {
		this.numReproducciones ++;
		if(this.numReproducciones >= Muflix.getInstance().getMaxReproducciones()) {
			this.limiteAlcanzado = true;
		}
	}
	
	/**
	 * Realizar una busqueda de contenido multimedia o usuarios dentro de la aplicacion.
	 * La busqueda se basa en una clave pasada como argumento.
	 * @param clave Clave utilizada como criterio principal de busqueda.
	 * 
	 */
	public boolean realizarBusqueda(String clave) {
		for(int i=0; i<Muflix.getInstance().getNumCanciones(); i++) {
			if(Muflix.getInstance().getCanciones().get(i).getTitulo().contains(clave)) {
				if(!Muflix.getInstance().getCanciones().get(i).esOculta()) {
					if(Muflix.getInstance().getCanciones().get(i).esExplicita()){
						System.out.println("[EXPLICITA] Canción: " + Muflix.getInstance().getCanciones().get(i).getTitulo());
					} else {
						System.out.println("Canción: " + Muflix.getInstance().getCanciones().get(i).getTitulo());
					}
				}
			}
		}
		
		for(int i=0; i<Muflix.getInstance().getNumAlbumes(); i++) {
			if(Muflix.getInstance().getAlbumes().get(i).getTitulo().contains(clave)) {
				System.out.println("Álbum: " + Muflix.getInstance().getAlbumes().get(i).getTitulo());
			}
		}
		
		for(int i=0; i<Muflix.getInstance().getNumMiembros(); i++) {
			if(Muflix.getInstance().getMiembros().get(i).getNombreUsuario().contains(clave) || Muflix.getInstance().getMiembros().get(i).getNombreCompleto().contains(clave)) {
				System.out.println("Autor: " + Muflix.getInstance().getMiembros().get(i).getNombreUsuario());
			}
		}
		return true;
	}
}
