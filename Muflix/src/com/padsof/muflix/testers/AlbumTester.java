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
import com.padsof.muflix.media.Album;
import com.padsof.muflix.media.Cancion;
import com.padsof.muflix.usuario.Administrador;
import com.padsof.muflix.usuario.Anonimo;
import com.padsof.muflix.usuario.Miembro;
import com.padsof.muflix.utils.ConstantesGlobales;

public class AlbumTester {
	
	private Miembro usuario1;
	private Miembro usuario2;
	
	private Anonimo anonimo;
	
	private Administrador admin;
	
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
		
		album = new Album("A-5", "U-6", "AlbumPrueba","14/03/2019");
		
		cancion = new Cancion("C-5", "U-6", "Prueba3", "14/03/2019", "data/canciones/ring.mp3", true, false, false, 10);
		
		muflix.setUsuarioActual(anonimo);
		
		muflix.setAdministrador(admin);
		
		muflix.agregarMiembro(usuario1);
		muflix.agregarMiembro(usuario2);
		
		muflix.agregarAlbum(album);
		
		muflix.agregarCancion(cancion);
		
	}
	
	@Test
	public void testAlbum() {
		
		assertEquals(album.getId(), "A-5");
		assertEquals(album.getAutor_id(), "U-6");
		assertEquals(album.getTitulo(), "AlbumPrueba");
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		assertEquals(album.getFechaCreacion(), LocalDate.parse("14/03/2019", formatter));
	}

	@Test
	public void testAgregarCancion() {
		Cancion cancion2 = new Cancion("C-6", "U-5", "Prueba6", "14/03/2019", "data/canciones/ring.mp3", true, false, false, 0);
		muflix.agregarCancion(cancion2);
		assertTrue(album.agregarCancion(cancion.getId()));
		assertFalse(album.agregarCancion(cancion.getId())); //No agregar una cancion si ya esta contenida en el album.
		assertTrue(album.agregarCancion(cancion2.getId()));
	}
	
	@Test
	public void testEliminarCancion() {
		assertTrue(album.agregarCancion(cancion.getId()));
		assertTrue(album.eliminarCancion(cancion.getId()));
		assertFalse(album.eliminarCancion(cancion.getId())); //No eliminar una cancion que no esta en el album.
		
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
			muflix.getAlbumById(album.getId()).agregarCancion(cancion2.getId());
		} catch (AlbumIDNoEncontrado e2) {
			e2.printStackTrace();
		}
		
		//************ Anonimo ************//
		muflix.getUsuarioActual().setLimiteAlcanzado(false); //No ha alcanzado el limite de reproduccion.
		
		try {
			assertTrue(album.cargarMedia(muflix.getUsuarioActual()));
		} catch (MiembroIDNoEncontrado | CancionIDNoEncontrado e1) {
			e1.printStackTrace();
		}
		assertEquals(11, cancion.getNumReproducciones()); //Incrementar numero de reproducciones de la cancion.
		muflix.getUsuarioActual().setLimiteAlcanzado(true); //Establecer limite alcanzado para el anonimo.
		
		try {
			assertFalse(album.cargarMedia(muflix.getUsuarioActual())); //No reproducir con limite alcanzado.
		} catch (MiembroIDNoEncontrado | CancionIDNoEncontrado e1) {
			e1.printStackTrace();
		}
		//********************************//
		
		//************ Miembro ************//
		try {
			assertTrue(album.cargarMedia(muflix.getMiembroById("U-5")));
		} catch (MiembroIDNoEncontrado | CancionIDNoEncontrado e) {
			e.printStackTrace();
		}
		assertEquals(12, cancion.getNumReproducciones()); //Incrementar numero de reproducciones.
		assertEquals(0, cancion2.getNumReproducciones()); //No tiene reproduccion automatica.
		
		cancion.setExplicita(true); //Establecer cancion como explicita.
		
		try {
			assertTrue(album.cargarMedia(muflix.getMiembroById("U-5"))); //Miembro mayor de edad.
		} catch (MiembroIDNoEncontrado | CancionIDNoEncontrado e) {
			e.printStackTrace();
		}
		assertEquals(13, cancion.getNumReproducciones()); //Reproducir cancion explicita (mayor de edad).
		
		try {
			assertTrue(album.cargarMedia(muflix.getMiembroById("U-6"))); //Miembro menor de edad.
		} catch (MiembroIDNoEncontrado | CancionIDNoEncontrado e) {
			e.printStackTrace();
		}
		assertEquals(13, cancion.getNumReproducciones()); //No reproducir cancion explicita.
		
		try {
			muflix.getMiembroById("U-5").setLimiteAlcanzado(true);
			assertFalse(album.cargarMedia(muflix.getMiembroById("U-5"))); //Limite de reproduccion alcanzado.
		} catch (MiembroIDNoEncontrado | CancionIDNoEncontrado e) {
			e.printStackTrace();
		}
		
		try {
			muflix.getMiembroById("U-5").darPremium(); //Hacer Premium.
			assertTrue(album.cargarMedia(muflix.getMiembroById("U-5"))); //Limite de reproduccion alcanzado, pero es Premium.
		} catch (MiembroIDNoEncontrado | CancionIDNoEncontrado e) {
			e.printStackTrace();
		}
		
		try {
			muflix.getMiembroById("U-5").activarReproduccionAutomatica(); //Activar reproduccion automatica.
			assertTrue(album.cargarMedia(muflix.getMiembroById("U-5")));
		} catch (MiembroIDNoEncontrado | CancionIDNoEncontrado e) {
			e.printStackTrace();
		}
		
		//Con la reproduccion automatica, se cargan ambas canciones. Se incrementan sus reproducciones.
		assertEquals(15, cancion.getNumReproducciones());
		assertEquals(1, cancion2.getNumReproducciones());
		
		//*********************************//
		
		//********* Administrador *********//
		try {
			assertTrue(album.cargarMedia(muflix.getAdministrador())); //Administrador puede cargar media.
		} catch (MiembroIDNoEncontrado | CancionIDNoEncontrado e) {
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
			muflix.getAlbumById(album.getId()).agregarCancion(cancion2.getId());
		} catch (AlbumIDNoEncontrado e2) {
			e2.printStackTrace();
		}
		
		//************ Anonimo ************//
		assertTrue(album.reproducirMedia(muflix.getUsuarioActual()));
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion()); //Solo reproduce la primera cancion (No rep. automatica).
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		assertEquals(11, cancion.getNumReproducciones()); //Incrementar numero de reproducciones.
		cancion.bloquear(); //Ocultar cancion.
		
		assertTrue(album.reproducirMedia(muflix.getUsuarioActual())); //Cancion oculta.
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion()); //Se reproduce la segunda cancion.
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		assertEquals(11, cancion.getNumReproducciones()); //No se incrementa el numero de reproducciones.
		assertEquals(1, cancion2.getNumReproducciones()); //Se incrementa el numero de reproducciones.
		cancion.desbloquear(); //Mostrar cancion.
		//********************************//
		
		//************ Miembro ************//
		try {
			assertTrue(album.reproducirMedia(muflix.getMiembroById("U-5")));
		} catch (MiembroIDNoEncontrado e1) {
			e1.printStackTrace();
		} //Cancion visible.
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion()); //Solo reproduce la primera cancion (No rep. automatica).
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		assertEquals(12, cancion.getNumReproducciones()); //Incrementar numero de reproducciones.
		
		try {
			assertTrue(album.reproducirMedia(muflix.getMiembroById("U-6")));
		} catch (MiembroIDNoEncontrado e1) {

			e1.printStackTrace();
		} //Cancion visible.
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		assertEquals(12, cancion.getNumReproducciones()); //No incrementar numero de reproducciones, si reproduce el autor.
		
		cancion.bloquear(); //Ocultar cancion.
		
		try {
			assertTrue(album.reproducirMedia(muflix.getMiembroById("U-5"))); //Primera Cancion oculta.
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion()); //Reproduce segunda cancion.
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		assertEquals(12, cancion.getNumReproducciones()); //No se incrementa el numero de reproducciones.
		assertEquals(2, cancion2.getNumReproducciones()); //Se incrementa el numero de reproducciones.
		
		cancion.desbloquear(); //Mostrar cancion de nuevo.
		
		cancion.setExplicita(true); //Establecer cancion como explicita.
		
		try {
			assertTrue(album.reproducirMedia(muflix.getMiembroById("U-5"))); //Miembro mayor de edad.
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		assertEquals(13, cancion.getNumReproducciones()); //Se incrementa el numero de reproducciones.
		assertEquals(2, cancion2.getNumReproducciones()); //No Se incrementa el numero de reproducciones.
		
		muflix.agregarMiembro(new Miembro("U-7", "MenorTest", "c@muflix.es", "1234", "Usuario Test", "01/04/2019", "01/01/2017", false, true, 0, false, "01/01/" + ConstantesGlobales.NULL_DATE_YEAR, "01/01/" + ConstantesGlobales.NULL_DATE_YEAR));
		
		try {
			assertTrue(album.reproducirMedia(muflix.getMiembroById("U-7"))); //Miembro menor de edad
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		assertEquals(13, cancion.getNumReproducciones()); //No se incrementa el numero de reproducciones.
		assertEquals(3, cancion2.getNumReproducciones()); //Se incrementa el numero de reproducciones.
		
		try {
			muflix.getMiembroById("U-5").setLimiteAlcanzado(true);
			assertFalse(album.reproducirMedia(muflix.getMiembroById("U-5"))); //Limite de reproduccion alcanzado.
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		assertEquals(13, cancion.getNumReproducciones()); //No se incrementa el numero de reproducciones.
		assertEquals(3, cancion2.getNumReproducciones()); //No se incrementa el numero de reproducciones.
		
		try {
			muflix.getMiembroById("U-5").darPremium(); //Hacer Premium.
			assertTrue(muflix.getMiembroById("U-5").esPremium());
			assertTrue(album.reproducirMedia(muflix.getMiembroById("U-5"))); //Limite de reproduccion alcanzado, pero es Premium.
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		//Solo se incrementa la primera cancion porque no tiene rep. automatica.
		assertEquals(14, cancion.getNumReproducciones()); //Se incrementa el numero de reproducciones.
		assertEquals(3, cancion2.getNumReproducciones()); //No se incrementa el numero de reproducciones.
		
		try {
			muflix.getMiembroById("U-5").activarReproduccionAutomatica(); //Activar rep. automatica.
			assertTrue(album.reproducirMedia(muflix.getMiembroById("U-5"))); //Limite de reproduccion alcanzado, pero es Premium.
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion()*2);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		assertEquals(15, cancion.getNumReproducciones()); //Se incrementa el numero de reproducciones.
		assertEquals(4, cancion2.getNumReproducciones()); //Se incrementa el numero de reproducciones.
		//*********************************//
		
		//********* Administrador *********//
		assertTrue(album.reproducirMedia(muflix.getAdministrador())); //Administrador puede reproducir media.
		//*********************************//
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion()*2);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		assertEquals(16, cancion.getNumReproducciones()); //Se incrementa el numero de reproducciones.
		assertEquals(5, cancion2.getNumReproducciones()); //Se incrementa el numero de reproducciones.
	}
	
	@Test
	public void testEquals() {
		Album album1 = null;
		try {
			album1 = muflix.getAlbumById(album.getId());
		} catch (AlbumIDNoEncontrado e) {
			e.printStackTrace();
		}
		Album album2 = new Album("A-5", "U-6", "AlbumPrueba","14/03/2019");
		
		assertTrue(album1.equals(album2)); //Mismo ID, mismo titulo.
		
		album2 = new Album("A-6", "U-6", "AlbumPrueba","14/03/2019");
		assertFalse(album1.equals(album2)); //Mismo titulo, distinto ID.
		
		album2 = new Album("A-6", "U-6", "AlbumPrueba2","14/03/2019");
		assertFalse(album1.equals(album2)); //Distinto titulo, distinto ID.
	}
}
