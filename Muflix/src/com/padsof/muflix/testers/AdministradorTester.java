package com.padsof.muflix.testers;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.padsof.muflix.core.Muflix;
import com.padsof.muflix.denuncia.Denuncia;
import com.padsof.muflix.exceptions.CancionIDNoEncontrado;
import com.padsof.muflix.exceptions.MiembroIDNoEncontrado;
import com.padsof.muflix.media.Cancion;
import com.padsof.muflix.usuario.Administrador;
import com.padsof.muflix.usuario.Anonimo;
import com.padsof.muflix.usuario.Miembro;
import com.padsof.muflix.utils.ConstantesGlobales;

public class AdministradorTester {

	private Miembro usuario1;
	//private Miembro usuario2;
	
	private Anonimo anonimo;
	
	private Administrador admin;
	
	private Denuncia denuncia;
	
	private Cancion cancion;
	
	private Muflix muflix;
	
	@Before
	public void setUp() throws Exception {
		Muflix.reset();
		muflix = Muflix.getInstance();
		
		anonimo = new Anonimo("INV");
		
		admin = new Administrador("admin@muflix.es", "admin123");
		
		usuario1 = new Miembro("U-5", "Oyente", "a@muflix.es", "1234", "Usuario Oyente", "01/04/2019", "01/01/1999", false, false, 0, false, "01/01/" + ConstantesGlobales.NULL_DATE_YEAR, "01/01/" + ConstantesGlobales.NULL_DATE_YEAR);
		//usuario2 = new Miembro("U-6", "Autor", "b@muflix.es", "1234", "Usuario Autor", "01/04/2019", "01/01/2017", false, true, 0, false, "01/01/" + ConstantesGlobales.NULL_DATE_YEAR, "01/01/" + ConstantesGlobales.NULL_DATE_YEAR);
		
		cancion = new Cancion("C-5", "U-6", "Prueba3", "14/03/2019", "data/canciones/Prueba3.mp3", true, true, false, 8);
		
		denuncia = new Denuncia("D-5", "U-1", "U-5", "28/03/2019", "C-5", "Ha plagiado mi cancion titulada UN RAYO DE SOL.", false);
		muflix.setUsuarioActual(anonimo);
		
		muflix.setAdministrador(admin);
		
		muflix.agregarMiembro(usuario1);
		//muflix.agregarMiembro(usuario2);
		
		muflix.agregarCancion(cancion);
		
	}
	
	@Test
	public void testAdministrador() {
		assertEquals(admin.getId(), "ADM");
		assertEquals(admin.getNombreUsuario(), "Administrador");
		assertEquals(admin.getEmail(), "admin@muflix.es");
		assertEquals(admin.getPassword(), "admin123");
	}
	
	@Test
	public void testBloquearMiembro() {
		//Bloqueamos el miembro que existe
		assertTrue(admin.bloquearMiembro("U-1"));
		
		//Comprobamos que esta bloqueado
		try {
			assertTrue(muflix.getMiembroById("U-1").esBloqueado());
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDesbloquearMiembro() {
		//Bloqueamos el miembro que existe
		assertTrue(admin.desbloquearMiembro("U-1"));
		
		//Comprobamos si se encuentra en el sistema
		try {
			assertEquals(muflix.getMiembroById("U-1"), "guillemono");
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		
		//Comprobamos que esta bloqueado
		try {
			assertFalse(muflix.getMiembroById("U-1").esBloqueado());
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testBanearMiembro() {
		//Baneamos el miembro que existe
		assertTrue(admin.banearMiembro("U-5"));
		
		//Comprobamos que esta baneado
		try {
			assertTrue(muflix.getMiembroById("U-5").esBaneado());
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDesbanearMiembro() {
		//Baneamos el miembro que existe
		assertTrue(admin.desbanearMiembro("U-1"));
		
		//Comprobamos que esta baneado
		try {
			assertFalse(muflix.getMiembroById("U-1").esBaneado());
		} catch (MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAceptarDenuncia() {
		assertTrue(admin.aceptarDenuncia("D-5"));
		
		assertTrue(muflix.getDenunciaById(denuncia.getId()).esRevisada());
		try {
			assertTrue(muflix.getMiembroById("U-1").esBaneado());
		} catch (MiembroIDNoEncontrado e1) {
			e1.printStackTrace();
			System.out.println(e1);
		}
		try {
			assertFalse(muflix.getMiembroById(usuario1.getId()).esBloqueado());
		} catch (MiembroIDNoEncontrado e1) {
			e1.printStackTrace();
			System.out.println(e1);
		}
		
		try {
			assertTrue(muflix.getCancionById(cancion.getId()).esOculta());
		} catch (CancionIDNoEncontrado e) {
			e.printStackTrace();
			System.out.println(e);
		}
		
	}
	
	@Test
	public void testDenegarDenuncia() {
		assertTrue(admin.denegarDenuncia("D-5"));
		
		assertTrue(muflix.getDenunciaById(denuncia.getId()).esRevisada());
		try {
			assertTrue(muflix.getMiembroById(usuario1.getId()).esBaneado());
		} catch (MiembroIDNoEncontrado e1) {
			e1.printStackTrace();
			System.out.println(e1);
		}
		try {
			assertFalse(muflix.getMiembroById("U-1").esBloqueado());
		} catch (MiembroIDNoEncontrado e1) {
			e1.printStackTrace();
			System.out.println(e1);
		}
		
		try {
			assertFalse(muflix.getCancionById(cancion.getId()).esOculta());
		} catch (CancionIDNoEncontrado e) {
			e.printStackTrace();
			System.out.println(e);
		}
		
	}
	
	@Test
	public void testMarcarComoExplicita() {
		admin.marcarComoExplicita("C-5");
		
		try {
			assertEquals(muflix.getCancionById("C-5").getTitulo(), "Prueba3");
		} catch (CancionIDNoEncontrado e1) {
			e1.printStackTrace();
		}
		
		try {
			assertTrue(muflix.getCancionById("C-5").esExplicita());
		} catch (CancionIDNoEncontrado e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMarcarComoNoExplicita() {
		admin.marcarComoExplicita("C-5");
		
		try {
			assertEquals(muflix.getCancionById("C-5").getTitulo(), "Prueba3");
		} catch (CancionIDNoEncontrado e1) {
			e1.printStackTrace();
		}
		
		try {
			assertFalse(muflix.getCancionById("C-5").esExplicita());
		} catch (CancionIDNoEncontrado e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testValidarCancion() {
		admin.validarCancion("C-5");
		
		try {
			assertTrue(muflix.getCancionById("C-5").esValida());
		} catch (CancionIDNoEncontrado e) {
			e.printStackTrace();
		}
		
		try {
			assertEquals(muflix.getCancionById("C-5").getTitulo(), "Prueba3");
		} catch (CancionIDNoEncontrado e1) {
			e1.printStackTrace();
		}
	}
	
	@Test
	public void testSetLimitacion() {
		admin.setLimitacion("maxReproducciones", "13");
		assertEquals(muflix.getMaxReproducciones(), 13, 0);
		
		admin.setLimitacion("numRepsPremium", "11");
		assertEquals(muflix.getNumRepsPremium(), 11);
		
		admin.setLimitacion("periodoPremium", "1");
		assertEquals(muflix.getPeriodoPremium(), 1);
		
		admin.setLimitacion("diasBloqueoTemporal", "30");
		assertEquals(muflix.getDiasBloqueoTemporal(), 30);
		
		admin.setLimitacion("precioPremium", "45");
		assertEquals(muflix.getPrecioPremium(), 45, 0);
	}
	
	
}

















