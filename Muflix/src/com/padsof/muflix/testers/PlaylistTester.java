package com.padsof.muflix.testers;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.padsof.muflix.core.Muflix;
import com.padsof.muflix.exceptions.AlbumIDNoEncontrado;
import com.padsof.muflix.exceptions.CancionIDNoEncontrado;
import com.padsof.muflix.exceptions.MiembroIDNoEncontrado;
import com.padsof.muflix.exceptions.PlaylistIDNoEncontrado;
import com.padsof.muflix.media.Album;
import com.padsof.muflix.media.Cancion;
import com.padsof.muflix.media.Playlist;
import com.padsof.muflix.usuario.Administrador;
import com.padsof.muflix.usuario.Anonimo;
import com.padsof.muflix.usuario.Miembro;
import com.padsof.muflix.utils.ConstantesGlobales;

public class PlaylistTester {
	
	private Miembro usuario1;
	private Miembro usuario2;
	
	private Anonimo anonimo;
	
	private Administrador admin;
	
	private Playlist playlist;
	private Playlist playlist2;
	
	private Album album;
	
	private Cancion cancion;
	
	private Muflix muflix;
	
	@Before
	public void setUp() throws Exception {
		Muflix.reset();
		muflix = Muflix.getInstance();
		
		anonimo = new Anonimo("INV");
		
		admin = new Administrador("admin@muflix.es", "admin123");
		
		usuario1 = new Miembro("U-5", "Oyente", "a@muflix.es", "1234", "Usuario Oyente", "01/04/2019", "01/01/1999", false, false, 0, false, "01/01/" + ConstantesGlobales.NULL_DATE_YEAR, "01/01/" + ConstantesGlobales.NULL_DATE_YEAR);
		usuario2 = new Miembro("U-6", "Autor", "b@muflix.es", "1234", "Usuario Autor", "01/04/2019", "01/01/2017", false, true, 0, false, "01/01/" + ConstantesGlobales.NULL_DATE_YEAR, "01/01/" + ConstantesGlobales.NULL_DATE_YEAR);
		
		playlist = new Playlist("P-5", "U-5", "PlaylistPrueba", "14/03/2019");
		playlist2 = new Playlist("P-6", "U-5", "PlaylistPrueba2", "15/03/2019");
		
		album = new Album("A-5", "U-6", "AlbumPrueba","14/03/2019");
		
		cancion = new Cancion("C-5", "U-6", "Prueba3", "14/03/2019", "data/canciones/ring.mp3", true, false, false, 10);
		
		muflix.setUsuarioActual(anonimo);
		
		muflix.setAdministrador(admin);
		
		muflix.agregarMiembro(usuario1);
		muflix.agregarMiembro(usuario2);
		
		muflix.agregarPlaylist(playlist);
		muflix.agregarPlaylist(playlist2);
		
		muflix.agregarAlbum(album);
		
		muflix.agregarCancion(cancion);
		
	}
	
	@Test
	public void testPlaylist() {
		
		assertEquals(playlist.getId(), "P-5");
		assertEquals(playlist.getAutor_id(), "U-5");
		assertEquals(playlist.getTitulo(), "PlaylistPrueba");
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		assertEquals(playlist.getFechaCreacion(), LocalDate.parse("14/03/2019", formatter));
	}

	@Test
	public void testAgregarContenido() {
		assertTrue(playlist.agregarContenido(cancion.getId())); //Agregar cancion.
		assertFalse(playlist.agregarContenido(cancion.getId())); //No agregar una cancion si ya esta contenida.
		
		assertTrue(playlist.agregarContenido(album.getId())); //Agregar album.
		assertFalse(playlist.agregarContenido(cancion.getId())); //No agregar un album si ya esta contenido.
		
		assertTrue(playlist.agregarContenido(playlist2.getId())); //Agregar playlist.
		assertFalse(playlist.agregarContenido(playlist2.getId())); //No agregar una playlist si ya esta contenida.
		
		assertFalse(playlist.agregarContenido(playlist.getId())); //No agregarse a si misma.
	}
	
	@Test
	public void testEliminarContenido() {
		assertTrue(playlist.agregarContenido(cancion.getId())); //Agregar cancion.
		assertTrue(playlist.eliminarContenido(cancion.getId())); //Eliminar cancion.
		
		assertTrue(playlist.agregarContenido(album.getId())); //Agregar album.
		assertTrue(playlist.eliminarContenido(album.getId())); //Eliminar album.
		
		assertTrue(playlist.agregarContenido(playlist2.getId())); //Agregar playlist.
		assertTrue(playlist.eliminarContenido(playlist2.getId())); //Eliminar playlist.
		
		assertFalse(playlist.eliminarContenido(playlist2.getId())); //No eliminar contenido inexistente.
		
	}
	
	@Test
	public void testCargarMedia() {
		
		try {
			cancion = muflix.getCancionById(cancion.getId());
		} catch (CancionIDNoEncontrado e3) {
			e3.printStackTrace();
		}
		
		try {
			album = muflix.getAlbumById(album.getId());
		} catch (AlbumIDNoEncontrado e) {
			e.printStackTrace();
		}
		album.agregarCancion(cancion.getId());
		
		Cancion cancion2 = new Cancion("C-6", "U-6", "Prueba6", "14/03/2019", "data/canciones/ring.mp3", true, false, false, 0);
		muflix.agregarCancion(cancion2);
		
		try {
			playlist = muflix.getPlaylistById(playlist.getId());
			playlist2 = muflix.getPlaylistById(playlist2.getId());
		} catch (PlaylistIDNoEncontrado e2) {
			e2.printStackTrace();
		}
		
		playlist2.agregarContenido(cancion2.getId());
		
		playlist.agregarContenido(cancion2.getId());
		playlist.agregarContenido(album.getId());
		playlist.agregarContenido(playlist2.getId());
		
		//************ Anonimo ************//
		muflix.getUsuarioActual().setLimiteAlcanzado(false); //No ha alcanzado el limite de reproduccion.
		
		try {
			assertFalse(playlist.cargarMedia(muflix.getUsuarioActual())); //Anonimo no reproduce playlists.
		} catch (MiembroIDNoEncontrado | CancionIDNoEncontrado | AlbumIDNoEncontrado e1) {
			e1.printStackTrace();
		}
		//********************************//
		
		//************ Miembro ************//
		try {
			assertTrue(playlist.cargarMedia(muflix.getMiembroById("U-5")));
		} catch (MiembroIDNoEncontrado | CancionIDNoEncontrado | AlbumIDNoEncontrado e) {
			e.printStackTrace();
		}
		assertEquals(1, cancion2.getNumReproducciones()); //Incrementar numero de reproducciones.
		assertEquals(10, cancion.getNumReproducciones()); //No tiene reproduccion automatica.
		
		cancion2.setExplicita(true); //Establecer cancion como explicita.
		
		try {
			assertTrue(playlist.cargarMedia(muflix.getMiembroById("U-5"))); //Miembro mayor de edad.
		} catch (MiembroIDNoEncontrado | CancionIDNoEncontrado | AlbumIDNoEncontrado e) {
			e.printStackTrace();
		}
		assertEquals(2, cancion2.getNumReproducciones()); //Reproducir cancion explicita (mayor de edad).
		
		try {
			assertFalse(playlist.cargarMedia(muflix.getMiembroById("U-6"))); //Cargar una playlist ajena.
		} catch (MiembroIDNoEncontrado | CancionIDNoEncontrado | AlbumIDNoEncontrado e) {
			e.printStackTrace();
		}
		
		try {
			muflix.getMiembroById("U-5").setLimiteAlcanzado(true);
			assertFalse(playlist.cargarMedia(muflix.getMiembroById("U-5"))); //Limite de reproduccion alcanzado.
		} catch (MiembroIDNoEncontrado | CancionIDNoEncontrado | AlbumIDNoEncontrado e) {
			e.printStackTrace();
		}
		
		try {
			muflix.getMiembroById("U-5").darPremium(); //Hacer Premium.
			assertTrue(playlist.cargarMedia(muflix.getMiembroById("U-5"))); //Limite de reproduccion alcanzado, pero es Premium.
		} catch (MiembroIDNoEncontrado | CancionIDNoEncontrado | AlbumIDNoEncontrado e) {
			e.printStackTrace();
		}
		
		try {
			muflix.getMiembroById("U-5").activarReproduccionAutomatica(); //Activar reproduccion automatica.
			assertTrue(playlist.cargarMedia(muflix.getMiembroById("U-5")));
		} catch (MiembroIDNoEncontrado | CancionIDNoEncontrado | AlbumIDNoEncontrado e) {
			e.printStackTrace();
		}
		
		//Con la reproduccion automatica, se cargan ambas canciones, album y playlist. Se incrementan sus reproducciones.
		assertEquals(11, cancion.getNumReproducciones());
		assertEquals(5, cancion2.getNumReproducciones());
		
		//*********************************//
		
		//********* Administrador *********//
		try {
			assertTrue(playlist.cargarMedia(muflix.getAdministrador())); //Administrador puede cargar media.
		} catch (MiembroIDNoEncontrado | CancionIDNoEncontrado | AlbumIDNoEncontrado e) {
			e.printStackTrace();
		}
		//*********************************//
	}
	
	@Test
	public void testReproducirMedia() {
		try {
			cancion = muflix.getCancionById(cancion.getId());
		} catch (CancionIDNoEncontrado e3) {
			e3.printStackTrace();
		}
		
		try {
			album = muflix.getAlbumById(album.getId());
		} catch (AlbumIDNoEncontrado e) {
			e.printStackTrace();
		}
		album.agregarCancion(cancion.getId());
		
		Cancion cancion2 = new Cancion("C-6", "U-6", "Prueba6", "14/03/2019", "data/canciones/ring.mp3", true, false, false, 0);
		muflix.agregarCancion(cancion2);
		
		try {
			playlist = muflix.getPlaylistById(playlist.getId());
			playlist2 = muflix.getPlaylistById(playlist2.getId());
		} catch (PlaylistIDNoEncontrado e2) {
			e2.printStackTrace();
		}
		
		playlist2.agregarContenido(cancion2.getId());
		
		playlist.agregarContenido(cancion2.getId());
		playlist.agregarContenido(album.getId());
		playlist.agregarContenido(playlist2.getId());
		
		//************ Anonimo ************//
		muflix.getUsuarioActual().setLimiteAlcanzado(false); //No ha alcanzado el limite de reproduccion.
		
		assertFalse(playlist.reproducirMedia(muflix.getUsuarioActual())); //Anonimo no reproduce playlists.
		//********************************//
		
		//************ Miembro ************//
		try {
			assertTrue(playlist.reproducirMedia(muflix.getMiembroById("U-5")));
		} catch (MiembroIDNoEncontrado e2) {
			e2.printStackTrace();
		}
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion()); //Solo reproduce la primera cancion (No rep. automatica).
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		assertEquals(1, cancion2.getNumReproducciones()); //Incrementar numero de reproducciones.
		assertEquals(10, cancion.getNumReproducciones()); //No tiene reproduccion automatica.
		
		cancion2.setExplicita(true); //Establecer cancion como explicita.
		
		try {
			assertTrue(playlist.reproducirMedia(muflix.getMiembroById("U-5"))); //Miembro mayor de edad.
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion()); //Solo reproduce la primera cancion (No rep. automatica).
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		assertEquals(2, cancion2.getNumReproducciones()); //Reproducir cancion explicita (mayor de edad).
		
		try {
			assertFalse(playlist.reproducirMedia(muflix.getMiembroById("U-6"))); //Reproducir una playlist ajena.
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		
		try {
			muflix.getMiembroById("U-5").setLimiteAlcanzado(true);
			assertFalse(playlist.reproducirMedia(muflix.getMiembroById("U-5"))); //Limite de reproduccion alcanzado.
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		
		try {
			muflix.getMiembroById("U-5").darPremium(); //Hacer Premium.
			assertTrue(playlist.reproducirMedia(muflix.getMiembroById("U-5"))); //Limite de reproduccion alcanzado, pero es Premium.
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion()); //Solo reproduce la primera cancion (No rep. automatica).
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		assertEquals(3, cancion2.getNumReproducciones());
		
		try {
			muflix.getMiembroById("U-5").activarReproduccionAutomatica(); //Activar rep. automatica.
			assertTrue(playlist.reproducirMedia(muflix.getMiembroById("U-5"))); //Limite de reproduccion alcanzado, pero es Premium.
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion()*3);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		assertEquals(11, cancion.getNumReproducciones()); //Se incrementa el numero de reproducciones.
		assertEquals(5, cancion2.getNumReproducciones()); //Se incrementa el numero de reproducciones.
		//*********************************//
		
		//********* Administrador *********//
		assertTrue(playlist.reproducirMedia(muflix.getAdministrador())); //Administrador puede reproducir media.
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion()*3);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		assertEquals(12, cancion.getNumReproducciones()); //Se incrementa el numero de reproducciones.
		assertEquals(7, cancion2.getNumReproducciones()); //Se incrementa el numero de reproducciones.
		//*********************************//
	}
	
	@Test
	public void testEquals() {
		Playlist playlist1 = null;
		try {
			playlist1 = muflix.getPlaylistById(playlist.getId());
		} catch (PlaylistIDNoEncontrado e) {
			e.printStackTrace();
		}
		Playlist playlist2 = new Playlist("P-5", "U-5", "PlaylistPrueba", "14/03/2019");
		
		assertTrue(playlist1.equals(playlist2)); //Mismo ID, mismo titulo.
		
		playlist2 = new Playlist("P-7", "U-5", "PlaylistPrueba", "14/03/2019");
		assertFalse(playlist1.equals(playlist2)); //Mismo titulo, distinto ID.
		
		playlist2 = new Playlist("P-7", "U-5", "PlaylistPrueba222", "14/03/2019");
		assertFalse(playlist1.equals(playlist2)); //Distinto titulo, distinto ID.
	}
}
