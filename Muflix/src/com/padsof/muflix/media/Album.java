package com.padsof.muflix.media;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.padsof.muflix.core.Muflix;
import com.padsof.muflix.exceptions.CancionIDNoEncontrado;
import com.padsof.muflix.exceptions.MiembroIDNoEncontrado;
import com.padsof.muflix.usuario.Administrador;
import com.padsof.muflix.usuario.Anonimo;
import com.padsof.muflix.usuario.Miembro;
import com.padsof.muflix.usuario.Usuario;
import com.padsof.muflix.utils.ConstantesGlobales;
import com.padsof.muflix.utils.FuncionesUtiles;

import pads.musicPlayer.exceptions.Mp3InvalidFileException;
import pads.musicPlayer.exceptions.Mp3PlayerException;


/**
 * Clase que gestiona los metodos y atributos del objeto Album del sistema.
 * Muflix entiende Album como un conjunto de canciones de un mismo autor.
 * Un album es publico para todos los usuarios del sistema.
 * Muflix exige que no haya 2 albumes con el mismo nombre en la aplicacion.
 * @author Grupo 4 - PADSOF 2261
 *
 */
public class Album extends ElementoMultimedia {

	private List<String> canciones;
	
	/**
	 * Constructor del objeto Album.
	 * @param id ID del album.
	 * @param autor ID del autor del album.
	 * @param titulo Titulo del album.
	 * @param fechaCreacion Fecha de creacion del album.
	 * 
	 */
	public Album(String id, String autor, String titulo, String fechaCreacion) {
		this.titulo = titulo;
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		this.fechaCreacion = LocalDate.parse(fechaCreacion, formatter);
		
		this.id = id;
		this.autor_id = autor;
		this.canciones = new ArrayList<String>();
	}
	
	/**
	 * Obtener la lista de canciones que contiene el album.
	 * @return Lista de canciones del album, inmodificable.
	 * 
	 */
	public List<String> getCanciones() {
		return Collections.unmodifiableList(this.canciones);
	}
	
	/**
	 * Obtener el numero de canciones que contiene el album.
	 * @return Numero de canciones en el album.
	 * 
	 */
	public int getNumCanciones() {
		return this.canciones.size();
	}
	
	/**
	 * Obtener una cancion determinada del album, basandose en el indice que ocupa en la lista.
	 * @param i Entero de la posicion de la cancion en la lista (indice).
	 * @return Cancion especificada del album.
	 * 
	 */
	public String getCancion(int i) {
		return this.canciones.get(i);
	}
	
	/**
	 * Cambiar el titulo de un Album.
	 * @param titulo Nuevo titulo para el Album.
	 * @return true si se ha podido cambiar; false si no se ha podido cambiar.
	 * 
	 */
	public boolean setTitulo(String titulo) {
		this.titulo = titulo;
		return true;
	}
	
	/**
	 * Agregar una cancion nueva al album, comprobando que no exista ya en el.
	 * @param cancion ID de la Cancion que se va a agregar.
	 * @return true si se ha agregado correctamente; false si no se ha agregado correctamente.
	 * 
	 */
	public boolean agregarCancion(String cancion) {
		if(!this.canciones.contains(cancion)) {
			this.canciones.add(cancion);
			return true;
		}
		return false;
	}
	
	/**
	 * Eliminar una cancion de un album, comprobando que exista dentro de el.
	 * @param ID de la Cancion que se quiere eliminar
	 * @return true si se ha eliminado correctamente; false si no se ha eliminado correctamente.
	 * 
	 */
	public boolean eliminarCancion(String cancion) {
		if(this.canciones.contains(cancion)) {
			this.canciones.remove(cancion);
			return true;
		}
		return false;
	}
	
	/**
	 * Realizar la carga de los Elementos Multimedia del album en el reproductor del sistema.
	 * Comprueba el tipo de usuario que solicita esta reproduccion y realiza determinadas acciones
	 * dependiendo de este tipo y de sus limitaciones.
	 * @param m Usuario para el que se carga la multimedia en el reproductor.
	 * @throws CancionIDNoEncontrado Si no se encuentra el ID de alguna cancion en el sistema.
	 * @throws MiembroIDNoEncontrado Si no se encuentra el ID del usuario Miembro en el sistema.
	 * @return true si la carga ha sido exitosa; false si no lo ha sido.
	 * 
	 */
	public boolean cargarMedia(Usuario m) throws CancionIDNoEncontrado, MiembroIDNoEncontrado {
		
		for(int i=0; i<this.canciones.size(); i++){
			if(m instanceof Anonimo) {
				if(m.getLimiteAlcanzado() == true) return false;
				if(!Muflix.getInstance().getCancionById(this.canciones.get(i)).esOculta()) {
					try {
						if(m.getLimiteAlcanzado() == false) {
							Muflix.getInstance().getReproductor().add(Muflix.getInstance().getCancionById(this.canciones.get(i)).getPath());
							Muflix.getInstance().getCancionById(this.canciones.get(i)).incrementarNumReproducciones();
							Muflix.getInstance().getMiembroById(this.autor_id).incrementarTotalReproducciones();
							m.incrementarNumReproducciones();
						}
					} catch (Mp3InvalidFileException e) {
						//e.printStackTrace();
						System.out.println(e);
						return false;
					}
					return true; //No tiene reproduccion automatica.
				}
				
			}
			
			if(m instanceof Administrador) {
				try {
					Muflix.getInstance().getReproductor().add(Muflix.getInstance().getCancionById(this.canciones.get(i)).getPath());
					Muflix.getInstance().getCancionById(this.canciones.get(i)).incrementarNumReproducciones();
					Muflix.getInstance().getMiembroById(this.autor_id).incrementarTotalReproducciones();
				} catch (Mp3InvalidFileException e) {
					//e.printStackTrace();
					System.out.println(e);
					return false;
				}
			}
			
			if(m instanceof Miembro) {
				m = (Miembro) m;
				
				if(m.getLimiteAlcanzado() == true && !(((Miembro) m).esPremium())) return false;
				
				if(!Muflix.getInstance().getCancionById(this.canciones.get(i)).esOculta()) {
					if(Muflix.getInstance().getCancionById(this.canciones.get(i)).esExplicita() && ((Miembro) m).esMayorDeEdad()) {
						try {
							if(m.getLimiteAlcanzado() == false || ((Miembro) m).esPremium()) {
								Muflix.getInstance().getReproductor().add(Muflix.getInstance().getCancionById(this.canciones.get(i)).getPath());
								
								if(!m.getId().equalsIgnoreCase(this.autor_id)) {
									Muflix.getInstance().getCancionById(this.canciones.get(i)).incrementarNumReproducciones();
									Muflix.getInstance().getMiembroById(this.autor_id).incrementarTotalReproducciones();
									m.incrementarNumReproducciones();
								}
							}
						} catch (Mp3InvalidFileException e) {
							//e.printStackTrace();
							System.out.println(e);
							return false;
						}
						if(((Miembro) m).isReproduccionAutomatica() == false) return true;
					}
					//Si no es mayor de edad y la cancion es explicita, no hacer nada.
					if(Muflix.getInstance().getCancionById(this.canciones.get(i)).esExplicita() && !(((Miembro) m).esMayorDeEdad()));
					
					if(!Muflix.getInstance().getCancionById(this.canciones.get(i)).esExplicita()) {
						try {
							if(m.getLimiteAlcanzado() == false || ((Miembro) m).esPremium()) {
								Muflix.getInstance().getReproductor().add(Muflix.getInstance().getCancionById(this.canciones.get(i)).getPath());
								
								if(!m.getId().equalsIgnoreCase(this.autor_id)) {
									Muflix.getInstance().getCancionById(this.canciones.get(i)).incrementarNumReproducciones();
									Muflix.getInstance().getMiembroById(this.autor_id).incrementarTotalReproducciones();
									m.incrementarNumReproducciones();
								}
							}
						} catch (Mp3InvalidFileException e) {
							//e.printStackTrace();
							System.out.println(e);
							return false;
						}
						if(((Miembro) m).isReproduccionAutomatica() == false) return true;
					}
				}	
			}
		}
		return true;
	}
	
	/**
	 * Carga el Album en el reproductor de la aplicacion y lo reproduce.
	 * @param m Usuario para el que se reproduce el contenido multimedia.
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
	 * Comprueba si dos objetos Album son iguales, basandose en su ID.
	 * @param o Objeto a comparar.
	 * @return true si son iguales; false si no lo son.
	 * 
	 */
	@Override
	public boolean equals(Object o) {
		if(o instanceof Album) {
			Album cmp = (Album) o;
			if(cmp.id.equalsIgnoreCase(this.id)) return true;
		}
		return false;
	}
	
	/**
	 * Imprime el objeto Album como un String.
	 * @return String del Album. 
	 * 
	 */
	public String toString() {
		String aux = "";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		for(int i=0; i<this.canciones.size(); i++) {
			aux += this.canciones.get(i) + "|";
		}
		
		return this.id + "|" + this.autor_id + "|" + this.titulo + "|" + this.fechaCreacion.format(formatter) + "|" + FuncionesUtiles.removeLastChar(aux);
	}
}
