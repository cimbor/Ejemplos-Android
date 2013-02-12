package com.example.botontoggle;

/**
 * Ejemplo de uso de u boton tipo toggle; se comprueba su estado (pulsado o libre) y se modifica el estado
 * de otra vista en función de él, en este caso el color de fondo de una vista TextView.
 * 
 * @author Marco Antonio Martín Callejo
 */
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class BotonToggle extends Activity {
	ToggleButton botToggle;
	TextView tvObjetivo;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		tvObjetivo = (TextView) findViewById(R.id.textoSaludo);
		tvObjetivo.setBackgroundColor(Color.RED);
		botToggle = (ToggleButton) findViewById(R.id.ToggleButton01);

	}

	/**
	 * Gestiona el evento de pulsar el boton toggle, comprobando si esta
	 * activado o desactivado por medio de su metodo "isChecked" y actuando en
	 * consecuencia sobre la vista de texto que queremos modificar.
	 */

	public void onClick(View v) {
		if ((botToggle.isChecked())) {
			System.out.println("checked");
			tvObjetivo.setBackgroundColor(Color.YELLOW);
		} else {
			System.out.println("Unchecked");
			tvObjetivo.setBackgroundColor(Color.RED);
		}
	}
}