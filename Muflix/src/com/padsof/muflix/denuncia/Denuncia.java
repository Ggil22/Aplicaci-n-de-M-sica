package com.padsof.muflix.denuncia;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.padsof.muflix.core.Muflix;
import com.padsof.muflix.exceptions.CancionIDNoEncontrado;
import com.padsof.muflix.exceptions.MiembroIDNoEncontrado;


/**
 * Clase que gestiona los metodos y atributos del objeto Denuncia del sistema.
 * Actualmente, estas denuncias se asignan a una Cancion si existe algun problema de plagio.
 * @author Grupo 4 - PADSOF 2261
 *
 */
public class Denuncia {
	private String id;
	private String denunciante;
	private String denunciado;
	private LocalDate fecha;
	private String cancion;
	private String comentario;
	private boolean revisada;
	
	/**
	 * Constructor del objeto Denuncia.
	 * @param id ID de la denuncia.
	 * @param denunciante ID del Miembro que realiza la denuncia.
	 * @param denunciado ID del Miembro al que se le ha denunciado.
	 * @param fecha Fecha de la denuncia.
	 * @param cancion Cancion por lo que se ha denunciado.
	 * @param comentario Razon que expone el denunciante para razonar la denuncia.
	 * @param revisada Estado de revision de la denuncia.
	 * 
	 */
	public Denuncia(String id, String denunciante, String denunciado, String fecha, String cancion, String comentario, boolean revisada) {
		
		this.id = id;
		this.denunciante = denunciante;
		this.denunciado = denunciado;
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		this.fecha = LocalDate.parse(fecha, formatter);
		
		this.cancion = cancion;
		this.comentario = comentario;
		this.revisada = revisada;
	}
	
	/**
	 * Obtener el ID de la Denuncia.
	 * @return ID de la Denuncia.
	 * 
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * Obtener el ID del usuario que creo la Denuncia.
	 * @return ID del denunciante.
	 * 
	 */
	public String getDenunciante() {
		return this.denunciante;
	}
	
	/**
	 * Obtener el ID del usuario denunciado.
	 * @return ID del denunciado.
	 * 
	 */
	public String getDenunciado() {
		return this.denunciado;
	}
	
	/**
	 * Obtener la fecha de creacion de la Denuncia.
	 * @return Fecha de creacion de la Denuncia.
	 * 
	 */
	public LocalDate getFecha() {
		return this.fecha;
	}
	
	/**
	 * Cambiar fecha de creacion de la Denuncia.
	 * @param fecha Nueva fecha de creacion de la Denuncia.
	 * 
	 */
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	/**
	 * Obtener el ID de la cancion denunciada por plagio.
	 * @return ID de la cancion denunciada.
	 * 
	 */
	public String getCancion() {
		return this.cancion;
	}
	
	/**
	 * Obtener el comentario/motivo de la Denuncia.
	 * @return Motivo de la Denuncia.
	 * 
	 */
	public String getComentario() {
		return this.comentario;
	}
	
	/**
	 * Cambiar el comentario/motivo de la Denuncia.
	 * @param comentario Nuevo motivo de la denuncia.
	 * 
	 */
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	
	/**
	 * Cambiar el estado de revision de la denuncia.
	 * @param revisada True o false, dependiendo de si ha sido revisada o no
	 * 
	 */
	public void setRevisada(boolean revisada) {
		this.revisada = revisada;
	}
	
	/**
	 * Comprobar si una Denuncia ha sido revisada
	 * @return true si ha sido revisada; false si no lo ha sido.
	 * 
	 */
	public boolean esRevisada() {
		return this.revisada;
	}
	
	/**
	 * Aceptar la Denuncia en cuestion, baneando al Miembro denunciado,
	 * poniendo sus canciones en estado oculto.
	 * 
	 */
	public void aceptar() {
		this.revisada = true;
		try {
			Muflix.getInstance().getMiembroById(this.denunciado).banear();
		} catch (MiembroIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return;
		}
		for(int i=0; i<Muflix.getInstance().getNumCanciones(); i++) {
			if(Muflix.getInstance().getCanciones().get(i).getAutor_id().equalsIgnoreCase(this.denunciado)) {
				Muflix.getInstance().getCanciones().get(i).bloquear();
			}
		}
	}
	
	/**
	 * Denegar la denuncia. Se sanciona al Denunciante bloqueandolo temporalmente.
	 * Se elimina del estado "oculto" la cancion afectada.
	 * 
	 */
	public void denegar() {
		this.revisada = true;
		try {
			Muflix.getInstance().getCancionById(this.cancion).desbloquear();
		} catch (CancionIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
		}
		try {
			Muflix.getInstance().getMiembroById(this.denunciante).bloquear();
		} catch (MiembroIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return;
		}
	}
	
	/**
	 * Metodo para comprobar si dos denuncias son iguales.
	 * @param o Objeto a comprobar.
	 * @return true si son iguales; false si no lo son.
	 * 
	 */
	@Override
	public boolean equals(Object o) {
		if(o instanceof Denuncia) {
			Denuncia cmp = (Denuncia) o;
			if(cmp.id.equalsIgnoreCase(this.id)) return true;
		}
		return false;
	}
	
	/**
	 * Imprime el objeto Denuncia como un String.
	 * @return String de la Denuncia. 
	 * 
	 */
	public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return this.id + "|" + this.denunciante + "|" + this.denunciado + "|" + this.fecha.format(formatter) + "|" + this.cancion + "|" + this.comentario + "|" + this.revisada;
	}
	
}