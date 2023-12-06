package com.padsof.muflix.usuario;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.padsof.muflix.core.Muflix;
import com.padsof.muflix.denuncia.Denuncia;
import com.padsof.muflix.exceptions.AlbumIDNoEncontrado;
import com.padsof.muflix.exceptions.CancionIDNoEncontrado;
import com.padsof.muflix.exceptions.MiembroIDNoEncontrado;
import com.padsof.muflix.exceptions.PlaylistIDNoEncontrado;
import com.padsof.muflix.media.Album;
import com.padsof.muflix.media.Cancion;
import com.padsof.muflix.media.Playlist;
import com.padsof.muflix.utils.ConstantesGlobales;
import com.padsof.muflix.utils.FuncionesUtiles;

import es.uam.eps.padsof.telecard.FailedInternetConnectionException;
import es.uam.eps.padsof.telecard.InvalidCardNumberException;
import es.uam.eps.padsof.telecard.OrderRejectedException;
import es.uam.eps.padsof.telecard.TeleChargeAndPaySystem;
import pads.musicPlayer.Mp3Player;


/**
 * Clase para gestionar los metodos y atributos del objeto Miembro.
 * Muflix entiende Miembro como un Usuario registrado en la aplicacion.
 * @author Grupo 4 - PADSOF 2261
 *
 */
public class Miembro extends Registrado {
	
	private String nombreCompleto;
	private LocalDate fechaRegistro;
	private LocalDate fechaNacimiento;
	private LocalDate fechaBloqueo;
	private LocalDate fechaPremium;
	private boolean bloqueado;
	private boolean baneado;
	private boolean premium;
	private int totalReproducciones;
	
	private List<String> seguidores;
	private List<String> seguidos;
	
	/**
	 * Constructor del objeto Miembro.
	 * @param id ID a asignar al Miembro.
	 * @param nombreUsuario Nombre de usuario del Miembro.
	 * @param email Email del Miembro.
	 * @param password Password del Miembro.
	 * @param nombreCompleto Nombre completo del Miembro.
	 * @param fechaRegistro Fecha en la que el Miembro se dio de alta en la aplicacion.
	 * @param fechaNacimiento Fecha de nacimiento del Miembro.
	 * @param baneado Estado de baneo del Miembro.
	 * @param premium Estado de la suscripcion premium del Miembro.
	 * @param reproducciones Numero de reproducciones totales realizadas en las canciones del Miembro.
	 * @param bloqueado Estado de bloqueo temporal del Miembro.
	 * @param fechaBloqueo Fecha en la que se llevo a cabo el ultimo bloqueo temporal del Miembro.
	 * @param fechaPremium Fecha en la que el Miembro adquirio su suscripcion Premium.
	 * 
	 */
	public Miembro(String id, String nombreUsuario, String email, String password, String nombreCompleto, String fechaRegistro, String fechaNacimiento, boolean baneado, boolean premium, int reproducciones, boolean bloqueado, String fechaBloqueo, String fechaPremium) {
		
		this.id = id;
		this.nombreUsuario = nombreUsuario;
		this.email = email;
		this.password = password;
		this.nombreCompleto = nombreCompleto;
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		this.fechaRegistro = LocalDate.parse(fechaRegistro, formatter);
		
		formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		this.fechaNacimiento = LocalDate.parse(fechaNacimiento, formatter);
		
		formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		this.fechaPremium = LocalDate.parse(fechaPremium, formatter);
		
		formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		this.fechaBloqueo = LocalDate.parse(fechaBloqueo, formatter);
		
		this.baneado = baneado;
		this.premium = premium;
		this.totalReproducciones = reproducciones;
		
		this.bloqueado = bloqueado;
		
		this.numReproducciones = 0;
		
		this.seguidos = new ArrayList<String>();
		this.seguidores = new ArrayList<String>();
	
	}
	
	/**
	 * Obtener el nombre completo del Miembro.
	 * @return Nombre completo del Miembro.
	 * 
	 */
	public String getNombreCompleto() {
		return this.nombreCompleto;
	}
	
	/**
	 * Obtener la lista de IDs de los usuarios seguido por el Miembro.
	 * @return Lista inmodificable de IDs de usuarios seguidos por el Miembro.
	 * 
	 */
	public List<String> getSeguidos() {
		return Collections.unmodifiableList(this.seguidos);
	}
	
	/**
	 * Obtener el numero de usuarios seguidos por el Miembro.
	 * @return Cantidad de usuarios seguidos por el Miembro.
	 * 
	 */
	public int getNumSeguidos() {
		return this.seguidos.size();
	}
	
	/**
	 * Obtener la lista de IDs de los usuarios que siguen al Miembro.
	 * @return Lista inmodificable de IDs de usuarios que siguen al Miembro.
	 * 
	 */
	public List<String> getSeguidores() {
		return Collections.unmodifiableList(this.seguidores);
	}
	
	/**
	 * Obtener el numero de usuarios que siguen al Miembro.
	 * @return Cantidad de seguidores del Miembro.
	 * 
	 */
	public int getNumSeguidores() {
		return this.seguidores.size();
	}
	
	/**
	 * Cambiar el nombre completo de un Miembro.
	 * @param nombreCompleto Nuevo nombre completo para el Miembro.
	 * 
	 */
	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}
	
	/**
	 * Obtener la fecha en la que un Miembro adquirio su ultima suscripcion Premium.
	 * @return Fecha de adquisicion de la ultima suscripcion Premium del Miembro.
	 * 
	 */
	public LocalDate getFechaPremium() {
		return this.fechaPremium;
	}
	
	/**
	 * Cambiar la fecha en la que el Miembro adquirio su ultima suscripcion Premium.
	 * @param fechaPremium Nueva fecha de adquisicion de la ultima suscripcion Premium.
	 * 
	 */
	public void setFechaPremium(LocalDate fechaPremium) {
		this.fechaPremium = fechaPremium;
	}
	
	/**
	 * Obtener la fecha en la que el Miembro fue bloqueado temporalmente por ultima vez.
	 * @return Fecha en la que el Miembro fue bloqueado temporalmente por ultima vez.
	 * 
	 */
	public LocalDate getFechaBloqueo() {
		return this.fechaBloqueo;
	}
	
	/**
	 * Cambiar la fecha en la que el Miembro fue bloqueado temporalmente por ultima vez.
	 * @param fechaBloqueo Nueva fecha de ultimo bloqueo temporal.
	 * 
	 */
	public void setFechaBloqueo(LocalDate fechaBloqueo) {
		this.fechaBloqueo = fechaBloqueo;
	}
	
	/**
	 * Obtener la fecha en la que el Miembro fue registrado en el sistema.
	 * @return Fecha en la que el Miembro fue registrado en el sistema.
	 * 
	 */
	public LocalDate getFechaRegistro() {
		return this.fechaRegistro;
	}

	/**
	 * Cambiar la fecha en la que el Miembro fue registrado en el sistema.
	 * @param fechaRegistro Nueva fecha de registro.
	 * 
	 */
	public void setFechaRegistro(LocalDate fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	
	/**
	 * Obtener la fecha de nacimiento del Miembro.
	 * @return Fecha de nacimiento del Miembro.
	 * 
	 */
	public LocalDate getFechaNacimiento() {
		return this.fechaNacimiento;
	}
	
	/**
	 * Cambiar la fecha de nacimiento del Miembro.
	 * @param Nueva fecha de nacimiento del Miembro.
	 * 
	 */
	public void setFechaNacimiento(LocalDate fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}
	
	/**
	 * Comprobar si el Miembro es mayor de edad o no.
	 * @return true si es mayor de edad; false si no lo es.
	 * 
	 */
	public boolean esMayorDeEdad() {
		LocalDate hoy = LocalDate.now();
		Period periodo = Period.between(this.fechaNacimiento, hoy);
		
		if(periodo.getYears() >= 18) return true;
		return false;
	}
	
	/**
	 * Comprobar si el Miembro esta baneado del sistema o no.
	 * @return true si esta baneado; false si no lo esta.
	 * 
	 */
	public boolean esBaneado() {
		return this.baneado;
	}
	
	/**
	 * Banear al Miembro del sistema.
	 * 
	 */
	public void banear() {
		this.baneado = true;
	}
	
	/**
	 * Desbanear al Miembro del sistema.
	 * 
	 */
	public void desbanear() {
		this.baneado = false;
	}
	
	/**
	 * Comprobar si el Miembro esta bloqueado temporalmente del sistema o no.
	 * @return true si esta bloqueado temporalmente; false si no lo esta.
	 * 
	 */
	public boolean esBloqueado() {
		return this.bloqueado;
	}
	
	/**
	 * Bloquear temporalmente al Miembro del sistema.
	 * 
	 */
	public void bloquear() {
		this.bloqueado = true;
		this.fechaBloqueo = LocalDate.now();
	}
	
	/**
	 * Desbloquear al Miembro del sistema.
	 * 
	 */
	public void desbloquear() {
		this.bloqueado = false;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		this.fechaBloqueo = LocalDate.parse("01/01/" + ConstantesGlobales.NULL_DATE_YEAR, formatter);
	}
	
	
	/**
	 * Comprobar si el Miembro es Premium o no.
	 * @return true si es Premium; false si no lo es.
	 * 
	 */
	public boolean esPremium() {
		return this.premium;
	}
	
	/**
	 * Comprobar si el Miembro es seguidor del parametro pasado como argumento.
	 * @param seguido ID del usuario a comprobar si es seguido por el Miembro.
	 * @return true si es seguidor; false si no lo es.
	 * 
	 */
	public boolean esSeguidorDe(String seguido) {
		if(this.seguidos.contains(seguido)) return true;
		return false;
	}
	
	/**
	 * Comprobar si el Miembro es seguido por el parametro pasado como argumento.
	 * @param seguidor ID del usuario a comprobar si sigue al Miembro.
	 * @return true si le sigue; false si no le sigue.
	 * 
	 */
	public boolean esSeguidoPor(String seguidor) {
		if(this.seguidores.contains(seguidor)) return true;
		return false;
	}
	
	/**
	 * Otorgar la suscripcion Premium al Miembro.
	 * 
	 */
	public void darPremium() {
		this.premium = true;
		this.fechaPremium = LocalDate.now();
	}
	
	/**
	 * Eliminar la suscripcion Premium del Miembro.
	 * 
	 */
	public void quitarPremium() {
		this.premium = false;
		this.desactivarReproduccionAutomatica();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		this.fechaPremium = LocalDate.parse("01/01/" + ConstantesGlobales.NULL_DATE_YEAR, formatter);
	}

	/**
	 * Obtener el total de reproducciones de las canciones del Miembro.
	 * @return Total de reproducciones en las canciones subidas por el Miembro.
	 * 
	 */
	public int getTotalReproducciones() {
		return this.totalReproducciones;
	}
	
	/**
	 * Cambiar el total de reproducciones de las canciones del Miembro.
	 * @param totalReproducciones Nuevo total de reproducciones en las canciones subidas por el Miembro.
	 * 
	 */
	public void setTotalReproducciones(int totalReproducciones) {
		this.totalReproducciones = totalReproducciones;
	}
	
	/**
	 * Incrementa en 1 el total de reproducciones en las canciones del Miembro.
	 * 
	 */
	public void incrementarTotalReproducciones() {
		this.totalReproducciones++;
	}
	
	/**
	 * Restar una cantidad de concreta de reproducciones totales del Miembro.
	 * @param x Cantidad de reproducciones a restar.
	 * 
	 */
	public void restarTotalReproducciones(int x) {
		this.totalReproducciones -= x;
	}
	
	/**
	 * Agregar un seguidor a la lista de seguidores del Miembro, comprobando que no exista en ella.
	 * @param s ID del seguidor a agregar.
	 * @return true si se ha podido agregar; false si ha habido algun error.
	 * 
	 */
	public boolean agregarSeguidor(String s) {
		if(!this.seguidores.contains(s)) {
			this.seguidores.add(s);
			return true;
		}
		return false;
	}
	
	/**
	 * Eliminar un seguidor de la lista de seguidores del Miembro, comprobando que exista en ella.
	 * @param s ID del seguidor a eliminar.
	 * @return true si se ha podido eliminar; false si ha habido algun error.
	 * 
	 */
	public boolean eliminarSeguidor(String s) {
		if(this.seguidores.contains(s)) {
			this.seguidores.remove(s);
			return true;
		}
		return false;
	}
	
	/**
	 * Agregar un seguido a la lista de seguidos por el Miembro, comprobando que no exista en ella.
	 * @param s ID del usuario a agregar.
	 * @return true si se ha podido agregar; false si ha habido algun error.
	 * 
	 */
	public boolean agregarSeguido(String s) {
		if(!this.seguidos.contains(s)) {
			this.seguidos.add(s);
			return true;
		}
		return false;
	}

	/**
	 * Eliminar un seguido de la lista de seguidos por el Miembro, comprobando que exista en ella.
	 * @param s ID del usuario a eliminar.
	 * @return true si se ha podido eliminar; false si ha habido algun error.
	 * 
	 */
	public boolean eliminarSeguido(String s) {
		if(this.seguidos.contains(s)) {
			this.seguidos.remove(s);
			return true;
		}
		return false;
	}
	
	/**
	 * Seguir al Miembro dado como argumento, actualizando atributos en ambos miembros a la vez.
	 * @param s ID del Miembro a seguir.
	 * @return true si se ha establecido la relacion de follow correctamente; false si ha ocurrido algun error.
	 * 
	 */
	public boolean seguir(String s) { //TODO AGREGAR A LOS DIAGRAMAS
		try {
			if(Muflix.getInstance().getMiembroById(s) == null) return false;
		} catch (MiembroIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
		
		try {
			Muflix.getInstance().getMiembroById(s).agregarSeguidor(this.id);
		} catch (MiembroIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
		return this.agregarSeguido(s);
	}
	
	/**
	 * Dejar de seguir al Miembro dado como argumento, actualizando atributos en ambos miembros a la vez.
	 * @param s ID del Miembro a dejar de seguir.
	 * @return true si se ha establecido la relacion de unfollow correctamente; false si ha ocurrido algun error.
	 * 
	 */
	public boolean dejarDeSeguir(String s) { //TODO AGREGAR A LOS DIAGRAMAS
		try {
			if(Muflix.getInstance().getMiembroById(s) == null) return false;
		} catch (MiembroIDNoEncontrado e1) {
			//e.printStackTrace();
			System.out.println(e1);
			return false;
		}
		
		try {
			Muflix.getInstance().getMiembroById(s).eliminarSeguidor(this.id);
		} catch (MiembroIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
		return this.eliminarSeguido(s);
	}
	
	/**
	 * Pagar una suscripcion Premium para el Miembro.
	 * Se comprueba que la tarjeta sea validad, asi como la conexion a internet y si la orden es rechazada.
	 * @param tarjeta Numero de la tarjeta para realizar el pago.
	 * @param concepto Concepto del pago realizado.
	 * @return true si se realiza correctamente; false si ocurre algun error.
	 * 
	 */
	public boolean pagarPremium(String tarjeta, String concepto) {
		
		if(!TeleChargeAndPaySystem.isValidCardNumber(tarjeta)) return false;
		
		try {
			TeleChargeAndPaySystem.charge(tarjeta, concepto, Muflix.getInstance().getPrecioPremium());
		} catch (InvalidCardNumberException e) {
			e.printStackTrace();
			return false;
		} catch (FailedInternetConnectionException e) {
			e.printStackTrace();
			return false;
		} catch (OrderRejectedException e) {
			e.printStackTrace();
			return false;
		}
		
		this.darPremium();
		return true;
	}
	
	/**
	 * Subir una cancion con el Miembro como autor.
	 * Se comprueba que se especifique un archivo MP3 valido y que la cancion no exista ya en el sistema, basandose en el titulo.
	 * @param titulo Titulo de la nueva cancion.
	 * @param path Ruta hasta el archivo MP3 de la cancion.
	 * @return true si se ha podido subir la cancion; false si ha habido algun error.
	 * 
	 */
	public boolean subirCancion(String titulo, String path) {
		if(Mp3Player.isValidMp3File(path)) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			LocalDate localDate = LocalDate.now();
			
			if(Muflix.getInstance().getCancionByTitulo(titulo) == null) {
				Cancion c = new Cancion("C-" + Muflix.getInstance().getNextSongID(), this.id, titulo, dtf.format(localDate), path, false, true, false, 0);
				if(Muflix.getInstance().agregarCancion(c)) {
					return true;
				}
			}	
		}
		return false;
	}
	
	/**
	 * Crear un album con el Miembro como autor.
	 * @param titulo Titulo del nuevo album.
	 * @return true si se ha podido crear el album; false si ha habido algun error.
	 * 
	 */
	public boolean crearAlbum(String titulo) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate localDate = LocalDate.now();
		
		Album a = new Album("A-" + Muflix.getInstance().getNextAlbumID(), this.id, titulo, dtf.format(localDate));
		if(Muflix.getInstance().agregarAlbum(a)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Elimina un album creado por el propio Miembro, asi como todo su contenido.
	 * @param album ID del album a eliminar.
	 * @return true si se ha podido eliminar el album; false si ha habido algun error.
	 * 
	 */
	public boolean eliminarAlbum(String album) { //TODO Agregar al diagrama.
		try {
			if(!Muflix.getInstance().getAlbumById(album).getAutor_id().equals(this.id)) return false;
		} catch (AlbumIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
		try {
			if(Muflix.getInstance().eliminarAlbum(Muflix.getInstance().getAlbumById(album))) {
				return true;
			}
		} catch (AlbumIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
		return false;
	}
	
	/**
	 * Agrega una cancion al album creado por el propio Miembro.
	 * @param album ID del album objetivo.
	 * @param contenido ID de la cancion a agregar.
	 * @return true si se ha podido agregar el contenido al album; false si ha habido algun error.
	 * 
	 */
	public boolean agregarContenidoAlbum(String album, String contenido) { //TODO Agregar al diagrama.
		
		if(!contenido.contains("C-")) return false;
		
		try {
			if(Muflix.getInstance().getCancionById(contenido).getAutor_id().equals(this.id)) return false;
		} catch (CancionIDNoEncontrado e1) {
			//e.printStackTrace();
			System.out.println(e1);
			return false;
		}
		
		try {
			if(!Muflix.getInstance().getAlbumById(album).getAutor_id().equals(this.id)) return false;
		} catch (AlbumIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
		try {
			return (Muflix.getInstance().getAlbumById(album).agregarCancion(contenido));
		} catch (AlbumIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
	}
	
	/**
	 * Elimina una cancion del album creado por el propio Miembro.
	 * @param album ID del album objetivo.
	 * @param contenido ID de la cancion a eliminar.
	 * @return true si se ha podido eliminar el contenido del album; false si ha habido algun error.
	 * 
	 */
	public boolean eliminarContenidoAlbum(String album, String contenido) { //TODO Agregar al diagrama.
		
		if(!contenido.contains("C-")) return false;
		
		try {
			if(!Muflix.getInstance().getAlbumById(album).getAutor_id().equals(this.id)) return false;
		} catch (AlbumIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
		try {
			return (Muflix.getInstance().getAlbumById(album).eliminarCancion(contenido));
		} catch (AlbumIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
	}
	
	/**
	 * Crear una playlist con el Miembro como autor.
	 * @param titulo Titulo de la nueva playlist.
	 * @return true si se ha podido crear la playlist; false si ha habido algun error.
	 * 
	 */
	public boolean crearPlaylist(String titulo) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate localDate = LocalDate.now();
		
		Playlist p = new Playlist("P-" + Muflix.getInstance().getNextPlaylistID(), this.id, titulo, dtf.format(localDate));
		if(Muflix.getInstance().agregarPlaylist(p)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Elimina una playlist creada por el propio Miembro, sin eliminar su contenido.
	 * @param playlist ID de la playlist a eliminar.
	 * @return true si se ha podido eliminar la playlist; false si ha habido algun error.
	 * 
	 */
	public boolean eliminarPlaylist(String playlist) { //TODO Agregar al diagrama.

		try {
			if(!Muflix.getInstance().getPlaylistById(playlist).getAutor_id().equals(this.id)) return false;
		} catch (PlaylistIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
	
		try {
			if(Muflix.getInstance().eliminarPlaylist(Muflix.getInstance().getPlaylistById(playlist))) {
				return true;
			}
		} catch (PlaylistIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}

		return false;
	}
	
	/**
	 * Agrega contenido a la playlist creada por el propio Miembro.
	 * @param playlist ID de la playlist objetivo.
	 * @param contenido ID del contenido multimedia a agregar.
	 * @return true si se ha podido agregar el contenido a la playlist; false si ha habido algun error.
	 * 
	 */
	public boolean agregarContenidoPlaylist(String playlist, String contenido) { //TODO Agregar al diagrama.
		
		if(!contenido.contains("C-") || !contenido.contains("A-") || !contenido.contains("P-")) return false;
		
		try {
			if(!Muflix.getInstance().getPlaylistById(playlist).getAutor_id().equals(this.id)) return false;
		} catch (PlaylistIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
		try {
			return (Muflix.getInstance().getPlaylistById(playlist).agregarContenido(contenido));
		} catch (PlaylistIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
	}
	
	/**
	 * Elimina contenido de la playlist creada por el propio Miembro.
	 * @param playlist ID de la playlist objetivo.
	 * @param contenido ID del contenido multimedia a eliminar.
	 * @return true si se ha podido eliminar el contenido de la playlist; false si ha habido algun error.
	 * 
	 */
	public boolean eliminarContenidoPlaylist(String playlist, String contenido) { //TODO Agregar al diagrama.
		
		if(!contenido.contains("C-") || !contenido.contains("A-") || !contenido.contains("P-")) return false;
		
		try {
			if(!Muflix.getInstance().getPlaylistById(playlist).getAutor_id().equals(this.id)) return false;
		} catch (PlaylistIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
		try {
			return (Muflix.getInstance().getPlaylistById(playlist).eliminarContenido(contenido));
		} catch (PlaylistIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
			return false;
		}
	}
	
	/**
	 * Crear una denuncia por plagio con el Miembro como denunciante.
	 * Se comprueba que la cancion denunciada y su autor existan en el sistema.
	 * @param cancion ID de la cancion a denunciar.
	 * @param comentario Comentario donde se expone el motivo de la denuncia (plagio).
	 * @return true si se ha podido crear la denuncia; false si ha habido algun error.
	 * 
	 */
	public boolean denunciarPlagio(String cancion, String comentario) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate localDate = LocalDate.now();
		
		try {
			if(Muflix.getInstance().getCancionById(cancion).getAutor_id().equalsIgnoreCase(this.id)) return false;
		} catch (CancionIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
		}
		
		String denunciado = "";
		try {
			denunciado = Muflix.getInstance().getCancionById(cancion).getAutor_id();
		} catch (CancionIDNoEncontrado e) {
			e.printStackTrace();
			System.out.println(e);
		}
		Denuncia d = new Denuncia("D" + Muflix.getInstance().getNextReportID(), this.id, denunciado, dtf.format(localDate), cancion, comentario, false);
		
		try {
			Muflix.getInstance().getCancionById(cancion).bloquear();
		} catch (CancionIDNoEncontrado e) {
			//e.printStackTrace();
			System.out.println(e);
		}
		
		if(Muflix.getInstance().agregarDenuncia(d)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Mostrar la actividad al Miembro de los usuarios seguidos por el.
	 * El sistema se basa en la constante global ACTIVITY_DAYS para mostrar solo la actividad reciente.
	 * Se mostraran nuevas canciones y nuevos albumes de los usuarios seguidos por el Miembro.
	 * 
	 */
	public void mostrarActividad() {
		for(int i=0; i<Muflix.getInstance().getNumCanciones(); i++) {
			if(Muflix.getInstance().getCanciones().get(i).getAutor_id().equals(this.id) && !Muflix.getInstance().getCanciones().get(i).esOculta()) {
				if(ChronoUnit.DAYS.between(Muflix.getInstance().getCanciones().get(i).getFechaCreacion(), LocalDate.now()) <= ConstantesGlobales.ACTIVITY_DAYS) {
					System.out.println("[" + Muflix.getInstance().getCanciones().get(i).getFechaCreacion() + "]" + "El usuario " + this.nombreUsuario + " ha publicado una nueva canción: " + Muflix.getInstance().getCanciones().get(i).getTitulo());
				}
			}
		}
		
		for(int i=0; i<Muflix.getInstance().getNumAlbumes(); i++) {
			if(Muflix.getInstance().getAlbumes().get(i).getAutor_id().equals(this.id)) {
				if(ChronoUnit.DAYS.between(Muflix.getInstance().getAlbumes().get(i).getFechaCreacion(), LocalDate.now()) <= ConstantesGlobales.ACTIVITY_DAYS) {
					System.out.println("[" + Muflix.getInstance().getAlbumes().get(i).getFechaCreacion() + "]" + "El usuario " + this.nombreUsuario + " ha publicado un nuevo álbum: " + Muflix.getInstance().getAlbumes().get(i).getTitulo());
				}
			}
		}
	}
	
	/**
	 * Mostrar las canciones subidas por el usuario.
	 * 
	 */
	public void mostrarCanciones() { //TODO AGregar al diagrama
		for(int i=0; i<Muflix.getInstance().getNumCanciones(); i++) {
			if(Muflix.getInstance().getCanciones().get(i).getAutor_id().equals(this.id)) {
				if(!Muflix.getInstance().getCanciones().get(i).esValida()) {
					System.out.println("[PENDIENTE DE VALIDACION] Cancion #" + i+1 + ": Titulo: " + Muflix.getInstance().getCanciones().get(i).getTitulo() + ".");
				}
				else if(Muflix.getInstance().getCanciones().get(i).esOculta()) {
					System.out.println("[OCULTA] Cancion #" + i+1 + ": Titulo: " + Muflix.getInstance().getCanciones().get(i).getTitulo() + ".");
				}
				else {
					if(Muflix.getInstance().getCanciones().get(i).esExplicita()) {
						System.out.println("[EXPLICITA] Cancion #" + i+1 + ": Titulo: " + Muflix.getInstance().getCanciones().get(i).getTitulo() + ".");
					} else{
						System.out.println("Cancion #" + i+1 + ": Titulo: " + Muflix.getInstance().getCanciones().get(i).getTitulo() + ".");
					}
				}
			}
		}
	}
	
	/**
	 * Mostrar los albumes y su contenido, creados por el propio usuario.
	 * 
	 */
	public void mostrarAlbumes() { //TODO AGregar al diagrama
		for(int i=0; i<Muflix.getInstance().getNumAlbumes(); i++) {
			String aux = "";
			String id;
			if(Muflix.getInstance().getAlbumes().get(i).getAutor_id().equals(this.id)) {
				for(int j=0; j<Muflix.getInstance().getAlbumes().get(i).getCanciones().size(); j++) {
					id = Muflix.getInstance().getAlbumes().get(i).getCanciones().get(j);
					if(id.contains("C-")) {
						try {
							aux += "Cancion - " + Muflix.getInstance().getCancionById(id).getTitulo() + "; ";
						} catch (CancionIDNoEncontrado e) {
							//e.printStackTrace();
							System.out.println(e);
						}
					}
				}
				if(aux.length() >= 1) {
					System.out.println("Album #" + i+1 + ": Contenido: [" + FuncionesUtiles.removeLastChar(FuncionesUtiles.removeLastChar(aux)) + "].");
				}
			}
		}
	}
	
	/**
	 * Mostrar las playlists y su contenido, creadas por el propio usuario.
	 * Muflix entiende las Playlists como privadas, por esto, se realizan comrpobaciones
	 * entre el ID del usuario desde el que se llama a esta funcion y el ID del autor de la Playlist.
	 * 
	 */
	public void mostrarPlaylists() {
		for(int i=0; i<Muflix.getInstance().getNumPlaylists(); i++) {
			String aux = "";
			String id;
			if(Muflix.getInstance().getPlaylists().get(i).getAutor_id().equals(this.id)) {
				for(int j=0; j<Muflix.getInstance().getPlaylists().get(i).getContenido().size(); j++) {
					id = Muflix.getInstance().getPlaylists().get(i).getContenido().get(j);
					if(id.contains("C-")) {
						try {
							aux += "Cancion - " + Muflix.getInstance().getCancionById(id).getTitulo() + "; ";
						} catch (CancionIDNoEncontrado e) {
							//e.printStackTrace();
							System.out.println(e);
						}
					}
					if(id.contains("A-")) {
						try {
							aux += "Album - " + Muflix.getInstance().getAlbumById(id).getTitulo() + "; ";
						} catch (AlbumIDNoEncontrado e) {
							//e.printStackTrace();
							System.out.println(e);
						}
					}
					if(id.contains("P-")) {
						try {
							aux += "Playlist - " + Muflix.getInstance().getPlaylistById(id).getTitulo() + "; ";
						} catch (PlaylistIDNoEncontrado e) {
							//e.printStackTrace();
							System.out.println(e);
						}
					}
				}
				if(aux.length() >= 1) {
					System.out.println("Playlist #" + i+1 + ": Contenido: [" + FuncionesUtiles.removeLastChar(FuncionesUtiles.removeLastChar(aux)) + "].");
				}
			}
		}
	}
	
	/**
	 * Comprueba si dos objetos Miembro son iguales, basandose en su ID y en su nombre de usuario. 
	 * @param o Objeto a comparar.
	 * @return true si son iguales; false si no lo son.
	 * 
	 */
	@Override
	public boolean equals(Object o) {
		if(o instanceof Miembro) {
			Miembro cmp = (Miembro) o;
			if(cmp.id.equalsIgnoreCase(this.id) || cmp.nombreUsuario.equalsIgnoreCase(this.nombreUsuario)) return true;
		}
		return false;
	}
	
	/**
	 * Imprime el objeto Miembro como un String.
	 * @return String del Miembro. 
	 * 
	 */
	public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return this.id + "|" + this.nombreUsuario + "|" + this.email + "|" + this.password + "|" + this.nombreCompleto + "|" + this.fechaRegistro.format(formatter) + "|" + this.fechaNacimiento.format(formatter) + "|" + this.baneado + "|" + this.premium + "|" + this.totalReproducciones + "|" + this.bloqueado + "|" + this.fechaBloqueo.format(formatter) + "|" + this.fechaPremium.format(formatter);
	}
	
}