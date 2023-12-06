package com.padsof.muflix.utils;

/**
 * Clase auxiliar para almacenar ya acceder a metodos utiles y genericos.
 * @author Grupo 4 - PADSOF 2261
 *
 */
public class FuncionesUtiles {
	
	/**
	 * Constructor privado para no crear instancias de FuncionesUtiles.
	 * 
	 */
	private FuncionesUtiles () {} // No crear instancias
	
	/**
	 * Eliminar el ultimo caracter de una String.
	 * @param str String de la que eliminar el ultimo caracter.
	 * @return String con el ultimo caracter eliminado.
	 * 
	 */
	public static String removeLastChar(String str) {
        return str.substring(0,str.length()-1);
    }
}
