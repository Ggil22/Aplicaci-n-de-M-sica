package com.padsof.muflix.testers;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.padsof.muflix.core.Muflix;
import com.padsof.muflix.exceptions.CancionIDNoEncontrado;
import com.padsof.muflix.exceptions.MiembroIDNoEncontrado;
import com.padsof.muflix.media.Cancion;
import com.padsof.muflix.usuario.Administrador;
import com.padsof.muflix.usuario.Anonimo;
import com.padsof.muflix.usuario.Miembro;
import com.padsof.muflix.utils.ConstantesGlobales;

public class CancionTester {
	
	private Miembro usuario1;
	private Miembro usuario2;
	
	private Anonimo anonimo;
	
	private Administrador admin;
	
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
		
		cancion = new Cancion("C-5", "U-6", "Prueba3", "14/03/2019", "data/canciones/ring.mp3", true, false, false, 10);
		
		muflix.setUsuarioActual(anonimo);
		
		muflix.setAdministrador(admin);
		
		muflix.agregarMiembro(usuario1);
		muflix.agregarMiembro(usuario2);
		
		muflix.agregarCancion(cancion);
		
	}
	
	@Test
	public void testCancion() {
		
		assertEquals(cancion.getId(), "C-5");
		assertEquals(cancion.getAutor_id(), "U-6");
		assertEquals(cancion.getTitulo(), "Prueba3");
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		assertEquals(cancion.getFechaCreacion(), LocalDate.parse("14/03/2019", formatter));
		
		assertEquals(cancion.getPath(), "data/canciones/ring.mp3");
		assertTrue(cancion.esValida());
		assertFalse(cancion.esOculta());
		assertFalse(cancion.esExplicita());
		assertEquals(10, cancion.getNumReproducciones());
		
	}

	@Test
	public void testSetTitulo() {
		Cancion cancion2 = new Cancion("C-6", "U-5", "Prueba6", "14/03/2019", "data/canciones/ring.mp3", true, false, false, 0);
		muflix.agregarCancion(cancion2);
		assertTrue(cancion.setTitulo("Prueba5"));
		assertFalse(cancion.setTitulo("Prueba6")); //No crear canciones con el mismo titulo.
	}
	
	@Test
	public void testCargarMedia() {
		
		try {
			cancion = muflix.getCancionById(cancion.getId());
		} catch (CancionIDNoEncontrado e2) {
			e2.printStackTrace();
		}
		
		//************ Anonimo ************//
		try {
			assertTrue(cancion.cargarMedia(muflix.getUsuarioActual())); //Cancion visible.
		} catch (MiembroIDNoEncontrado e1) {
			e1.printStackTrace();
		}
		assertEquals(11, cancion.getNumReproducciones()); //Incrementar numero de reproducciones.
		cancion.bloquear(); //Ocultar cancion.
		
		try {
			assertFalse(cancion.cargarMedia(muflix.getUsuarioActual())); //Cancion invisible.
		} catch (MiembroIDNoEncontrado e1) {
			e1.printStackTrace();
		}
		cancion.desbloquear(); //Mostrar cancion.
		//********************************//
		
		//************ Miembro ************//
		try {
			assertTrue(cancion.cargarMedia(muflix.getMiembroById("U-5"))); //Cancion visible.
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		assertEquals(12, cancion.getNumReproducciones()); //Incrementar numero de reproducciones.
		
		try {
			assertTrue(cancion.cargarMedia(muflix.getMiembroById("U-6"))); //Cancion visible.
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		assertEquals(12, cancion.getNumReproducciones()); //No incrementar numero de reproducciones, si reproduce el autor.
		
		cancion.bloquear(); //Ocultar cancion.
		
		try {
			assertFalse(cancion.cargarMedia(muflix.getMiembroById("U-5"))); //Cancion oculta.
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		
		cancion.desbloquear(); //Mostrar cancion de nuevo.
		
		cancion.setExplicita(true); //Establecer cancion como explicita.
		
		try {
			assertTrue(cancion.cargarMedia(muflix.getMiembroById("U-5"))); //Miembro mayor de edad.
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		
		try {
			assertFalse(cancion.cargarMedia(muflix.getMiembroById("U-6"))); //Miembro menor de edad
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		
		try {
			muflix.getMiembroById("U-5").setLimiteAlcanzado(true);
			assertFalse(cancion.cargarMedia(muflix.getMiembroById("U-5"))); //Limite de reproduccion alcanzado.
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		
		try {
			muflix.getMiembroById("U-5").darPremium(); //Hacer Premium.
			assertTrue(cancion.cargarMedia(muflix.getMiembroById("U-5"))); //Limite de reproduccion alcanzado, pero es Premium.
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		//*********************************//
		
		//********* Administrador *********//
		try {
			assertTrue(cancion.cargarMedia(muflix.getAdministrador())); //Administrador puede cargar media.
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		//*********************************//
	}
	
	@Test
	public void testReproducirMedia() {
		
		//************ Anonimo ************//
		assertTrue(cancion.reproducirMedia(muflix.getUsuarioActual())); //Cancion visible.
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		assertEquals(11, cancion.getNumReproducciones()); //Incrementar numero de reproducciones.
		cancion.bloquear(); //Ocultar cancion.
		
		assertFalse(cancion.reproducirMedia(muflix.getUsuarioActual())); //Cancion invisible.
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		cancion.desbloquear(); //Mostrar cancion.
		//********************************//
		
		//************ Miembro ************//
		try {
			assertTrue(cancion.reproducirMedia(muflix.getMiembroById("U-5")));
		} catch (MiembroIDNoEncontrado e1) {
			e1.printStackTrace();
		} //Cancion visible.
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		assertEquals(12, cancion.getNumReproducciones()); //Incrementar numero de reproducciones.
		
		try {
			assertTrue(cancion.reproducirMedia(muflix.getMiembroById("U-6")));
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
			assertFalse(cancion.reproducirMedia(muflix.getMiembroById("U-5"))); //Cancion oculta.
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		cancion.desbloquear(); //Mostrar cancion de nuevo.
		
		cancion.setExplicita(true); //Establecer cancion como explicita.
		
		try {
			assertTrue(cancion.reproducirMedia(muflix.getMiembroById("U-5"))); //Miembro mayor de edad.
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		try {
			assertFalse(cancion.reproducirMedia(muflix.getMiembroById("U-6"))); //Miembro menor de edad
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		try {
			muflix.getMiembroById("U-5").setLimiteAlcanzado(true);
			assertFalse(cancion.reproducirMedia(muflix.getMiembroById("U-5"))); //Limite de reproduccion alcanzado.
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		try {
			muflix.getMiembroById("U-5").darPremium(); //Hacer Premium.
			assertTrue(cancion.reproducirMedia(muflix.getMiembroById("U-5"))); //Limite de reproduccion alcanzado, pero es Premium.
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		try {
			TimeUnit.SECONDS.sleep((long) cancion.getDuracion());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		//*********************************//
		
		//********* Administrador *********//
		assertTrue(cancion.reproducirMedia(muflix.getAdministrador())); //Administrador puede reproducir media.
		//*********************************//
	}
	
	@Test
	public void testEquals() {
		Cancion cancion1 = null;
		try {
			cancion1 = muflix.getCancionById(cancion.getId());
		} catch (CancionIDNoEncontrado e) {
			e.printStackTrace();
		}
		Cancion cancion2 = new Cancion("C-5", "U-6", "Prueba3", "14/03/2019", "data/canciones/ring.mp3", true, false, false, 10);
		
		assertTrue(cancion1.equals(cancion2)); //Mismo ID, mismo titulo.
		
		cancion2 = new Cancion("C-7", "U-6", "Prueba3", "14/03/2019", "data/canciones/ring.mp3", true, false, false, 10);
		assertTrue(cancion1.equals(cancion2)); //Mismo titulo.
		
		cancion2 = new Cancion("C-7", "U-6", "Prueba4", "14/03/2019", "data/canciones/ring.mp3", true, false, false, 10);
		assertFalse(cancion1.equals(cancion2)); //Distinto titulo, distinto ID.
	}
}
