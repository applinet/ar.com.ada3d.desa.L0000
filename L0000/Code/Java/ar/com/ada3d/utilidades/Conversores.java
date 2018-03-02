package ar.com.ada3d.utilidades;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Conversores {

	/**
	 * - Funcion StringToDate - Parseo un string y saco un JavaDate. Este
	 * formato sirve para un NotesDateTime
	 * 
	 * @param inputFormat
	 *            : String que voy a transformar
	 * @param inputTimeStamp
	 *            : Formato del String que ingresa(inputFormat). ej:"yyyyMMdd"
	 */
	public static Date StringToDate(final String inputFormat,
			String inputTimeStamp) {
		//Arreglo ya que como es un nro puede venir un blanco (cero a la izquierda) 
		if (inputFormat.length() > inputTimeStamp.length())
			inputTimeStamp = "0" + inputTimeStamp;
			
		SimpleDateFormat formatter = new SimpleDateFormat(inputFormat);
		Date convertedDate = null;
		try {
			convertedDate = (Date) formatter.parse(inputTimeStamp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return convertedDate;

	}

	/**
	 * - Funcion StringToDate - Parseo un JavaDate y saco un String.
	 * 
	 * @param prm_fecha
	 *            : dato de input
	 * @param prm_formato
	 *            : Como quiero la salida. ej:"yyyyMMdd"
	 */
	public static String DateToString(Date prm_fecha, String prm_formato) {
		SimpleDateFormat formatter = new SimpleDateFormat(prm_formato);
		// String folderName = formatter.format(prm_fecha);
		return formatter.format(prm_fecha);
	}

	/**
	 * - Funcion StringDateToStringDate - Parseo un string con formato de
	 * ingreso y egreso. Este formato sirve para un NotesDateTime
	 * 
	 * @Ejemplo parseo: new SimpleDateFormat("dd/MM/yyyy hh:mm").format(new
	 *          SimpleDateFormat("yyyyMMdd").parse("20140625")).toString();
	 * 
	 * @param inputFormat
	 *            : String que voy a transformar
	 * @param inputTimeStamp
	 *            : Formato del String que ingresa(inputFormat). ej:"yyyyMMdd"
	 * @param outputFormat
	 *            : El formato que va a tener el dt que devuelvo
	 *            ej:"dd/MM/yyyy hh:mm"
	 */
	public static String StringDateToStringDate(final String inputFormat,
			String inputTimeStamp, final String outputFormat) {

		String fecha = null;
		try {
			fecha = new SimpleDateFormat(outputFormat).format(
					new SimpleDateFormat(inputTimeStamp).parse(inputFormat))
					.toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return fecha;
	}

	
	
	
	public static BigDecimal stringToBigDecimal(String formattedString,Locale locale, int decimales) {
		
		final DecimalFormatSymbols symbols;
		final char groupSeparatorChar;
		final String groupSeparator;
		final char decimalSeparatorChar;
		final String decimalSeparator;
		String fixedString;
		final BigDecimal number;

		symbols = new DecimalFormatSymbols(locale);
		groupSeparatorChar = symbols.getGroupingSeparator();
		decimalSeparatorChar = symbols.getDecimalSeparator();

		StringBuilder str = new StringBuilder(formattedString);
		str.insert(formattedString.length()-decimales, decimalSeparatorChar);
		
		if (groupSeparatorChar == '.') {
			groupSeparator = "\\" + groupSeparatorChar;
		} else {
			groupSeparator = Character.toString(groupSeparatorChar);
		}

		if (decimalSeparatorChar == '.') {
			decimalSeparator = "\\" + decimalSeparatorChar;
		} else {
			decimalSeparator = Character.toString(decimalSeparatorChar);
		}

		fixedString = str.toString().replaceAll(groupSeparator, "");
		fixedString = fixedString.replaceAll(decimalSeparator, ".");
		number = new BigDecimal(fixedString);
		return (number);
	}
	
	public static String stringToStringDecimal(String formattedString,Locale locale, int decimales) {
		final DecimalFormatSymbols symbols;
		final char decimalSeparatorChar;
		symbols = new DecimalFormatSymbols(locale);
		decimalSeparatorChar = symbols.getDecimalSeparator();
		
		formattedString = formattedString.length() < 3 ? "000" + formattedString : formattedString; 
		StringBuilder str = new StringBuilder(formattedString);
		
		str.insert(formattedString.length()-decimales, decimalSeparatorChar);
		return str.toString();
	}

	
	//Convert Date to Calendar
	public static Calendar dateToCalendar(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;

	}

	//Convert BigDecimal to String AS400 (12,16 = 1216) (0,01 = 1)
	
	public static String bigDecimalToAS400(BigDecimal prm_importe, Integer prm_decimales){
		if (prm_decimales.equals(0)){
			return prm_importe.toString().replace(",", "").replace(".", "");			
		}
		prm_importe = prm_importe.setScale(prm_decimales, BigDecimal.ROUND_HALF_UP);
		return prm_importe.toString().replace(",", "").replace(".", "");
	}
	
	public static ArrayList<String> mergeTwoArrayRemoveDuplicate(ArrayList<String> arr1, ArrayList<String> arr2){
		HashSet<String> retorno = (HashSet<String>) new HashSet<String>();
		retorno.addAll(arr1);
		retorno.addAll(arr2);
		List<String> ret = new ArrayList<String>(retorno);
		return (ArrayList<String>) ret;
	}
}
