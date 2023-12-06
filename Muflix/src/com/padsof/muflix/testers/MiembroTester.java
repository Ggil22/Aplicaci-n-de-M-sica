package com.padsof.muflix.testers;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Test;

import com.padsof.muflix.core.Muflix;
import com.padsof.muflix.exceptions.MiembroIDNoEncontrado;
import com.padsof.muflix.media.Cancion;
import com.padsof.muflix.usuario.Administrador;
import com.padsof.muflix.usuario.Miembro;
import com.padsof.muflix.utils.ConstantesGlobales;


public class MiembroTester {
	
	private Miembro usuario1;
	private Miembro usuario2;
	
	private Cancion cancion;
	
	private Administrador admin;
	
	private Muflix muflix;
	
	@Before
	public void setUp() throws Exception {
		Muflix.reset();
		muflix = Muflix.getInstance();

		usuario1 = new Miembro("U-5", "MiembroTest", "a@muflix.es", "1234", "Usuario Test", "01/04/2019", "01/01/1999", false, false, 0, false, "01/01/" + ConstantesGlobales.NULL_DATE_YEAR, "01/01/" + ConstantesGlobales.NULL_DATE_YEAR);
		usuario2 = new Miembro("U-6", "MiembroTest2", "aa@muflix.es", "12345", "Usuario2 Test", "01/04/2019", "01/01/2015", false, false, 0, false, "01/01/" + ConstantesGlobales.NULL_DATE_YEAR, "01/01/" + ConstantesGlobales.NULL_DATE_YEAR);		
		
		admin = new Administrador("admin@muflix.es", "admin123");
		
		cancion = new Cancion("C-5", "U-6", "Prueba5", "14/03/2019", "data/canciones/ring.mp3", true, false, false, 10);
		
		muflix.setUsuarioActual(usuario1);
		
		muflix.agregarMiembro(usuario1);
		muflix.agregarMiembro(usuario2);
		
		muflix.agregarCancion(cancion);
		cancion.validar();
		
		muflix.setAdministrador(admin);
		
	}
	
	@Test
	public void testMiembro () {
		
		assertEquals(muflix.getUsuarioActual().getNombreUsuario(), "MiembroTest");
		assertEquals(usuario1.getId(), "U-5");
		assertEquals(usuario1.getNombreCompleto(), "Usuario Test");
		assertEquals(usuario1.getNombreUsuario(), "MiembroTest");
		assertEquals(usuario1.getEmail(), "a@muflix.es");
		assertEquals(usuario1.getPassword(), "1234");
		
		DateTimeFormatter nacimiento = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		assertEquals(usuario1.getFechaNacimiento(), LocalDate.parse("01/01/1999", nacimiento));
		
		DateTimeFormatter registro = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		assertEquals(usuario1.getFechaRegistro(), LocalDate.parse("01/04/2019", registro));
		
		assertEquals(usuario1.esBaneado(), false);
		assertEquals(usuario1.esPremium(), false);
		assertEquals(usuario1.getNumReproducciones(), 0);
		assertEquals(usuario1.esBloqueado(), false);
		
	}
	
	@Test
	public void testEsMayorDeEdad() {
		
		//El miembro 1 es mayor de edad.
		try {
			assertTrue(muflix.getMiembroById(usuario1.getId()).esMayorDeEdad());
		} catch (MiembroIDNoEncontrado e) {
			System.out.println(e);
		}
		
		//El miembro 2 no lo es.
		try {
			assertFalse(muflix.getMiembroById(usuario2.getId()).esMayorDeEdad());
		} catch (MiembroIDNoEncontrado e) {
			System.out.println(e);
		}
	}
	
	@Test
	public void testSeguir() {
		
		//El usuario 1 sigue al usuario 2.
		assertTrue(usuario1.seguir(usuario2.getId()));
		assertTrue(usuario1.esSeguidorDe(usuario2.getId()));
		assertTrue(usuario2.esSeguidoPor(usuario1.getId()));
		//El usuario 2 no sigue al usuario 1.
		assertFalse(usuario2.esSeguidorDe(usuario1.getId()));
		
		//El usuario 1 intenta seguir de nuevo al usuario 2.
		assertFalse(usuario1.seguir(usuario2.getId()));
		
		//El usuario 1 deja de seguir al usuario 2.
		assertTrue(usuario1.dejarDeSeguir(usuario2.getId()));
		assertFalse(usuario1.esSeguidorDe(usuario2.getId()));
		assertFalse(usuario2.esSeguidoPor(usuario1.getId()));
		
		//El usuario 1 intenta seguir a alguien que no existe.
		assertFalse(usuario1.seguir("NO EXISTE"));
	}
	
	@Test
	public void testDejarDeSeguir() {
		
		//El usuario 1 sigue al usuario 2.
		assertTrue(usuario1.seguir(usuario2.getId()));
		assertTrue(usuario1.esSeguidorDe(usuario2.getId()));
		assertTrue(usuario2.esSeguidoPor(usuario1.getId()));
		//El usuario 2 no sigue al usuario 1.
		assertFalse(usuario2.esSeguidorDe(usuario1.getId()));
		
		//El usuario 1 deja de seguir al usuario 2.
		assertTrue(usuario1.dejarDeSeguir(usuario2.getId()));
		assertFalse(usuario1.esSeguidorDe(usuario2.getId()));
		assertFalse(usuario2.esSeguidoPor(usuario1.getId()));
		
		//El usuario 1 no sigue al usuario 2.
		assertFalse(usuario1.dejarDeSeguir(usuario2.getId()));
		
		//El usuario 1 intenta dejar de seguir a alguien que no existe.
		assertFalse(usuario1.dejarDeSeguir("NO EXISTE"));
	}
	
	@Test
	public void testPagarPremium() {
		
		//El usuario no debe ser premium al inicio.
		assertFalse(usuario1.esPremium());
		
		//El usuario paga el premium correctamente.
		try {
			assertTrue(muflix.getMiembroById(usuario1.getId()).pagarPremium("1234567890123456", "testPremium"));
		} catch (MiembroIDNoEncontrado e) {
			System.out.println(e);
		}
		
		//Comprobamos que el usuario es Premium y que la fecha Premium coincide con hoy.
		try {
			assertTrue(muflix.getMiembroById(usuario1.getId()).esPremium() && muflix.getMiembroById(usuario1.getId()).getFechaPremium().equals(LocalDate.now()));
		} catch (MiembroIDNoEncontrado e) {
			System.out.println(e);
		}
	}
}