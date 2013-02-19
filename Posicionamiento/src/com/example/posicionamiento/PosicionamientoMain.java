package com.example.posicionamiento;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Ejemplo de como obtener la posición en Android haciendo uso de los proveedores de localización. Los más
 * comunes son el GPS y la localización por redes inhalámbricas(en versiones más recientes de Android).
 * 
 *  La posición se obtiene subscribiendonos a los origenes de datos que elijamos, y cada vez que transcurra
 *  el tiempo de recepción especificado recbiremos un objeto con toda la información sobre nuestra posición
 *  que el origen pueda darnos; el origen de datos nos informará también si se activa, desactiva ó no puede
 *  obtener la posición.
 *
 */
public class PosicionamientoMain extends Activity {
	private TextView lblLatitud;
	private TextView lblLongitud;
	private TextView lblPrecision;
	private TextView lblEstado;
	private TextView lblAltura;
	private final int SEGUNDOS_ACTUALIZACIONES=1000 * 60 * 2; // Una medida se descarta cuando pase este tiempo
	private final int SEGUNDOS_GPS = 1000 * 5;
	private final int SEGUNDOS_NETWORK = 1000 * 3;
	private final int METROS_NOTIFICACION_GPS = 0;
	private final int METROS_NOTIFICACION_NETWORK = 0;
	private float      precision;
	private boolean obtieneAltitud;
    private Location posicionActual;
	private LocationManager gestorLocalizacion;
	private listenerLocalizacion listenerLocalizacion;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		final boolean proveedorGPSActivado,proveedorNetworkActivado;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.posicionamiento_main);
		posicionActual = null;
		lblLatitud = (TextView) findViewById(R.id.etiq_latitud);
		lblLongitud = (TextView) findViewById(R.id.etiq_longitud);
		lblPrecision = (TextView) findViewById(R.id.etiq_precision);
		lblEstado = (TextView) findViewById(R.id.etiq_proveedor);
		lblAltura = (TextView) findViewById(R.id.etiq_altura);
		
		// Obtenemos una referencia al LocationManager del sistema
		gestorLocalizacion = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Obtenemos la ultima posicion conocida
		Location loc = gestorLocalizacion.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (loc==null)
			loc = gestorLocalizacion.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		mostrarPosicion(loc);
				
		proveedorGPSActivado = gestorLocalizacion.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (proveedorGPSActivado) {
			proveedorNetworkActivado = gestorLocalizacion.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if (!proveedorNetworkActivado) {
				abrirAjustesLocalizacion();
		    }
		}
	}

	/**
	 * Abre el menú de ajustes del sistema referentes a las opciones de localización, aquí el usuario
	 * podrá activar o desactivar los métodos de localización (GPS,redes inhalambricas,etc)
	 */
	private void abrirAjustesLocalizacion() {
		Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		this.startActivity(settingsIntent);
	}

	/**
	 * Evento que se dispara cada vez que se pulse un boton de la interfaz, el boton activar hará que
	 * se registren las actualizaciones de posición que entreguen los proveedores de lcalizacion, que
	 * en este caso serán el GPS y la localizacion por redes inhalambricas
	 * @param v Vista que ha disparado el evento
	 */
	public void onClick(View v) {
		
		if (v.getId() == R.id.bot_activar) {
			// Nos registramos para recibir actualizaciones de la posicon
			listenerLocalizacion = new listenerLocalizacion();
			if (gestorLocalizacion.isProviderEnabled(LocationManager.GPS_PROVIDER))
			    gestorLocalizacion.requestLocationUpdates(LocationManager.GPS_PROVIDER,	SEGUNDOS_GPS, METROS_NOTIFICACION_GPS, listenerLocalizacion);
			if (gestorLocalizacion.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
			gestorLocalizacion.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,	SEGUNDOS_NETWORK, METROS_NOTIFICACION_NETWORK, listenerLocalizacion);
		} else if (v.getId() == R.id.bot_parar)
			gestorLocalizacion.removeUpdates(listenerLocalizacion);
	}

	/**
	 * Actualiza el interfaz con la posición indicada, como la primera medida que se muestra es la
	 * última recordada por el dispositivo ésta puede ser nula porque puede que nunca se haya activado el
	 * GPS o la localización por redes inalambricas tampoco haya proporcionado una medida recientemente.
	 * @param loc La posicion a mostrar en el interfaz de usuario
	 */
	private void mostrarPosicion(Location loc) {
		if (loc != null) {
			precision = loc.getAccuracy();
			obtieneAltitud = loc.hasAltitude();
			lblLatitud.setText("Latitud: " + String.format("%.3fº", loc.getLatitude()));
			lblLongitud.setText("Longitud: "+ String.format("%.3fº", loc.getLongitude()));
			lblPrecision.setText("Precision: "+ String.format("%.3f", precision) + " metros");
			if (obtieneAltitud)
				this.lblAltura.setText("Altura: "+String.format("%.3f",loc.getAltitude()));
			else
				this.lblAltura.setText("Altura: No disponible");
				
			Log.i("",String.valueOf(loc.getLatitude() + " - "+ String.valueOf(loc.getLongitude())));
		} else {
			lblLatitud.setText("Latitud: (sin_datos)");
			lblLongitud.setText("Longitud: (sin_datos)");
			lblPrecision.setText("Precision: (sin_datos)");
			this.lblAltura.setText("Altura: No disponible");
		}
	}

	/*  Como se reciben actualizaciones de posicion de diferentes origenes necesitamos un criterio
	* para decidir en cada actualizacion si la nueva posición merece ser utilizada para mostrarse al 
	* usuario.
	* 
	*   La nueva medida reemplazara a la actual según criterios como la marca de tiempo(si es mucho
	*   mas nueva que la actual), la precisión y el origen de la medida (GPS o redes GSM y WIFI)
	*/
	protected boolean posicionEsMejor(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // Si no tenemos aun ninguna medida la nueva sera siempre mejor
	        return true;
	    }

	    // Se mira si la medida es significativamente mas nueva o más vieja que la mejor conocida
	    long diferenciaMomentos = location.getTime() - currentBestLocation.getTime();
	    boolean esMuchoMasNueva = diferenciaMomentos > SEGUNDOS_ACTUALIZACIONES;
	    boolean esMuchoMasVieja = diferenciaMomentos < -SEGUNDOS_ACTUALIZACIONES;
	    

	    // Han pasado mas de SEGUNDOS_ACTUALIZACIONES desde la posición actual, se prefiere la nueva
	    // posición ya que el usuario probablemente ha ido a una posicion distante
	    if (esMuchoMasNueva) {
	        return true;
	    // Si la nueva posición es muy anterior la descartamos    
	    } else if (esMuchoMasVieja) {
	        return false;
	    }
	    boolean esMasNueva = diferenciaMomentos > 0;

	    // La nueva medida se ha tomado en un marco de +SEGUNDOS_ACTUALIZACIONES,-SEGUNDOS_ACTUALIZACIONES
	    // respecto a la considerada actualmente, consideramos otros criterios
	    float diferenciaPrecision = location.getAccuracy() - currentBestLocation.getAccuracy();
	    boolean esMenosPrecisa = diferenciaPrecision > 0;
	    boolean esMasPrecisa = diferenciaPrecision < 0;
	    boolean esMuchoMenosPrecisa = diferenciaPrecision > 100;

	    // Comprobamos si la nueva medida y la actual vienen del mismo proveedor de posicionamiento
	    boolean tieneMismoOrigen = tienenMismoOrigen(location,currentBestLocation);

	    // Determine location quality using a combination of timeliness and accuracy
	    // Valoramos la calidad de la nueva medida combinando su precision y el momento en el que se tomó
	    if (esMasPrecisa) {
	        return true;
	    } else if (esMasNueva && !esMenosPrecisa) {
	        return true;
	    } else if (esMasNueva && !esMuchoMenosPrecisa && tieneMismoOrigen) {
	        return true;
	    }
	    return false;
	}

	/** Comprueba si dos origenes de posicionamiento son el mismo */
	private boolean tienenMismoOrigen(Location provider1, Location provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.getProvider().equals(provider2.getProvider());
	}
	
	/**
	 * Esta clase implementa las acciones que se tomaran cada vez que un proveedor de localizacion informe
	 * de un evento(nueva posicion disponible, proveedor activado,proveedor desactivado, estado modificado)
	 * 
	 *
	 */
	private class listenerLocalizacion implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if (posicionEsMejor(location, posicionActual)) {
				posicionActual = location;
			    mostrarPosicion(posicionActual);
			    lblEstado.setText("Origen: "+location.getProvider());
			}
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			lblEstado.setText("Estado de "+provider+": OFF");

		}

		@Override
		public void onProviderEnabled(String provider) {
			lblEstado.setText("Estado de "+provider+": ON");

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.i("", "Provider Status: " + status);
			if (status==LocationProvider.AVAILABLE)
			lblEstado.setText("Estado de "+provider+": Disponible");
			else if (status==LocationProvider.OUT_OF_SERVICE)
			lblEstado.setText("Estado de "+provider+": Fuera de servicio");
			else if (status==LocationProvider.TEMPORARILY_UNAVAILABLE)
				lblEstado.setText("Estado de "+provider+": Temporalmente no disponible");
		}
	}
}
