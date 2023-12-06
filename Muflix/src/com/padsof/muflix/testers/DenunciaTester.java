package com.padsof.muflix.testers;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Test;

import com.padsof.muflix.core.Muflix;
import com.padsof.muflix.denuncia.Denuncia;
import com.padsof.muflix.exceptions.CancionIDNoEncontrado;
import com.padsof.muflix.exceptions.MiembroIDNoEncontrado;
import com.padsof.muflix.media.Cancion;
import com.padsof.muflix.usuario.Miembro;
import com.padsof.muflix.utils.ConstantesGlobales;

public class DenunciaTester {
	
	private Miembro usuario1;
	private Miembro usuario2;
	
	private Cancion cancionDenunciada;
	private Cancion cancionAdicional;
	
	private Denuncia denuncia;
	
	private Muflix muflix;
	
	@Before
	public void setUp() throws Exception {
		Muflix.reset();
		muflix = Muflix.getInstance();
		
		usuario1 = new Miembro("U-5", "UsuarioDenunciante", "a@muflix.es", "1234", "Usuario Denunciante", "01/04/2019", "01/01/1999", false, false, 0, false, "01/01/" + ConstantesGlobales.NULL_DATE_YEAR, "01/01/" + ConstantesGlobales.NULL_DATE_YEAR);
		usuario2 = new Miembro("U-6", "UsuarioDenunciado", "b@muflix.es", "1234", "Usuario Denunciado", "01/04/2019", "01/01/1999", false, false, 0, false, "01/01/" + ConstantesGlobales.NULL_DATE_YEAR, "01/01/" + ConstantesGlobales.NULL_DATE_YEAR);
		
		cancionDenunciada = new Cancion("C-5", "U-6", "Prueba3", "14/03/2019", "data/canciones/Prueba3.mp3", true, true, false, 8);
		cancionAdicional = new Cancion("C-6", "U-6", "Prueba2", "14/03/2019", "data/canciones/Prueba2.mp3", true, false, false, 10);
		
		denuncia = new Denuncia("D-5", "U-5", "U-6", "28/03/2019", "C-5", "Ha plagiado mi cancion titulada UN RAYO DE SOL.", false);
		
		muflix.agregarMiembro(usuario1);
		muflix.agregarMiembro(usuario2);
		
		muflix.agregarCancion(cancionDenunciada);
		muflix.agregarCancion(cancionAdicional);
		
		muflix.agregarDenuncia(denuncia);
	}
	
	@Test
	public void testDenuncia() {
		
		assertEquals(denuncia.getId(), "D-5");
		assertEquals(denuncia.getDenunciante(), "U-5");
		assertEquals(denuncia.getDenunciado(), "U-6");
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		assertEquals(denuncia.getFecha(), LocalDate.parse("28/03/2019", formatter));
		
		assertEquals(denuncia.getCancion(), "C-5");
		assertEquals(denuncia.getComentario(), "Ha plagiado mi cancion titulada UN RAYO DE SOL.");
		assertFalse(denuncia.esRevisada());
		
	}
	
	@Test
	public void testAceptar() {
		muflix.getDenunciaById(denuncia.getId()).aceptar();
		
		assertTrue(muflix.getDenunciaById(denuncia.getId()).esRevisada());
		try {
			assertTrue(muflix.getMiembroById(usuario2.getId()).esBaneado());
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
			assertTrue(muflix.getCancionById(cancionDenunciada.getId()).esOculta());
		} catch (CancionIDNoEncontrado e) {
			e.printStackTrace();
			System.out.println(e);
		}
		try {
			assertTrue(muflix.getCancionById(cancionAdicional.getId()).esOculta());
		} catch (CancionIDNoEncontrado e) {
			e.printStackTrace();
			System.out.println(e);
		}
		
	}
	
	@Test
	public void testDenegar() {
		muflix.getDenunciaById(denuncia.getId()).denegar();
		
		assertTrue(muflix.getDenunciaById(denuncia.getId()).esRevisada());
		try {
			assertTrue(muflix.getMiembroById(usuario1.getId()).esBloqueado());
		} catch (MiembroIDNoEncontrado e1) {
			e1.printStackTrace();
			System.out.println(e1);
		}
		try {
			assertFalse(muflix.getMiembroById(usuario2.getId()).esBaneado());
		} catch (MiembroIDNoEncontrado e1) {
			e1.printStackTrace();
			System.out.println(e1);
		}
		
		try {
			assertFalse(muflix.getCancionById(cancionDenunciada.getId()).esOculta());
		} catch (CancionIDNoEncontrado e) {
			e.printStackTrace();
			System.out.println(e);
		}
		try {
			assertFalse(muflix.getCancionById(cancionAdicional.getId()).esOculta());
		} catch (CancionIDNoEncontrado e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
	
	@Test
	public void testEquals() {
		Denuncia denuncia1 = muflix.getDenunciaById(denuncia.getId());
		Denuncia denuncia2 = denuncia = new Denuncia("D-5", "U-5", "U-6", "28/03/2019", "C-5", "Ha plagiado mi cancion titulada UN RAYO DE SOL.", false);
		
		assertTrue(denuncia1.equals(denuncia2));
		
		denuncia2 = denuncia = new Denuncia("D-7", "U-5", "U-6", "28/03/2019", "C-5", "Ha plagiado mi cancion titulada UN RAYO DE SOL.", false);
	
		assertFalse(denuncia1.equals(denuncia2));
	}
}
