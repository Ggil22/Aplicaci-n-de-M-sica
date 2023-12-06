package com.padsof.muflix.testers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.padsof.muflix.*;
import com.padsof.muflix.core.Muflix;
import com.padsof.muflix.exceptions.MiembroIDNoEncontrado;
import com.padsof.muflix.exceptions.PlaylistIDNoEncontrado;
import com.padsof.muflix.media.Album;
import com.padsof.muflix.media.Cancion;
import com.padsof.muflix.media.Playlist;
import com.padsof.muflix.usuario.Anonimo;

import pads.musicPlayer.exceptions.Mp3PlayerException;

public class Tester01 {
	
	public static void main(String[] args) {
	 // @SuppressWarnings("unused")
	Muflix muflix = Muflix.getInstance();
	  //muflix.getMiembros().get(0).agregarSeguido("U-666");
	  Anonimo u = new Anonimo("AAAAA");
	  u.hacerRegistro("guillemono", "aa", "aa", "aa", "22/08/1999");
	  
	  //Album c = muflix.getAlbumById("A-1");
	  Playlist p = null;
	  try {
		  p = muflix.getPlaylistById("P-1");
	} catch (PlaylistIDNoEncontrado e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  try {
		Muflix.getInstance().getMiembroById("U-1").darPremium();
	} catch (MiembroIDNoEncontrado e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  try {
		Muflix.getInstance().getMiembroById("U-1").activarReproduccionAutomatica();
	} catch (MiembroIDNoEncontrado e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	  try {
		p.reproducirMedia(Muflix.getInstance().getMiembroById("U-1"));
	} catch (MiembroIDNoEncontrado e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
	 // Muflix.getInstance().getMiembroById("U-1").mostrarPlaylists();
	  
	  /*new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	Muflix.getInstance().getReproductor().stop();
		            }
		        }, 
		        5000 
		);
	  
	  new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	try {
		            		Muflix.getInstance().getReproductor().play();
		            	} catch (Mp3PlayerException e) {
		            		// TODO Auto-generated catch block
		            		e.printStackTrace();
		            	}
		            }
		        }, 
		        6000 
		);*/
	  
	  
	  
	  
/*	  try {
		TimeUnit.SECONDS.sleep(20);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}*/
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	LocalDate fecha = LocalDate.parse("22/08/1999", formatter);
	
	/*System.out.println(Muflix.getInstance().getMiembroById("U-1").getSeguidos());
	System.out.println(Muflix.getInstance().getMiembroById("U-1").getSeguidores());
	System.out.println(Muflix.getInstance().getMiembroById("U-2").getSeguidos());
	System.out.println(Muflix.getInstance().getMiembroById("U-2").getSeguidores());
	System.out.println(Muflix.getInstance().getMiembroById("U-3").getSeguidos());
	System.out.println(Muflix.getInstance().getMiembroById("U-3").getSeguidores());*/
	  System.out.println(LocalDate.now());
	  System.out.println(fecha.format(formatter));
	  //Muflix.getInstance().guardarUsuarios(Muflix.getInstance().getMiembros());
	  //Muflix.getInstance().guardarDatos();
	  System.out.println("FIN");
	}
}