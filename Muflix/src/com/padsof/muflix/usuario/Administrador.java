package com.padsof.muflix.usuario;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.padsof.muflix.core.Muflix;
import com.padsof.muflix.exceptions.CancionIDNoEncontrado;
import com.padsof.muflix.exceptions.MiembroIDNoEncontrado;


/**
 * Clase que gestiona los atributos y metodos del objeto Administrador del sistema.
 * @author Grupo 4 - PADSOF 2261
 *
 */
public class Administrador extends Registrado {
	
	/**
	 * Constructor del objeto Administrador.
	 * @param email Correo electronico del Administrador.
	 * @param password Password del administrador.
	 * 
	 */
	public Administrador(String email, String password) {
		this.id = "ADM";
		this.nombreUsuario = "Administrador";
		this.email = email;
		this.password = password;
	}
	
	/**
	 * Bloquear temporalmente a un Miembro de la aplicacion.
	 * @param m ID del Miembro a bloquear.
	 * @return true si se ha podido bloquear; false si no se ha podido bloquear.
	 * 
	 */
	public boolean bloquearMiembro(String m) {
		try {
			if(Muflix.getInstance().getMiembroById(m) != null) {
				Muflix.getInstance().getMiembroById(m).bloquear();
				return true;
			}
		} catch (MiembroIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
		return false;
	}
	
	/**
	 * Eliminar el bloqueo temporal a un Miembro de la aplicacion.
	 * @param m ID del Miembro a desbloquear.
	 * @return true si se ha podido desbloquear; false si no se ha podido desbloquear.
	 * 
	 */
	public boolean desbloquearMiembro(String m) {
		try {
			if(Muflix.getInstance().getMiembroById(m) != null) {
				Muflix.getInstance().getMiembroById(m).desbloquear();
				return true;
			}
		} catch (MiembroIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
		return false;
	}
	
	/**
	 * Banea a un Miembro de la aplicacion.
	 * @param m ID del Miembro a banear.
	 * @return true si se ha podido banear; false si no se ha podido banear.
	 * 
	 */
	public boolean banearMiembro(String m) {
		try {
			if(Muflix.getInstance().getMiembroById(m) != null) {
				Muflix.getInstance().getMiembroById(m).banear();
				return true;
			}
		} catch (MiembroIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
		return false;
	}
	
	/**
	 * Desbanear a un Miembro de la aplicacion.
	 * @param m ID del Miembro a desbanear.
	 * @return true si se ha podido desbanear; false si no se ha podido desbanear.
	 * 
	 */
	public boolean desbanearMiembro(String m) {
		try {
			if(Muflix.getInstance().getMiembroById(m) != null) {
				Muflix.getInstance().getMiembroById(m).desbanear();
				return true;
			}
		} catch (MiembroIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
		return false;
	}
	
	/**
	 * Aceptar una denuncia del sistema.
	 * @param d ID de la Denuncia a aceptar.
	 * @return true si se ha podido aceptar; false si no se ha podido aceptar.
	 * 
	 */
	public boolean aceptarDenuncia(String d) {
		if(Muflix.getInstance().getDenunciaById(d) != null) {
			Muflix.getInstance().getDenunciaById(d).aceptar();
			return true;
		}
		return false;
	}
	
	/**
	 * Denegar una denuncia del sistema.
	 * @param d ID de la Denuncia a denegar.
	 * @return true si se ha podido denegar; false si no se ha podido denegar.
	 * 
	 */
	public boolean denegarDenuncia(String d) {
		if(Muflix.getInstance().getDenunciaById(d) != null) {
			Muflix.getInstance().getDenunciaById(d).denegar();
			return true;
		}
		return false;
	}
	
	/**
	 * Cambiar una de las limitaciones de la configuracion del sistema.
	 * @param opcion Opcion a cambiar.
	 * @param valor Nuevo valor para la opcion elegida.
	 * 
	 */
	public void setLimitacion(String opcion, String valor) {
		switch(opcion) {
	    case "maxReproducciones":
	    	Muflix.getInstance().setMaxReproducciones(Integer.parseInt(valor));
	    	break;
	    case "numRepsPremium":
	    	Muflix.getInstance().setNumRepsPremium(Integer.parseInt(valor));
	    	break;
	    case "periodoPremium":
	    	Muflix.getInstance().setPeriodoPremium(Integer.parseInt(valor));
	    	break;
	    case "diasBloqueoTemporal":
	    	Muflix.getInstance().setDiasBloqueoTemporal(Integer.parseInt(valor));
	    	break;
	    case "precioPremium":
	    	Muflix.getInstance().setPrecioPremium(Double.parseDouble(valor));
	    	break;
	    default:
	    	break;
	    }
	}
	
	/**
	 * Validar una cancion del sistema si se encuentra en la lista de canciones no validadas del sistema.
	 * @param c ID de la Cancion a validar.
	 * @return true si se ha podido validar; false si no se ha podido validar.
	 * 
	 */
	public boolean validarCancion(String c) {
		if(Muflix.getInstance().getCancionesNoValidadas().contains(c)) {
			try {
				Muflix.getInstance().getCancionById(c).validar();
			} catch (CancionIDNoEncontrado e) {
				e.printStackTrace();
				System.out.println(e);
			}
			Muflix.getInstance().eliminarCancionNoValida(c);
			return true;
		}
		return false;
	}
	
	/**
	 * Mostrar una lista con las canciones no validadas del sistema.
	 * 
	 */
	public void mostrarCancionesNoValidadas() { //TODO Agregar al diagrama.
		
		for(int i=0; i<Muflix.getInstance().getNumCanciones(); i++) {
			if(!Muflix.getInstance().getCanciones().get(i).esValida()) {
				try {
					System.out.println("[PENDIENTE] Cancion: " + Muflix.getInstance().getCanciones().get(i).getTitulo() + " - Autor: " + Muflix.getInstance().getMiembroById(Muflix.getInstance().getCanciones().get(i).getAutor_id()).getNombreUsuario());
					System.out.println("  -> Dias restantes: " + ChronoUnit.DAYS.between(LocalDate.now(), Muflix.getInstance().getCanciones().get(i).getFechaCreacion()) + ".");
				} catch (MiembroIDNoEncontrado e) {
					//e.printStackTrace();
					System.out.println(e);
					return;
				}

			}
		}
	}
	
	/**
	 * Mostrar todas las denuncias pendientes de resolver del sistema.
	 * 
	 */
	public void mostrarDenuncias() { //TODO Agregar al diagrama.
		
		for(int i=0; i<Muflix.getInstance().getNumDenuncias(); i++) {
			if(!Muflix.getInstance().getDenuncias().get(i).esRevisada()) {
				try {
					System.out.println("[PENDIENTE] Denuncia #" + (i+1) + ": Cancion - " + Muflix.getInstance().getCancionById(Muflix.getInstance().getDenuncias().get(i).getCancion()));
					System.out.println("  -> Denunciante: " + Muflix.getInstance().getMiembroById(Muflix.getInstance().getDenuncias().get(i).getDenunciante()).getNombreUsuario());
					System.out.println("  -> Denunciado: " + Muflix.getInstance().getMiembroById(Muflix.getInstance().getDenuncias().get(i).getDenunciado()).getNombreUsuario());
				} catch (CancionIDNoEncontrado | MiembroIDNoEncontrado e) {
					//e.printStackTrace();
					System.out.println(e);
					return;
				}
			}
		}
	}
	
	/**
	 * Imprime el objeto Administrador como una String.
	 * @return String del objeto Administrador.
	 * 
	 */
	public String toString() {
		return this.id + "|" + this.nombreUsuario + "|" + this.email + "|" + this.password; 
	}
	
	//TODO Comentar
	//TODO Agregar al diagrama.
	/**
	 * Marca como explicita la cancion pasada como argumento.
	 * @param String de la cancion.
	 * @return true si se ha modificado correctamente; false si no se ha modificado correctamente.
	 * 
	 */
	public boolean marcarComoExplicita(String cancion) {
		if(cancion.contains("C-")) return false;
			
		try {
			Muflix.getInstance().getCancionById(cancion).setExplicita(true);
		} catch (CancionIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
			
		return true;
	}
		
	//TODO Comentar
	//TODO Agregar al diagrama.
	/**
	 * Marca como no explicita la cancion pasada como argumento.
	 * @param String de la cancion.
	 * @return true si se ha modificado correctamente; false si no se ha modificado correctamente.
	 * 
	 */
	public boolean marcarComoNoExplicita(String cancion) {
		if(cancion.contains("C-")) return false;
			
		try {
			Muflix.getInstance().getCancionById(cancion).setExplicita(false);
		} catch (CancionIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
			
		return true;
	}
}