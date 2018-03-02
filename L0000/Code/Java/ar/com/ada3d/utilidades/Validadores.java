package ar.com.ada3d.utilidades;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

public class Validadores {

	
	 /** 
	  * Validación del número de CUIT / CUIL. 
	  * @param number Número a validar. 
	  * @throws DocumentException cuando el número de CUIT / CUIL no es 
	  * válido. Salvo que sea 0 que no lo valido 
	  */ 
	 
	public void validateCUIT(FacesContext facesContext, UIComponent component,
			Object value) {

		String number = value.toString();
		if (number.equals("0"))
			return;
		boolean res = false;

		if (number != null && number.trim().length() != 0) {
			number = number.trim();
			try {
				int[] magicValues = { 5, 4, 3, 2, 7, 6, 5, 4, 3, 2 };
				int[] values = new int[11];
				int i;
				int sum = 0;

				number = number.replace("-", "");

				if (number.length() == 11) {
					for (i = 0; i < 11; i++)
						values[i] = Integer
								.parseInt(number.substring(i, i + 1));

					int checkDigit = values[10];

					for (i = 0; i < 10; i++)
						sum = sum + values[i] * magicValues[i];

					int dividend = sum / 11;
					int product = dividend * 11;
					int substraction = sum - product;
					checkDigit = (substraction > 0) ? 11 - substraction
							: substraction;

					res = (checkDigit == values[i]);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (!res) {
			FacesMessage message = new FacesMessage("El CUIT es inválido.");
			throw new ValidatorException(message);
		}
	}
  
}
