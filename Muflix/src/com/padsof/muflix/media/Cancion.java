package com.padsof.muflix.media;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.padsof.muflix.core.Muflix;
import com.padsof.muflix.exceptions.MiembroIDNoEncontrado;
import com.padsof.muflix.usuario.Administrador;
import com.padsof.muflix.usuario.Anonimo;
import com.padsof.muflix.usuario.Miembro;
import com.padsof.muflix.usuario.Usuario;
import com.padsof.muflix.utils.ConstantesGlobales;

import pads.musicPlayer.Mp3Player;
import pads.musicPlayer.exceptions.Mp3InvalidFileException;
import pads.musicPlayer.exceptions.Mp3PlayerException;


/**
 * Clase que gestiona los metodos y atributos del objeto Cancion del sistema.
 * Muflix asume que no pueden existir 2 canciones con el mismo nombre en la aplicacion.
 * @author Grupo 4 - PADSOF 2261
 *
 */
public class Cancion extends ElementoMultimedia {
	private double duracion;
	private boolean validada;
	private boolean oculta;
	private boolean explicita;
	private int numReproducciones;
	private String path;
	
	/**
	 * Constructor del objeto Cancion.
	 * @param id ID de la cancion.
	 * @param autor ID del Autor de la cancion.
	 * @param fechaSubida Fecha de la subida de la cancion.
	 * @param path Ruta donde se encuentra el archivo MP3.
	 * @param validada Estado de validacion de la cancion.
	 * @param oculta Estado de visibilidad de la cancion 
	 * @param explicita Estado de limitacion de edad de la cancion.
	 * @param numReproduccion Numero de veces que se ha reproducido la cancion.
	 * 
	 */
	public Cancion(String id, String autor, String titulo, String fechaSubida, String path, boolean validada, boolean oculta, boolean explicita, int numReproducciones) {
		
		this.id = id;
		this.autor_id = autor;
		this.titulo = titulo;
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		this.fechaCreacion = LocalDate.parse(fechaSubida, formatter);
		
		this.validada = validada;
		this.oculta = oculta;
		this.explicita = explicita;
		this.numReproducciones = numReproducciones;
		this.path = path;
		
		try {
			this.duracion = Mp3Player.getDuration(this.path);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			System.out.println(ConstantesGlobales.ERROR_PREFIX + "No se puede acceder al archivo.");
		}
		
	}
	
	/**
	 * Obtener la ruta de del archivo MP3 de la Cancion.
	 * @return Ruta del archivo MP3 de la Cancion.
	 * 
	 */
	public String getPath() {
		return this.path;
	}
	
	/**
	 * Obtener el numero de reproducciones de la Cancion.
	 * @return Numero de reproducciones de la Cancion.
	 * 
	 */
	public int getNumReproducciones() {
		return this.numReproducciones;
	}
	
	/**
	 * Validar la Cancion, permitiendo asi su visibilidad.
	 * 
	 */
	public void validar() {
		this.validada = true;
		this.oculta = false;
	}
	
	/**
	 * Establecer si una cancion es para +18 o no.
	 * @param flag True o false dependiendo de si es explicita o no.
	 * 
	 */
	public void setExplicita(boolean flag) {
		this.explicita = flag;
	}
	
	/**
	 * Al bloquear la Cancion, se oculta. Metodo usado en las denuncias por plagio.
	 * 
	 */
	public void bloquear() {
		this.oculta = true;
	}
	
	/**
	 * Desbloquear la Cancion, ponerla visible de nuevo al publico.
	 * 
	 */
	public void desbloquear() {
		this.oculta = false;
	}
	
	/**
	 * Obtener la duracion en segundos de la Cancion.
	 * @return Duracion en segundos de la Cancion.
	 * 
	 */
	public double getDuracion() {
		return this.duracion;
	}
	
	/**
	 * Cambiar el titulo de una Cancion.
	 * @param titulo Nuevo titulo para la Cancion.
	 * @return true si se ha podido cambiar; false si no se ha podido cambiar.
	 * 
	 */
	public boolean setTitulo(String titulo) {
		if(Muflix.getInstance().getCancionByTitulo(titulo) == null) {
			this.titulo = titulo;
			return true;
		}
		return false;
	}
	
	/**
	 * Incrementar el numero de reproducciones de la Cancion.
	 * @return Nuevo numero de reproducciones de la Cancion.
	 * 
	 */
	public int incrementarNumReproducciones() {
		this.numReproducciones++;
		return this.numReproducciones;
	}
	
	/**
	 * Obtener la validez de una Cancion.
	 * @return true si la Cancion es valida; false si no lo es.
	 * 
	 */
	public boolean esValida() {
		if(this.validada) return true;
		return false;
	}
	
	/**
	 * Obtener el estado de visibilidad de la Cancion.
	 * @return true si la cancion esta oculta; false si no lo es.
	 * 
	 */
	public boolean esOculta() {
		if(this.oculta) return true;
		return false;
	}
	
	/**
	 * Obtener el estado de limitacion de edad de la Cancion.
	 * @return true si la Cancion es explicita; false si no lo es.
	 * 
	 */
	public boolean esExplicita() {
		if(this.explicita) return true;
		return false;
	}
	
	/**
	 * Permite editar la Cancion si no esta validada aun.
	 * @param nuevoNombre Nuevo titulo de la Cancion.
	 * @return true si la Cancion ha podido ser editada; false si no ha podido ser editada.
	 * 
	 */
	public boolean editar(String nuevoNombre) {
		if(this.validada) {
			return false;
		}else {
			return this.setTitulo(nuevoNombre);
		}
	}
	
	/**
	 * Carga el Elemento Multimedia Cancion en el sistema.
	 * Se lleva a cabo una comprobacion del tipo de usuario que solicita esta carga y sus limitaciones, para
	 * poder tomar diferentes decisiones basandose en estos criterios.
	 * @param m Usuario para el que se carga la multimedia.
	 * @throws MiembroIDNoEncontrado si no se encuentra el ID de un Miembro en el sistema.
	 * @return true si la carga ha sido exitosa; false si no lo ha sido.
	 * 
	 */
	public boolean cargarMedia(Usuario m) throws MiembroIDNoEncontrado {
		
		if(this.esOculta() && !(m instanceof Administrador)) return false;
		if(m instanceof Miembro) {
			m = (Miembro) m;
			if(this.esExplicita() && !((Miembro) m).esMayorDeEdad()) return false;
			if(!((Miembro) m).esPremium() && m.getLimiteAlcanzado() == true) return false;
		}
		if(m instanceof Anonimo) {
			m = (Anonimo) m;
			if(m.getLimiteAlcanzado() == true) return false;
		}
		if(!m.getId().equalsIgnoreCase(this.autor_id)) {
			this.incrementarNumReproducciones();
			Muflix.getInstance().getMiembroById(this.autor_id).incrementarTotalReproducciones();
			m.incrementarNumReproducciones();
		}
		
		try {
			Muflix.getInstance().getReproductor().add(this.path);
		} catch (Mp3InvalidFileException e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
		return true;
	}
	
	/**
	 * Cargar la Cancion en el reproductor de la aplicacion y reproducirla.
	 * @param m Usuario para el que se reproduce la Cancion.
	 * @return true si la reproduccion ha sido exitosa; false si no lo ha sido.
	 * 
	 */
	public boolean reproducirMedia(Usuario m) {
		
		Muflix.getInstance().limpiarReproductor();
		
		try {
			if(this.cargarMedia(m) == false) return false;
		} catch (MiembroIDNoEncontrado e1) {
			//e.printStackTrace();
			System.out.println(e1);
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
	 * Comprueba si dos objetos Cancion son iguales, basandose en su ID y en su titulo. 
	 * @param o Objeto a comparar.
	 * @return true si son iguales; false si no lo son.
	 * 
	 */
	@Override
	public boolean equals(Object o) {
		if(o instanceof Cancion) {
			Cancion cmp = (Cancion) o;
			if(cmp.id.equalsIgnoreCase(this.id) || cmp.titulo.equalsIgnoreCase(this.titulo)) return true;
		}
		return false;
	}
	
	/**
	 * Imprime el objeto Cancion como un String.
	 * @return String de la Cancion. 
	 * 
	 */
	public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return this.id + "|" + this.autor_id + "|" + this.titulo + "|" + this.fechaCreacion.format(formatter) + "|" + this.path + "|" + this.validada + "|" + this.oculta + "|" + this.explicita + "|" + this.numReproducciones;
	}
}