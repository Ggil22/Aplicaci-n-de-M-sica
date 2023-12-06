package com.padsof.muflix.testers;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.padsof.muflix.core.Muflix;
import com.padsof.muflix.exceptions.MiembroIDNoEncontrado;
import com.padsof.muflix.exceptions.PasswordIncorrecta;
import com.padsof.muflix.exceptions.UsernameNoEncontrado;
import com.padsof.muflix.exceptions.UsuarioBaneado;
import com.padsof.muflix.exceptions.UsuarioBloqueado;
import com.padsof.muflix.usuario.Administrador;
import com.padsof.muflix.usuario.Anonimo;

public class AnonimoTester {
	
	private Anonimo anonimo;
	private Anonimo anonimo2;
	
	private Administrador admin;
	
	private Muflix muflix;
	
	@Before
	public void setUp() throws Exception {
		Muflix.reset();
		muflix = Muflix.getInstance();
		
		anonimo = new Anonimo("INV");
		anonimo2 = new Anonimo("INV");
		
		admin = new Administrador("admin@muflix.es", "admin123");
		anonimo2.hacerRegistro("nombreTest2", "email@prueba.com", "password4321", "nombre Completo", "10/01/1999");
		
		muflix.setUsuarioActual(anonimo);
		
		muflix.setAdministrador(admin);

	}

	@Test
	public void testAnonimo() {
		
		assertEquals(muflix.getUsuarioActual().getNombreUsuario(), "Anonimo");
		assertEquals(muflix.getUsuarioActual().getId(), "INV");
		assertEquals(muflix.getUsuarioActual().getNumReproducciones(), 0);
		assertEquals(muflix.getUsuarioActual().getLimiteAlcanzado(), false);	
	}

	@Test
	public void testHacerRegistro() {
		//Realizamos registro correcto.
		assertTrue(anonimo.hacerRegistro("nombreTest", "email@prueba.com", "password4321", "nombre Completo", "10/01/1999"));
		
		//Iniciamos sesion.
		try {
			assertTrue(anonimo.hacerLogin("nombreTest", "password4321"));
		} catch (UsuarioBaneado | UsuarioBloqueado | PasswordIncorrecta | MiembroIDNoEncontrado e) {
			e.printStackTrace();
		}
		
		//Se espera que el usuario actual tenga el nombre que empleamos en el registro.
		assertEquals(muflix.getUsuarioActual().getNombreUsuario(), "nombreTest");
		
		//Cerramos sesion.
		try {
			muflix.getMiembroByNombre("nombreTest").hacerLogOut();
		} catch (UsernameNoEncontrado e) {
			e.printStackTrace();
		}
		
		//Al cerrar sesion, se pasa a Anonimo.
		assertEquals(muflix.getUsuarioActual().getNombreUsuario(), "Anonimo");
		
		//Intentamos registrarnos con el mismo nombre.
		assertFalse(anonimo.hacerRegistro("nombreTest", "email@prueba.com", "password4321", "nombre Completo", "10/01/1999"));
	}
	
	@Test
	public void testHacerLogin() {
		
		//Iniciamos sesion correctamente.
		assertEquals(muflix.getUsuarioActual().getNombreUsuario(), "Anonimo");
		try {
			assertTrue(anonimo2.hacerLogin("nombreTest2", "password4321"));
		} catch (UsuarioBaneado e) {
			System.out.print(e);
		} catch (UsuarioBloqueado e) {
			System.out.println(e);
		} catch (PasswordIncorrecta e) {
			System.out.println(e);
		} catch (MiembroIDNoEncontrado e) {
			System.out.println(e);
		}
		
		//Comprobamos que el usuario actual cambia.
		assertEquals(muflix.getUsuarioActual().getNombreUsuario(), "nombreTest2");
		
		//Cerramos sesion.
		try {
			muflix.getMiembroByNombre("nombreTest2").hacerLogOut();
		} catch (UsernameNoEncontrado e) {
			System.out.println(e);
		}
		
		//Intentamos iniciar sesion con una password incorrecta.
		try {
			assertTrue(anonimo.hacerLogin("nombreTest2", "incorrectooo"));
		} catch (UsuarioBaneado e) {
			System.out.println(e);
		} catch (UsuarioBloqueado e) {
			System.out.println(e);
		} catch (PasswordIncorrecta e) {
			System.out.println(e);
		} catch (MiembroIDNoEncontrado e) {
			System.out.println(e);
		}
		
		//Baneamos a nombreTest2.
		try {
			muflix.getMiembroByNombre("nombreTest2").banear();
		} catch (UsernameNoEncontrado e) {
			System.out.println(e);
		}
		
		//Intentamos iniciar sesion pero estamos baneados.
		try {
			assertTrue(anonimo.hacerLogin("nombreTest2", "password4321"));
		} catch (UsuarioBaneado e) {
			System.out.println(e);
		} catch (UsuarioBloqueado e) {
			System.out.println(e);
		} catch (PasswordIncorrecta e) {
			System.out.println(e);
		} catch (MiembroIDNoEncontrado e) {
			System.out.println(e);
		}
		
		//Desbaneamos a nombreTest2.
		try {
			muflix.getMiembroByNombre("nombreTest2").desbanear();
		} catch (UsernameNoEncontrado e) {
			System.out.println(e);
		}
		
		//Bloqueamos a nombreTest2.
		try {
			muflix.getMiembroByNombre("nombreTest2").bloquear();
		} catch (UsernameNoEncontrado e) {
			System.out.println(e);
		}
		
		//Intentamos iniciar sesion pero estamos bloqueados.
		try {
			assertTrue(anonimo.hacerLogin("nombreTest2", "password4321"));
		} catch (UsuarioBaneado e) {
			System.out.println(e);
		} catch (UsuarioBloqueado e) {
			System.out.println(e);
		} catch (PasswordIncorrecta e) {
			System.out.println(e);
		} catch (MiembroIDNoEncontrado e) {
			System.out.println(e);
		}
		
	}
}