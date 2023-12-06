package com.padsof.muflix.media;

import java.time.LocalDate;

import com.padsof.muflix.core.Muflix;
import com.padsof.muflix.exceptions.AlbumIDNoEncontrado;
import com.padsof.muflix.exceptions.CancionIDNoEncontrado;
import com.padsof.muflix.exceptions.MiembroIDNoEncontrado;
import com.padsof.muflix.usuario.Usuario;


/**
 * Clase abstracta para agrupar los atributos y metodos comunes a
 * los diferentes elementos multimedia: Album, Cancion y Playlist.
 * @author Grupo 4 - PADSOF 2261
 *
 */
public abstract class ElementoMultimedia {
	
	protected String id;
	protected String autor_id;
	protected String titulo;
	protected LocalDate fechaCreacion;
	
	/**
	 * Obtener el ID de un Elemento Multimedia.
	 * @return ID del Elemento Multimedia.
	 * 
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * Obtener el ID del autor del Elemento Multimedia.
	 * @return ID del autor del Elemento Multimedia.
	 * 
	 */
	public String getAutor_id() {
		return this.autor_id;
	}
	
	/**
	 * Obtener el titulo del Elemento Multimedia.
	 * @return Titulo del Elemento Multimedia.
	 * 
	 */
	public String getTitulo() {
		return this.titulo;
	}
	
	/**
	 * Obtener la fecha de creacion del Elemento Multimedia.
	 * @return Fecha de creacion del Elemento Multimedia.
	 * 
	 */
	public LocalDate getFechaCreacion() {
		return this.fechaCreacion;
	}
	
	/**
	 * Cambiar el titulo de un Elemento Multimedia.
	 * @param titulo Nuevo titulo para el Elemento Multimedia.
	 * @return true si se ha podido cambiar; false si no se ha podido cambiar.
	 * 
	 */
	public abstract boolean setTitulo(String titulo);
	
	/**
	 * Cambiar la fecha de un Elemento Multimedia.
	 * @param fecha Nueva fecha para el Elemento Multimedia.
	 * 
	 */
	public void setFechaCreacion(LocalDate fecha) {
		this.fechaCreacion = fecha;
	}
	
	/**
	 * Cargar en el reproductor MP3 del sistema el Elemento Multimedia para un usuario concreto.
	 * @param m Usuario para el que se carga el Elemento Multimedia.
	 * @throws CancionIDNoEncontrado si no se encuentra el ID de la cancion en el sistema.
	 * @throws AlbumIDNoEncontrado si no se encuentra el ID del album en el sistema.
	 * @throws MiembroIDNoEncontrado si no se encuentra el ID del miembro en el sistema.
	 * @return true si la carga ha sido exitosa; false si no lo ha sido.
	 * 
	 */
	public abstract boolean cargarMedia(Usuario m) throws CancionIDNoEncontrado, AlbumIDNoEncontrado, MiembroIDNoEncontrado;
	
	/**
	 * Reproducir el Elemento Multimedia cargado en el reproductor MP3 del sistema para un usuario concreto.
	 * @param m Usuario para el que se reproduce el Elemento Multimedia.
	 * @return true si la reproduccion ha sido exitosa; false si no lo ha sido.
	 * 
	 */
	public abstract boolean reproducirMedia(Usuario m);
	
	/**
	 * Para la reproduccion del Elemento Multimedia del reproductor MP3 del sistema.
	 * 
	 */
	public void pausarMedia() {
		Muflix.getInstance().getReproductor().stop();
	}
}
