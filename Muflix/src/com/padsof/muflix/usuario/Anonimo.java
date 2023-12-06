package com.padsof.muflix.usuario;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.padsof.muflix.core.Muflix;
import com.padsof.muflix.exceptions.MiembroIDNoEncontrado;
import com.padsof.muflix.exceptions.PasswordIncorrecta;
import com.padsof.muflix.exceptions.UsernameNoEncontrado;
import com.padsof.muflix.exceptions.UsuarioBaneado;
import com.padsof.muflix.exceptions.UsuarioBloqueado;
import com.padsof.muflix.utils.ConstantesGlobales;


/**
 * Clase para gestionar los metodos y atributos del objeto Anonimo.
 * Muflix entiende Anonimo como un Usuario que no esta registrado en la aplicacion.
 * @author Grupo 4 - PADSOF 2261
 *
 */
public class Anonimo extends Usuario {
	
	/**
	 * Constructor del objeto Anonimo.
	 * @param id ID a asignar al usuario Anonimo. Al ser Muflix usada por un solo usuario a la vez, coincidira siempre con "INV".
	 * 
	 */
	public Anonimo(String id) {
		this.id = id;
		this.nombreUsuario = "Anonimo";
		this.numReproducciones = 0;
		this.limiteAlcanzado = false;
	}
	
	/**
	 * Realiza el registro de un usuario en el sistema, agregandolo a su lista de Miembros.
	 * Comprueba que no exista un miembro igual en el sistema basandose en el Nomre de Usuario y email dados por argumento.
	 * @param nombreUsuario Nombre de usuario deseado por el Usuario Anonimo para realizar el registro.
	 * @param email Email del Usuario Anonimo para realizar el registro.
	 * @param password Password deseada por el Usuario Anonimo para realizar el registro.
	 * @param nombreCompleto Nombre completo del Usuario Anonimo para realizar el registro.
	 * @param fechaNacimiento Fecha de nacimiento del Usuario Anonimo para realizar el registro.
	 * @return true si se ha podido registrar correctamente; false si ha ocurrido algun error.
	 * 
	 */
	public boolean hacerRegistro(String nombreUsuario, String email, String password, String nombreCompleto, String fechaNacimiento) {
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate localDate = LocalDate.now();
		
		if(Muflix.getInstance().checkNombreUsuario(nombreUsuario)) return false;
		
		Miembro m = new Miembro("U-" + Muflix.getInstance().getNextUserID(), nombreUsuario, email, password, nombreCompleto, dtf.format(localDate), fechaNacimiento, false, false ,0, false, "01/01/" + ConstantesGlobales.NULL_DATE_YEAR, "01/01/" + ConstantesGlobales.NULL_DATE_YEAR);
		
		return Muflix.getInstance().agregarMiembro(m);
	}
	
	/**
	 * Iniciar sesion para pasar de Anonimo a Miembro.
	 * Se comprueba que el nombre de usuario dado por argumento existe en el sistema y que la password coincida.
	 * Se llevan a cabo las comprobaciones de bloqueo temporal y baneo.
	 * Ademas, se establece este nuevo Miembro como usuario que actualmente esta usando la aplicacion (uso unico).
	 * @param usuario Nombre del usuario para iniciar sesion.
	 * @param password Password del usuario que inicia sesion.
	 * @throws UsuarioBloqueado si el usuario se encuentra bloqueado temporalmente.
	 * @throws UsuarioBaneado si el usuario se encuentra baneado permanentemente.
	 * @throws PasswordIncorrecta si la password dada no coincide con la del usuario.
	 * @throws MiembroIDNoEncontrado si no encuentra el ID del usuario en el sistema.
	 * @return true si se ha podido iniciar sesion correctamente; false si ha ocurrido algun error.
	 * 
	 */
	public boolean hacerLogin(String usuario, String password) throws UsuarioBaneado, UsuarioBloqueado, PasswordIncorrecta, MiembroIDNoEncontrado {
		try {
			if(Muflix.getInstance().getMiembroByNombre(usuario) != null) {
				
				if(Muflix.getInstance().getMiembroByNombre(usuario).esBloqueado()) {
					throw new UsuarioBloqueado(Muflix.getInstance().getMiembroByNombre(usuario).getId());
				}
				
				if(Muflix.getInstance().getMiembroByNombre(usuario).esBaneado()) {
					throw new UsuarioBaneado(Muflix.getInstance().getMiembroByNombre(usuario).getId());
				}
				
				if(!Muflix.getInstance().getMiembroByNombre(usuario).getPassword().equals(password)) {
					throw new PasswordIncorrecta(usuario);
				}
				
				if(Muflix.getInstance().getMiembroByNombre(usuario).getPassword().equals(password)) {
					Muflix.getInstance().setUsuarioActual(Muflix.getInstance().getMiembroByNombre(usuario));
					return true;
				}
			}
		} catch (UsernameNoEncontrado e) {
			e.printStackTrace();
			System.out.println(e);
		}
		return false;
	}
	
}
