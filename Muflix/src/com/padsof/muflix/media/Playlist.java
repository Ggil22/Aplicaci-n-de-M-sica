package com.padsof.muflix.media;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pads.musicPlayer.exceptions.Mp3PlayerException;

import com.padsof.muflix.core.Muflix;
import com.padsof.muflix.exceptions.AlbumIDNoEncontrado;
import com.padsof.muflix.exceptions.CancionIDNoEncontrado;
import com.padsof.muflix.exceptions.MiembroIDNoEncontrado;
import com.padsof.muflix.exceptions.PlaylistIDNoEncontrado;
import com.padsof.muflix.usuario.Administrador;
import com.padsof.muflix.usuario.Anonimo;
import com.padsof.muflix.usuario.Miembro;
import com.padsof.muflix.usuario.Usuario;
import com.padsof.muflix.utils.ConstantesGlobales;
import com.padsof.muflix.utils.FuncionesUtiles;


/**
 * Clase que gestiona los metodos y atributos del objeto Playlist del sistema.
 * Muflix entiende Playlist como un conjunto PRIVADO de canciones y albumes.
 * @author Grupo 4 - PADSOF 2261
 *
 */
public class Playlist extends ElementoMultimedia {
	
	private List<String> contenido;
	
	/**
	 * Constructor del objeto Playlist.
	 * @param id ID de la Playlist.
	 * @param autor ID del Autor de la PlayList.
	 * @param titulo Titulo de la Playlist.
	 * @param fechaCreacion Fecha de creacion de la Playlist. 
	 * 
	 */
	public Playlist(String id, String autor, String titulo, String fechaCreacion) {
		
		this.id = id;
		this.autor_id = autor;
		this.titulo = titulo;
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		this.fechaCreacion = LocalDate.parse(fechaCreacion, formatter);
		
		this.contenido = new ArrayList<String>();
		
	}
	
	/**
	 * Obtener la lista de IDs de los diferentes Elementos Multimedia que forman parte de la Playlist.
	 * @return Lista inmodificable con el contenido de la Playlist.
	 * 
	 */
	public List<String> getContenido() {
		return Collections.unmodifiableList(this.contenido);
	}
	
	/**
	 * Cambiar el titulo de una Playlist.
	 * @param titulo Nuevo titulo para la Playlist.
	 * @return true si se ha podido cambiar; false si no se ha podido cambiar.
	 * 
	 */
	public boolean setTitulo(String titulo) {
		this.titulo = titulo;
		return true;
	}
	
	/**
	 * Agregar un Elemento Multimedia al contenido de la Playlist, comprobando que no exista en ella.
	 * En Muflix, las Playlists son privadas, con lo cual se comprueba que agregar una, esta sea del mismo autor.
	 * @param contenido ID del nuevo Elemento Multimedia a agregar.
	 * @return true si se ha podido agregar; false si no se ha podido agregar.
	 * 
	 */
	public boolean agregarContenido(String contenido) {
		if(contenido.equalsIgnoreCase(this.id)) return false;
		
		if(!this.contenido.contains(contenido)) {
			if(contenido.startsWith("P-")) {
				try {
					if(!Muflix.getInstance().getPlaylistById(contenido).getAutor_id().equals(this.autor_id)) {
						return false; //Playlists privadas.
					}
				} catch (PlaylistIDNoEncontrado e) {
					//e.printStackTrace();
					System.out.println(e);
					return false;
				}
			}
			this.contenido.add(contenido);
			return true;
		}
		return false;
	}
	
	/**
	 * Eliminar un Elemento Multimedia de la Playlist.
	 * @param contenido ID del Elemento Multimedia a eliminar.
	 * @return true si se ha eliminado correctamente; false si no se ha eliminado correctamente.
	 * 
	 */
	public boolean eliminarContenido(String contenido) {
		if(this.contenido.contains(contenido)) {
			this.contenido.remove(contenido);
			return true;
		}
		return false;
	}
	
	/**
	 * Carga el contenido de la Playlist en el reproductor del sistema.
	 * Se llama a diferentes funciones en funcion del tipo de Elemento Multimedia que se vaya a cargar.
	 * @param m Miembro para el que se carga el contenido de la Playlist.
	 * @throws CancionIDNoEncontrado si no se encuentra el ID de alguna cancion en el sistema.
	 * @throws AlbumIDNoEncontrado si no se encuentra el ID del album en el sistema.
	 * @throws MiembroIDNoEncontrado si no se encuentra el ID del miembro en el sistema.
	 * @return true si la carga ha sido exitosa; false si no lo ha sido.
	 * 
	 */
	public boolean cargarMedia(Usuario m) throws CancionIDNoEncontrado, AlbumIDNoEncontrado, MiembroIDNoEncontrado {
		
		if(!(m instanceof Administrador)) {
			try {
				if(!m.getId().equals(Muflix.getInstance().getPlaylistById(this.id).autor_id)) return false;
			} catch (PlaylistIDNoEncontrado e1) {
				//e1.printStackTrace();
				System.out.println(e1);
				return false;
			}
		}
		
		if(m instanceof Anonimo) {
			if(m.getLimiteAlcanzado() == true) return false;
		}
		
		if(m instanceof Miembro) {
			if(((Miembro) m).esPremium() == false && m.getLimiteAlcanzado() == true) return false;
		}
		
		for(int i=0; i<this.contenido.size(); i++){
			if(this.contenido.get(i).startsWith("C-")) {
				if(Muflix.getInstance().getCancionById(this.contenido.get(i)).cargarMedia(m) == false) return false;
			}
			else if(this.contenido.get(i).startsWith("A-")) {
				if(Muflix.getInstance().getAlbumById(this.contenido.get(i)).cargarMedia(m) == false) return false;
			}
			else if(this.contenido.get(i).startsWith("P-")) {
				try {
					Muflix.getInstance().getPlaylistById(this.contenido.get(i)).cargarMedia(m);
				} catch (PlaylistIDNoEncontrado e) {
					//e.printStackTrace();
					System.out.println(e);
					return false;
				}
			}
			
			if(m instanceof Miembro) {
				if(((Miembro) m).isReproduccionAutomatica() == false) return true;
			}
			
			if(m instanceof Anonimo) {
				return true; //No tiene reproduccion automatica.
			}
		}
		return true;
	}
	
	/**
	 * Cargar la Playlist en el reproductor de la aplicacion y reproducirla.
	 * @param m Usuario para el que se reproduce la Playlist.
	 * @return true si la reproduccion ha sido exitosa; false si no lo ha sido.
	 * 
	 */
	public boolean reproducirMedia(Usuario m) {
		
		Muflix.getInstance().limpiarReproductor();
		
		try {
			if(this.cargarMedia(m) == false) return false;
		} catch (CancionIDNoEncontrado e1) {
			//e.printStackTrace();
			System.out.println(e1);
			return false;
		} catch (AlbumIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		} catch (MiembroIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
		try {
			Muflix.getInstance().getReproductor().play();
		} catch (Mp3PlayerException e) {
			//e.printStackTrace();
			System.out.println(ConstantesGlobales.ERROR_PREFIX + "Ha ocurrido un error con el reproductor MP3.");
			return false;
		}
		return true;
	}
	
	/**
	 * Comprueba si dos objetos Playlist son iguales, basandose en su ID. 
	 * @param o Objeto a comparar.
	 * @return true si son iguales; false si no lo son.
	 * 
	 */
	@Override
	public boolean equals(Object o) {
		if(o instanceof Playlist) {
			Playlist cmp = (Playlist) o;
			if(cmp.id.equalsIgnoreCase(this.id)) return true;
		}
		return false;
	}
	
	
	/**
	 * Imprime el objeto Playlist como un String.
	 * @return String de la Playlist. 
	 * 
	 */
	public String toString() {
		String aux = "";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		for(int i=0; i<this.contenido.size(); i++) {
			aux += this.contenido.get(i) + "|";
		}
		
		return this.id + "|" + this.autor_id + "|" + this.titulo + "|" + this.fechaCreacion.format(formatter) + "|" + FuncionesUtiles.removeLastChar(aux);
	}
	
}