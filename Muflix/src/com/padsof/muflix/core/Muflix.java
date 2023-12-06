package com.padsof.muflix.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.padsof.muflix.denuncia.Denuncia;
import com.padsof.muflix.exceptions.AlbumIDNoEncontrado;
import com.padsof.muflix.exceptions.CancionIDNoEncontrado;
import com.padsof.muflix.exceptions.MiembroIDNoEncontrado;
import com.padsof.muflix.exceptions.PlaylistIDNoEncontrado;
import com.padsof.muflix.exceptions.UsernameNoEncontrado;
import com.padsof.muflix.media.*;
import com.padsof.muflix.usuario.*;
import com.padsof.muflix.utils.ConstantesGlobales;
import com.padsof.muflix.utils.FuncionesUtiles;

import pads.musicPlayer.Mp3Player;
import pads.musicPlayer.exceptions.Mp3PlayerException;

//TODO Mejorar funcion CargarMedia Album

/**
 * Clase principal de la aplicacion Muflix. En general, el resto de clases
 * debera comunicarse con esta para llevar a cabo su funcionalidad completa.
 * @author Grupo 4 - PADSOF 2261
 *
 */
public class Muflix {
	
	private int maxReproducciones;
	private int numRepsPremium;
	private int periodoPremium;
	private double precioPremium;
	private int diasBloqueoTemporal;
	
	private Administrador administrador;
	private Usuario usuarioActual;
	
	private List<Miembro> miembros;
	
	private List<Cancion> canciones;
	private List<String> cancionesNoValidadas;
	
	private List<Album> albumes;
	private List<Playlist> playlists;
	private List<Denuncia> denuncias;
	
	private Mp3Player reproductor;

	
	private static Muflix instanciaUnica;
	
	/**
	 * Obtener la instancia unica de la aplicacion (Singleton).
	 * @return instanciaUnica Instancia unica principal de la aplicacion.
	 * 
	 */
	public static Muflix getInstance() {
		if(instanciaUnica == null) {
			instanciaUnica = new Muflix();
		}
		return instanciaUnica;
    }
	
	/**
	 * Funcion para resetear el patron Singleton. Usada en los testers JUnit.
	 * 
	 */
	public static synchronized void reset(){
	    instanciaUnica = new Muflix();
	}
	
	/**
	 * Constructor de la aplicacion, donde se cargan todos los datos y se inicializan los atributos.
	 * 
	 */
	private Muflix() {
		
		this.cargarConfig();
		
		this.administrador = null;
		this.usuarioActual = new Anonimo("INV");
		
		this.denuncias = new ArrayList<Denuncia>();
		this.cargarDenuncias();
		
		this.canciones = new ArrayList<Cancion>();
		this.cargarCanciones();
		
		this.cancionesNoValidadas = new ArrayList<String>();
		this.cargarCancionesNoValidadas();
		
		this.albumes = new ArrayList<Album>();
		this.cargarAlbumes();
		
		this.playlists = new ArrayList<Playlist>();
		this.cargarPlaylists();
		
		this.miembros = new ArrayList<Miembro>();
		this.cargarUsuarios();
		
		this.cargarFollows();
		
		try {
			this.reproductor = new Mp3Player();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Mp3PlayerException e) {
			e.printStackTrace();
		}
		
		this.comprobarDenuncias();
		this.comprobarBloqueos();
		this.comprobarPremium();
	}
	
	/**
	 * Obtener los dias que dura un bloqueo temporal de usuario.
	 * @return Duracion del bloqueo temporal.
	 * 
	 */
	public int getDiasBloqueoTemporal() {
		return this.diasBloqueoTemporal;
	}
	
	/**
	 * Obtener el maximo tiempo que un usuario no Premium puede reproducir.
	 * @return Numero maximo de reproducciones para usuarios no Premium.
	 * 
	 */
	public double getMaxReproducciones() {
		return this.maxReproducciones;
	}
	
	/**
	 * Cambiar los dias que durara un bloqueo temporal.
	 * @param dias Nuevo numero de dias que durara un bloqueo temporal.
	 * 
	 */
	public void setDiasBloqueoTemporal(int dias) {
		if(dias <= 0) return;
		this.diasBloqueoTemporal = dias;
	}
	
	/**
	 * Cambiar el numero maximo de reproducciones de un usuario no Premium.
	 * @param tiempo Nuevo limite de reproduccion.
	 * 
	 */
	public void setMaxReproducciones(int reproducciones) {
		if(reproducciones <= 0) return;
		this.maxReproducciones = reproducciones;
	}
	
	/**
	 * Obtener el numero de reproducciones necesarias para que un usuario pase a Premium.
	 * @return Numero de reproducciones necesarias.
	 * 
	 */
	public int getNumRepsPremium() {
		return this.numRepsPremium;
	}
	
	/**
	 * Establecer el numero de reproducciones necesarias para que un usuario pase a Premium.
	 * @param numRepsPremium Nuevo numero de reproducciones necesarias.
	 * 
	 */
	public void setNumRepsPremium(int numRepsPremium) {
		if(numRepsPremium <= 0) return;
		this.numRepsPremium = numRepsPremium;
	}
	
	/**
	 * Obtener el periodo en dias que dura una suscripcion Premium.
	 * @return Periodo en dias que dura una suscripcion Premium.
	 * 
	 */
	public int getPeriodoPremium() {
		return this.periodoPremium;
	}
	
	/**
	 * Establecer el periodo en dias que dura una suscripcion Premium.
	 * @param periodoPremium Nueva duracion en dias de una suscripcion Premium.
	 * 
	 */
	public void setPeriodoPremium(int periodoPremium) {
		if(periodoPremium <= 0) return;
		this.periodoPremium = periodoPremium;
	}
	
	/**
	 * Obtener el precio de la suscripcion Premium.
	 * @return Precio de la suscripcion Premium.
	 * 
	 */
	public double getPrecioPremium() {
		return this.precioPremium;
	}
	
	/**
	 * Establecer el precio de la suscripcion Premium.
	 * @param precioPremium Nuevo precio para la suscripcion.
	 * 
	 */
	public void setPrecioPremium(double precioPremium) {
		this.precioPremium = precioPremium;
	}
	
	/**
	 * Obtener el administrador cargado en el sistema.
	 * @return Administrador del sistema
	 * 
	 */
	public Usuario getAdministrador() {
		return this.administrador;
	}
	
	/**
	 * Obtener el usuario que esta usando la aplicacion actualmente.
	 * @return Usuario actual.
	 * 
	 */
	public Usuario getUsuarioActual() {
		return this.usuarioActual;
	}
	
	/**
	 * Establecer el usuario que esta usando la aplicacion en el momento actual.
	 * @param u Usuario actual.
	 * 
	 */
	public void setUsuarioActual(Usuario u) {
		this.usuarioActual = u;
	}
	
	/**
	 * Establecer el administrador del sistema.
	 * @param adm Administrador del sistema.
	 * 
	 */
	public void setAdministrador(Administrador adm) {
		this.administrador = adm;
	}
	
	/**
	 * Obtener la lista de canciones cargadas en la aplicacion.
	 * @return Lista inmodificable de canciones de la aplicacion.
	 * 
	 */
	public List<Cancion> getCanciones() {
		return Collections.unmodifiableList(this.canciones);
	}
	
	/**
	 * Obtener la lista de canciones no validadas cargadas en la aplicacion.
	 * @return Lista inmodificable de canciones no validadas de la aplicacion.
	 * 
	 */
	public List<String> getCancionesNoValidadas() {
		return Collections.unmodifiableList(this.cancionesNoValidadas);
	}
	
	/**
	 * Obtener la lista de playlists cargadas en la aplicacion.
	 * @return Lista inmodificable de playlists de la aplicacion.
	 * 
	 */
	public List<Playlist> getPlaylists() {
		return Collections.unmodifiableList(this.playlists);
	}
	
	/**
	 * Obtener la lista de albumes cargados en la aplicacion.
	 * @return Lista inmodificable de albumes de la aplicacion.
	 * 
	 */
	public List<Album> getAlbumes() {
		return Collections.unmodifiableList(this.albumes);
	}
	
	/**
	 * Obtener la lista de denuncias cargadas en la aplicacion.
	 * @return Lista inmodificable de denuncias de la aplicacion.
	 * 
	 */
	public List<Denuncia> getDenuncias(){
		return Collections.unmodifiableList(this.denuncias);
	}
	
	/**
	 * Obtener el numero de denuncias cargadas en la aplicacion.
	 * @return Numero de denuncias de la aplicacion.
	 * 
	 */
	public int getNumDenuncias() {
		return this.denuncias.size();
	}
	
	/**
	 * Obtener la lista de miembros cargados en la aplicacion.
	 * @return Lista inmodificable de miembros de la aplicacion.
	 * 
	 */
	public List<Miembro> getMiembros(){
		return Collections.unmodifiableList(this.miembros);
	}
	
	/**
	 * Obtener el numero de miembros cargados en la aplicacion.
	 * @return Numero de miembros de la aplicacion.
	 * 
	 */
	public int getNumMiembros() {
		return this.miembros.size();
	}
	
	/**
	 * Obtener el numero de canciones cargadas en la aplicacion.
	 * @return Numero de canciones de la aplicacion.
	 * 
	 */
	public int getNumCanciones() {
		return this.canciones.size();
	}
	
	/**
	 * Obtener el numero de canciones no validadas cargadas en la aplicacion.
	 * @return Numero de canciones no validadas de la aplicacion.
	 * 
	 */
	public int getNumCancionesNoValidadas() {
		return this.cancionesNoValidadas.size();
	}
	
	/**
	 * Obtener el numero de albumes cargados en la aplicacion.
	 * @return Numero de albumes de la aplicacion.
	 * 
	 */
	public int getNumAlbumes() {
		return this.albumes.size();
	}
	
	/**
	 * Obtener el numero de playlists cargadas en la aplicacion.
	 * @return Numero de playlists de la aplicacion.
	 * 
	 */
	public int getNumPlaylists() {
		return this.playlists.size();
	}
	
	/**
	 * Obtener el reproductor MP3 de la aplicacion.
	 * @return Reproductor MP3 cargado en la aplicacion.
	 * 
	 */
	public Mp3Player getReproductor() {
		return this.reproductor;
	}
	
	/**
	 * Agregar un miembro al sistema, comprobando que no exista ya.
	 * @param m Miembro a agregar en el sistema.
	 * @return true si se ha podido agregar; false si no se ha podido agregar.
	 * 
	 */
	public boolean agregarMiembro(Miembro m) {
		if(!this.miembros.contains(m)) {
			this.miembros.add(m);
			return true;
		}
		//System.out.println("ERROR: El usuario " + m.getNombreUsuario() + " ya se encuentra registrado en el sistema."); /* Debugger */
		return false;
	}
	
	/**
	 * Agregar una cancion no validada al sistema, comprobando que no exista ya.
	 * @param c Cancion a agregar en el sistema.
	 * @return true si se ha podido agregar; false si no se ha podido agregar.
	 * 
	 */
	public boolean agregarCancionNoValida(String c) {
		if(!this.cancionesNoValidadas.contains(c)) {
			this.cancionesNoValidadas.add(c);
			return true;
		}
		return false;
	}
	
	/**
	 * Eliminar una cancion no validada del sistema, comprobando que exista en el.
	 * @param c Cancion a eliminar del sistema.
	 * @return true si se ha podido eliminar; false si no se ha podido eliminar.
	 * 
	 */
	public boolean eliminarCancionNoValida(String c) {
		if(this.cancionesNoValidadas.contains(c)) {
			this.cancionesNoValidadas.remove(c);
			return true;
		}
		return false;
	}
	
	/**
	 * Agregar una cancion al sistema, comprobando que no exista ya.
	 * Si no esta validada, la agrega a la lista de canciones no validadas.
	 * @param c Cancion a agregar en el sistema.
	 * @return true si se ha podido agregar; false si no se ha podido agregar.
	 * 
	 */
	public boolean agregarCancion(Cancion c) {
		if(!this.canciones.contains(c)) {
			if(!this.cancionesNoValidadas.contains(c.getId()) && c.esValida() == false) {
				this.cancionesNoValidadas.add(c.getId());
			}
			this.canciones.add(c);
			return true;
		}
		return false;
	}
	
	/**
	 * Eliminar una cancion del sistema, comprobando que exista en el.
	 * Ademas, elimina todas las posibles referencias a esta cancion.
	 * @param c Cancion a eliminar del sistema.
	 * @return true si se ha podido eliminar; false si no se ha podido eliminar.
	 * 
	 */
	public boolean eliminarCancion(Cancion c) {
		if(this.canciones.contains(c)) {
			for(int i=0; i<this.albumes.size(); i++) {
				if(this.albumes.get(i).getCanciones().contains(c.getId())){
					this.albumes.get(i).eliminarCancion(c.getId());
				}
			}
			for(int i=0; i<this.playlists.size(); i++) {
				if(this.playlists.get(i).getContenido().contains(c.getId())){
					this.playlists.get(i).eliminarContenido(c.getId());
				}
			}
			
			for(int i=0; i<this.denuncias.size(); i++) {
				if(this.denuncias.get(i).getCancion().equalsIgnoreCase(c.getId())) {
					this.denuncias.remove(i);
				}
			}
			
			 
			try {
				this.getMiembroById(c.getAutor_id()).restarTotalReproducciones(c.getNumReproducciones());
			} catch (MiembroIDNoEncontrado e) {
				//e.printStackTrace();
				System.out.println(e);
				return false;
			}
			this.canciones.remove(c);
			return true;
		}
		return false;
	}
	
	/**
	 * Agregar un album al sistema, comprobando que no exista ya.
	 * @param a Album a agregar en el sistema.
	 * @return true si se ha podido agregar; false si no se ha podido agregar.
	 * 
	 */
	public boolean agregarAlbum(Album a) {
		if(!this.albumes.contains(a)) {
			this.albumes.add(a);
			return true;
		}
		return false;
	}
	
	/**
	 * Eliminar un album del sistema, comprobando que exista en el. Tambien se eliminan las canciones que contiene.
	 * @param a Album a eliminar del sistema.
	 * @return true si se ha podido eliminar; false si no se ha podido eliminar.
	 * 
	 */
	public boolean eliminarAlbum(Album a) {
		if(this.albumes.contains(a)) {
			for(int i=0; i<a.getNumCanciones(); i++) {
				try {
					this.eliminarCancion(this.getCancionById(a.getCanciones().get(i)));
				} catch (CancionIDNoEncontrado e) {
					e.printStackTrace();
					System.out.println(e);
				}
			}
			for(int i=0; i<this.playlists.size(); i++) {
				if(this.playlists.get(i).getContenido().contains(a.getId())){
					this.playlists.get(i).eliminarContenido(a.getId());
				}
			}
			this.albumes.remove(a);
			return true;
		}
		return false;
	}
	
	/**
	 * Agregar una playlist al sistema, comprobando que no exista ya.
	 * @param p Playlist a agregar en el sistema.
	 * @return true si se ha podido agregar; false si no se ha podido agregar.
	 * 
	 */
	public boolean agregarPlaylist(Playlist p) {
		if(!this.playlists.contains(p)) {
			this.playlists.add(p);
			return true;
		}
		return false;
	}
	
	/**
	 * Eliminar una playlist del sistema, comprobando que exista en el.
	 * @param p Playlist a eliminar del sistema.
	 * @return true si se ha podido eliminar; false si no se ha podido eliminar.
	 * 
	 */
	public boolean eliminarPlaylist(Playlist p) {
		if(this.playlists.contains(p)) {
			this.playlists.remove(p);
			return true;
		}
		return false;
	}
	
	/**
	 * Agregar una denuncia al sistema, comprobando que no exista ya.
	 * @param d Denuncia a agregar en el sistema.
	 * @return true si se ha podido agregar; false si no se ha podido agregar.
	 * 
	 */
	public boolean agregarDenuncia(Denuncia d) {
		if(!this.denuncias.contains(d)) {
			this.denuncias.add(d);
			return true;
		}
		return false;
	}
	
	/**
	 * Eliminar una denuncia del sistema, comprobando que exista en el.
	 * @param d Denuncia a eliminar del sistema.
	 * @return true si se ha podido eliminar; false si no se ha podido eliminar.
	 * 
	 */
	public boolean eliminarDenuncia(Denuncia d) {
		if(this.denuncias.contains(d)) {
			this.denuncias.remove(d);
			return true;
		}
		return false;
	}
	
	/**
	 * Obtener un objeto Denuncia del sistema a partir de su ID.
	 * @param id ID del objeto a obtener.
	 * @return Objeto obtenido o null si no se encuentra en el sistema.
	 * 
	 */
	public Denuncia getDenunciaById(String id) {
		for(int i=0; i<this.denuncias.size(); i++) {
			if(this.denuncias.get(i).getId().equalsIgnoreCase(id)) return this.denuncias.get(i);
		}
		return null;
	}
	
	/**
	 * Obtener un objeto Miembro del sistema a partir de su ID.
	 * @param id ID del objeto a obtener.
	 * @throws MiembroIDNoEncontrado si no se encuentra a un Miembro con el ID dado.
	 * @return Objeto obtenido o null si no se encuentra en el sistema.
	 * 
	 */
	public Miembro getMiembroById(String id) throws MiembroIDNoEncontrado {
		boolean flag = true;
		
		for(int i=0; i<this.miembros.size(); i++) {
			if(this.miembros.get(i).getId().equalsIgnoreCase(id)) {
				flag = true;
				return this.miembros.get(i);
			}
		}
		
		if(flag == false) throw new MiembroIDNoEncontrado(id);
		return null;
	}
	
	/**
	 * Obtener un objeto Miembro del sistema a partir de su nombre de usuario.
	 * @param nombre Nombre de usuario del objeto a obtener.
	 * @throws UsernameNoEncontrado si no se encuentra a un Miembro con el nombre de usuario dado.
	 * @return Objeto obtenido o null si no se encuentra en el sistema.
	 * 
	 */
	public Miembro getMiembroByNombre(String nombre) throws UsernameNoEncontrado {
		boolean flag = false;
		
		for(int i=0; i<this.miembros.size(); i++) {
			if(this.miembros.get(i).getNombreUsuario().equalsIgnoreCase(nombre)) {
				flag = true;
				return this.miembros.get(i);
			}
		}
		
		if(flag == false) throw new UsernameNoEncontrado(nombre);
		return null;
	}
	
	/**
	 * Comprobar si un nombre de usuario ya esta registrado en el sistema.
	 * @param nombre Nombre de usuario a comprobar.
	 * @return true si el usuario ya existe en el sistema; false si no existe en el sistema.
	 * 
	 */
	public boolean checkNombreUsuario(String nombre) {
		for(int i=0; i<this.miembros.size(); i++) {
			if(this.miembros.get(i).getNombreUsuario().equalsIgnoreCase(nombre)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Obtener un objeto Album del sistema a partir de su ID.
	 * @param id ID del objeto a obtener.
	 * @throws AlbumIDNoEncontrado si no encuentra el ID del album en el sistema.
	 * @return Objeto obtenido o null si no se encuentra en el sistema.
	 * 
	 */
	public Album getAlbumById(String id) throws AlbumIDNoEncontrado {
		boolean flag = false;
		
		for(int i=0; i<this.albumes.size(); i++) {
			if(this.albumes.get(i).getId().equalsIgnoreCase(id)) {
				flag = true;
				return this.albumes.get(i);
			}
		}
		
		if(flag == false) throw new AlbumIDNoEncontrado(id);
		return null;
	}
	
	/**
	 * Obtener un objeto Playlist del sistema a partir de su ID.
	 * @param id ID del objeto a obtener.
	 * @throws PlayListIDNoEncontrado si no se encuentra el ID de la playlist en el sistema.
	 * @return Objeto obtenido o null si no se encuentra en el sistema.
	 * 
	 */
	public Playlist getPlaylistById(String id) throws PlaylistIDNoEncontrado {
		boolean flag = false;
		
		for(int i=0; i<this.playlists.size(); i++) {
			if(this.playlists.get(i).getId().equalsIgnoreCase(id)) {
				flag = true;
				return this.playlists.get(i);
			}
		}
		
		if(flag == false) throw new PlaylistIDNoEncontrado(id);
		return null;
	}
	
	/**
	 * Obtener un objeto Cancion del sistema a partir de su ID.
	 * @param id ID del objeto a obtener.
	 * @throws CancionIDNoEncontrado si no se encuentra el ID de la cancion en el sistema.
	 * @return Objeto obtenido o null si no se encuentra en el sistema.
	 * 
	 */
	public Cancion getCancionById(String id) throws CancionIDNoEncontrado {
		boolean flag = false;
		
		for(int i=0; i<this.canciones.size(); i++) {
			if(this.canciones.get(i).getId().equalsIgnoreCase(id)) {
				flag = true;
				return this.canciones.get(i);
			}
		}
		
		if(flag == false) throw new CancionIDNoEncontrado(id);
		return null;
	}
	
	/**
	 * Obtener un objeto Cancion del sistema a partir de su titulo.
	 * @param titulo Titulo del objeto a obtener.
	 * @return Objeto obtenido o null si no se encuentra en el sistema.
	 * 
	 */
	public Cancion getCancionByTitulo(String titulo) {
		for(int i=0; i<this.canciones.size(); i++) {
			if(this.canciones.get(i).getTitulo().equalsIgnoreCase(titulo)) return this.canciones.get(i);
		}
		return null;
	}
	
	/**
	 * Carga los objetos Miembro en las listas del sistema leyendo los parametros de su constructor del archivo dado como base de datos.
	 * Se asume que en la base de datos debe existir por defecto un administrador que sera acrgado como administrador del sistema.
	 * 
	 */
	public void cargarUsuarios(){
		FileReader fr = null;
		
		try {
			fr = new FileReader(ConstantesGlobales.DATA_DIRECTORY + ConstantesGlobales.MEMBER_DB);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			System.out.println(ConstantesGlobales.ERROR_PREFIX + "No se puede acceder al archivo.");
			return;
		}
		
		BufferedReader br = new BufferedReader(fr);
		String linea;
		
		try {
			while((linea = br.readLine()) != null){
			    //System.out.println(linea); /* Debugger */
			    String[] buffer = linea.split("\\|");
			    //System.out.println(buffer[0]); /* Debugger */
			    if(buffer[0].equalsIgnoreCase("ADM")) {
			    	//System.out.println("ADMIN CARGADO"); /* Debugger */
			    	this.administrador = new Administrador(buffer[2], buffer[3]);
			    }
			    else {
			    	//System.out.println("USER CARGADO"); /* Debugger */
			    	this.miembros.add(new Miembro(buffer[0], buffer[1], buffer[2], buffer[3], buffer[4], buffer[5], buffer[6], Boolean.parseBoolean(buffer[7]), Boolean.parseBoolean(buffer[8]), Integer.parseInt(buffer[9]), Boolean.parseBoolean(buffer[10]), buffer[11], buffer[12]));
			    }		    
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Carga las relaciones de seguimiento de los usuarios del sistema. Poblando las respectivas listas de los objetos Miembro.
	 * 
	 */
	public void cargarFollows(){
		FileReader fr = null;
		
		try {
			fr = new FileReader(ConstantesGlobales.DATA_DIRECTORY + ConstantesGlobales.FOLLOW_DB);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			System.out.println(ConstantesGlobales.ERROR_PREFIX + "No se puede acceder al archivo.");
			return;
		}
		
		BufferedReader br = new BufferedReader(fr);
		String linea;
		
		try {
			while((linea = br.readLine()) != null){
			    //System.out.println(linea); /* Debugger */
			    String[] buffer = linea.split("\\|");
			    String[] aux = new String[buffer.length-1];
			    
			    for(int i=1; i<buffer.length; i++) {
			    	aux[i-1] = buffer[i];
			    }
			    
			    for(int i=0; i<this.miembros.size(); i++) {
			    	if(this.miembros.get(i).getId().equalsIgnoreCase(buffer[0])) {
			    		for(int j=0; j<aux.length; j++) {
			    			this.miembros.get(i).agregarSeguido(aux[j]);
			    			try {
								this.getMiembroById(aux[j]).agregarSeguidor(this.miembros.get(i).getId());
							} catch (MiembroIDNoEncontrado e) {
								//e.printStackTrace();
								try {
									br.close();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								System.out.println(e);
								return;
							}
			    		}
			    	}
			    }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Carga los objetos Cancion en las listas del sistema leyendo los parametros de su constructor del archivo dado como base de datos.
	 * 
	 */
	public void cargarCanciones(){
		FileReader fr = null;
		
		try {
			fr = new FileReader(ConstantesGlobales.DATA_DIRECTORY + ConstantesGlobales.SONG_DB);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			System.out.println(ConstantesGlobales.ERROR_PREFIX + "No se puede acceder al archivo.");
			return;
		}
		
		BufferedReader br = new BufferedReader(fr);
		String linea;
		
		try {
			while((linea = br.readLine()) != null){
			    //System.out.println(linea); /* Debugger */
			    String[] buffer = linea.split("\\|");
			    
			    this.canciones.add(new Cancion(buffer[0], buffer[1], buffer[2], buffer[3], buffer[4], Boolean.parseBoolean(buffer[5]), Boolean.parseBoolean(buffer[6]), Boolean.parseBoolean(buffer[7]), Integer.parseInt(buffer[8])));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Carga los objetos Album en las listas del sistema leyendo los parametros de su constructor del archivo dado como base de datos.
	 * 
	 */
	public void cargarAlbumes(){
		FileReader fr = null;
		
		try {
			fr = new FileReader(ConstantesGlobales.DATA_DIRECTORY + ConstantesGlobales.ALBUM_DB);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			System.out.println(ConstantesGlobales.ERROR_PREFIX + "No se puede acceder al archivo.");
			return;
		}
		
		BufferedReader br = new BufferedReader(fr);
		String linea;
		
		try {
			while((linea = br.readLine()) != null){
			    //System.out.println(linea); /* Debugger */
			    String[] buffer = linea.split("\\|");
			    String[] aux = new String[buffer.length-4];
			    
			    for(int i=4; i<buffer.length; i++) {
			    	aux[i-4] = buffer[i];
			    }
			    
			    this.albumes.add(new Album(buffer[0], buffer[1], buffer[2], buffer[3]));
			    for(int i=0; i<aux.length; i++) {
			    	try {
						this.getAlbumById(buffer[0]).agregarCancion(aux[i]);
					} catch (AlbumIDNoEncontrado e) {
						//e.printStackTrace();
						System.out.println(e);
					}
			    }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Carga los objetos Denuncia en las listas del sistema leyendo los parametros de su constructor del archivo dado como base de datos.
	 * 
	 */
	public void cargarDenuncias(){
		FileReader fr = null;
		
		try {
			fr = new FileReader(ConstantesGlobales.DATA_DIRECTORY + ConstantesGlobales.REPORT_DB);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			System.out.println(ConstantesGlobales.ERROR_PREFIX + "No se puede acceder al archivo.");
			return;
		}
		
		BufferedReader br = new BufferedReader(fr);
		String linea;
		
		try {
			while((linea = br.readLine()) != null){
			    //System.out.println(linea); /* Debugger */
			    String[] buffer = linea.split("\\|");
			    
			    this.denuncias.add(new Denuncia(buffer[0], buffer[1], buffer[2], buffer[3], buffer[4], buffer[5], Boolean.parseBoolean(buffer[6])));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Carga los objetos Playlist en las listas del sistema leyendo los parametros de su constructor del archivo dado como base de datos.
	 * 
	 */
	public void cargarPlaylists(){
		FileReader fr = null;
		
		try {
			fr = new FileReader(ConstantesGlobales.DATA_DIRECTORY + ConstantesGlobales.PLAYLIST_DB);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			System.out.println(ConstantesGlobales.ERROR_PREFIX + "No se puede acceder al archivo.");
			return;
		}
		
		BufferedReader br = new BufferedReader(fr);
		String linea;
		
		try {
			while((linea = br.readLine()) != null){
			    //System.out.println(linea); /* Debugger */
			    String[] buffer = linea.split("\\|");
			    String[] aux = new String[buffer.length-4];
			    
			    for(int i=4; i<buffer.length; i++) {
			    	aux[i-4] = buffer[i];
			    }
			    
			    this.playlists.add(new Playlist(buffer[0], buffer[1], buffer[2], buffer[3]));
			    
			    for(int i=0; i<aux.length; i++) {
			    	try {
						this.getPlaylistById(buffer[0]).agregarContenido(aux[i]);
					} catch (PlaylistIDNoEncontrado e) {
						//e.printStackTrace();
						System.out.println(e);
					}
			    }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Carga las canciones no validadas como String ID en la lista del sistema.
	 * Ademas, si una cancion no ha sido validada en el periodo establecido como constante global, es eliminada.
	 * 
	 */
	public void cargarCancionesNoValidadas() {
		for(int i=0; i<this.canciones.size(); i++) {
			if(!this.canciones.get(i).esValida()) {
				if(ChronoUnit.DAYS.between(LocalDate.now(), this.canciones.get(i).getFechaCreacion()) >= ConstantesGlobales.VALIDATION_DAYS) {
					this.eliminarCancion(this.canciones.get(i));
				} else {
					this.cancionesNoValidadas.add(this.canciones.get(i).getId());
				}
				
			}
		}
	}
	
	/**
	 * Carga la configuracion del sistema a partir de su fichero, dando valor a los distintos atributos de la clase.
	 * 
	 */
	public void cargarConfig() {
		FileReader fr = null;
		
		try {
			fr = new FileReader(ConstantesGlobales.CONFIG_DIRECTORY + ConstantesGlobales.CONFIG_FILE);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			System.out.println(ConstantesGlobales.ERROR_PREFIX + "No se puede acceder al archivo.");
			return;
		}
		
		BufferedReader br = new BufferedReader(fr);
		String linea;
		
		try {
			while((linea = br.readLine()) != null){
			    //System.out.println(linea); /* Debugger */
			    String[] buffer = linea.split("	");
			    
			    switch(buffer[0]) {
			    case "maxReproducciones":
			    	this.maxReproducciones = Integer.parseInt(buffer[1]);
			    	break;
			    case "numRepsPremium":
			    	this.numRepsPremium = Integer.parseInt(buffer[1]);
			    	break;
			    case "periodoPremium":
			    	this.periodoPremium = Integer.parseInt(buffer[1]);
			    	break;
			    case "diasBloqueoTemporal":
			    	this.diasBloqueoTemporal = Integer.parseInt(buffer[1]);
			    	break;
			    case "precioPremium":
			    	this.precioPremium = Double.parseDouble(buffer[1]);
			    	break;
			    default:
			    	break;
			    } 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Guarda los objetos Usuario cargados en la memoria del sistema en el archivo base de datos con el formato correspondiente.
	 * 
	 */
	public void guardarUsuarios(){
	    File fileName = new File(ConstantesGlobales.DATA_DIRECTORY + ConstantesGlobales.MEMBER_DB); 

	    try {
	        FileWriter fw = new FileWriter(fileName);
	        BufferedWriter output = new BufferedWriter(fw);
	        
	        output.write(this.administrador.toString());
            output.newLine();
	        
	        for (int i = 0; i < this.miembros.size(); i++){
	            output.write(this.miembros.get(i).toString());
	            output.newLine();
	        }

	        output.close();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Guarda los objetos Cancion cargados en la memoria del sistema en el archivo base de datos con el formato correspondiente.
	 * 
	 */
	public void guardarCanciones(){
	    File fileName = new File(ConstantesGlobales.DATA_DIRECTORY + ConstantesGlobales.SONG_DB); 

	    try {
	        FileWriter fw = new FileWriter(fileName);
	        BufferedWriter output = new BufferedWriter(fw);

	        for (int i = 0; i < this.canciones.size(); i++){
	            output.write(this.canciones.get(i).toString());
	            output.newLine();
	        }

	        output.close();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Guarda los objetos Album cargados en la memoria del sistema en el archivo base de datos con el formato correspondiente.
	 * 
	 */
	public void guardarAlbumes(){
	    File fileName = new File(ConstantesGlobales.DATA_DIRECTORY + ConstantesGlobales.ALBUM_DB); 

	    try {
	        FileWriter fw = new FileWriter(fileName);
	        BufferedWriter output = new BufferedWriter(fw);

	        for (int i = 0; i < this.albumes.size(); i++){
	            output.write(this.albumes.get(i).toString());
	            output.newLine();
	        }

	        output.close();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Guarda los objetos Playlist cargados en la memoria del sistema en el archivo base de datos con el formato correspondiente.
	 * 
	 */
	public void guardarPlaylists(){
	    File fileName = new File(ConstantesGlobales.DATA_DIRECTORY + ConstantesGlobales.PLAYLIST_DB); 

	    try {
	        FileWriter fw = new FileWriter(fileName);
	        BufferedWriter output = new BufferedWriter(fw);

	        for (int i = 0; i < this.playlists.size(); i++){
	            output.write(this.playlists.get(i).toString());
	            output.newLine();
	        }

	        output.close();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Guarda los objetos Denuncia cargados en la memoria del sistema en el archivo base de datos con el formato correspondiente.
	 * 
	 */
	public void guardarDenuncias(){
	    File fileName = new File(ConstantesGlobales.DATA_DIRECTORY + ConstantesGlobales.REPORT_DB); 

	    try {
	        FileWriter fw = new FileWriter(fileName);
	        BufferedWriter output = new BufferedWriter(fw);

	        for (int i = 0; i < this.denuncias.size(); i++){
	            output.write(this.denuncias.get(i).toString());
	            output.newLine();
	        }

	        output.close();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Guarda las relaciones de seguimiento cargadas en cada objeto Miembro del sistema en el fichero base de datos, con el formato correspondiente.
	 * 
	 */
	public void guardarFollows(){
	    File fileName = new File(ConstantesGlobales.DATA_DIRECTORY + ConstantesGlobales.FOLLOW_DB); 

	    try {
	        FileWriter fw = new FileWriter(fileName);
	        BufferedWriter output = new BufferedWriter(fw);

	        for (int i = 0; i < this.miembros.size(); i++){
	        	if(!this.miembros.get(i).getSeguidos().isEmpty()) {
	        		String aux = "";
		        	for(int j=0; j<this.miembros.get(i).getNumSeguidos(); j++) {
		        		aux += this.miembros.get(i).getSeguidos().get(j) + "|";
		        	}
		        	
		            output.write(this.miembros.get(i).getId() + "|" + FuncionesUtiles.removeLastChar(aux));
		            output.newLine();
	        	}
	        }

	        output.close();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Guarda la configuracion actual del sistema en su fichero base de datos, con el formato correspondiente.
	 * 
	 */
	public void guardarConfig() {
		File fileName = new File(ConstantesGlobales.DATA_DIRECTORY + ConstantesGlobales.PLAYLIST_DB); 

	    try {
	        FileWriter fw = new FileWriter(fileName);
	        BufferedWriter output = new BufferedWriter(fw);

            output.write("maxReproducciones	" + this.maxReproducciones);
            output.newLine();
            
            output.write("numRepsPremium	" + this.numRepsPremium);
            output.newLine();
            
            output.write("periodoPremium	" + this.periodoPremium);
            output.newLine();
            
            output.write("diasBloqueoTemporal	" + this.diasBloqueoTemporal);
            output.newLine();
            
            output.write("precioPremium	" + this.precioPremium);
            output.newLine();

	        output.close();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Llama a todas las funciones de guardado del sistema.
	 * 
	 */
	public void guardarDatos() {
		this.guardarAlbumes();
		this.guardarCanciones();
		this.guardarConfig();
		this.guardarDenuncias();
		this.guardarFollows();
		this.guardarPlaylists();
		this.guardarUsuarios();
	}
	
	/**
	 * Comprueba las denuncias cargadas en el sistema y oculta las canciones afectadas en espera de que la disputa se resuelva.
	 * Tambien, elimina las denuncias que ya han sido revisadas por un administrador.
	 * 
	 */
	public void comprobarDenuncias() {
		for(int i=0; i<this.denuncias.size(); i++) {
			if(this.denuncias.get(i).esRevisada()) {
				this.denuncias.remove(i);
				i--;
			} else {
				for(int j=0; i<this.canciones.size(); j++) {
					if(this.denuncias.get(i).getCancion().equalsIgnoreCase(this.canciones.get(j).getId())) {
						this.canciones.get(j).bloquear();
					}
				}
			}
		}
	}
	
	/**
	 * Comproba si algun usuario del sistema ha obtenido las reproducciones necesarias para ser Premium y actualiza su suscripcion.
	 * Ademas, comprueba si el periodo de Premium de algun usuario del sistema ha finalizado para retirarle la suscripcion.
	 * 
	 */
	public void comprobarPremium() {
		for(int i=0; i<this.miembros.size(); i++) {
			if(this.miembros.get(i).getTotalReproducciones() >= this.numRepsPremium) {
				this.miembros.get(i).darPremium();
			}
			
			if(this.miembros.get(i).getFechaPremium().getYear() != ConstantesGlobales.NULL_DATE_YEAR && ChronoUnit.DAYS.between(LocalDate.now(), this.miembros.get(i).getFechaPremium()) >= this.periodoPremium) {
				this.miembros.get(i).quitarPremium();
			}
		}
	}
	
	/**
	 * Comprueba todos los usuarios cargados en el sistema por si su bloqueo temporal ha finalizado y, si es asi, los desbloquea.
	 * 
	 */
	public void comprobarBloqueos() {
		for(int i=0; i<this.miembros.size(); i++) {
			if(this.miembros.get(i).getFechaBloqueo().getYear() != ConstantesGlobales.NULL_DATE_YEAR && ChronoUnit.DAYS.between(LocalDate.now(), this.miembros.get(i).getFechaBloqueo()) >= this.diasBloqueoTemporal) {
				this.miembros.get(i).desbloquear();
			}
		}
	}
	
	/**
	 * Obtener el fragmento entero del siguiente ID disponible para asignar a un Miembro.
	 * @return Fragmento entero del ID.
	 * 
	 */
	public int getNextUserID() {
		int id = 0;
		String[] buffer;
		for(int i=0; i<this.miembros.size(); i++) {
			buffer = this.miembros.get(i).getId().split("\\-");
			if(Integer.parseInt(buffer[1]) > id) id = Integer.parseInt(buffer[1]);
		}
		return (id + 1);
	}
	
	/**
	 * Obtener el fragmento entero del siguiente ID disponible para asignar a una Cancion.
	 * @return Fragmento entero del ID.
	 * 
	 */
	public int getNextSongID() {
		int id = 0;
		String[] buffer;
		for(int i=0; i<this.canciones.size(); i++) {
			buffer = this.canciones.get(i).getId().split("-");
			if(Integer.parseInt(buffer[1]) > id) id = Integer.parseInt(buffer[1]);
		}
		return (id + 1);
	}
	
	/**
	 * Obtener el fragmento entero del siguiente ID disponible para asignar a un Album.
	 * @return Fragmento entero del ID.
	 * 
	 */
	public int getNextAlbumID() {
		int id = 0;
		String[] buffer;
		for(int i=0; i<this.albumes.size(); i++) {
			buffer = this.albumes.get(i).getId().split("-");
			if(Integer.parseInt(buffer[1]) > id) id = Integer.parseInt(buffer[1]);
		}
		return (id + 1);
	}
	
	/**
	 * Obtener el fragmento entero del siguiente ID disponible para asignar a una Playlist.
	 * @return Fragmento entero del ID.
	 * 
	 */
	public int getNextPlaylistID() {
		int id = 0;
		String[] buffer;
		for(int i=0; i<this.playlists.size(); i++) {
			buffer = this.playlists.get(i).getId().split("-");
			if(Integer.parseInt(buffer[1]) > id) id = Integer.parseInt(buffer[1]);
		}
		return (id + 1);
	}
	
	/**
	 * Obtener el fragmento entero del siguiente ID disponible para asignar a una Denuncia.
	 * @return Fragmento entero del ID.
	 * 
	 */
	public int getNextReportID() {
		int id = 0;
		String[] buffer;
		for(int i=0; i<this.denuncias.size(); i++) {
			buffer = this.denuncias.get(i).getId().split("-");
			if(Integer.parseInt(buffer[1]) > id) id = Integer.parseInt(buffer[1]);
		}
		return (id + 1);
	}
	
	/**
	 * Crear un reproductor nuevo para limpiarlo y vaciar todos los paths de su lista.
	 * 
	 */
	public void limpiarReproductor() {
		try {
			this.reproductor = new Mp3Player();
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			System.out.println(ConstantesGlobales.ERROR_PREFIX + "No se puede acceder al archivo.");
		} catch (Mp3PlayerException e) {
			//e.printStackTrace();
			System.out.println(ConstantesGlobales.ERROR_PREFIX + "Ha ocurrido un error con el reproductor MP3.");
		}
	}
}
