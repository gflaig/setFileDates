/**
 * @author gflaig
 *
 */

package FileDates;

import java.io.IOException;
import java.text.ParseException;

public class FileDates {

	public static void main (String[] args) throws IOException, ParseException, InterruptedException {
		/**
		 * main method of package "FileDatesSet",
		 * 				execution code transferred to class FileDatesSet, 
		 * 				to enable threading easily if needed   
		 */
       FileDatesSet runsetdate = new FileDatesSet(); 
       runsetdate.readparm(args);
    }


}
