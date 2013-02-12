package com.example.menucheckbox;

/**
 * Ejemplo de creacion de un menu y submenus en Android. Despues de diseñar el menu por medio del recurso
 * menu.xml contenido en el directorio menu se implementa el evento onCreateOptionsMenu para que se "infle"
 * el menu a partir del recurso XML. Para implementar las acciones de los menus y submenus hay que completar
 * el evento onOptionsItemSelected.
 * 
 * En los dos eventos implementados podemos distinguir la opcion seleccionada interrogando al objeto
 * Item que onOptionsItemSelected recibe por parametro.
 * 
 * En este ejemplo las opciones modifican el mensaje del TextView contenido en el layout principal, y la 
 * tercera opcion del menu principal es la que contiene un submenu.
 */
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MenuCheckBox extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	/**
	 * Evento que se llama cada vez que pulsamos una opcion en un menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		TextView saludo = (TextView) this.findViewById(R.id.texto_saludo);
	    switch (item.getItemId()) {
	        case R.id.MnuOpc1:
	        	saludo.setText("Opcion 1 pulsada!");
	            return true;
	        case R.id.MnuOpc2:
	        	saludo.setText("Opcion 2 pulsada!");
	            return true;
	        case R.id.sub31:
	        	saludo.setText("Subopcion 1 de opcion 3 pulsada!");
	            return true;
	        case R.id.sub32:
	        	saludo.setText("Subopcion 2 de opcion 3 pulsada!");
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
