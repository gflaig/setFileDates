/**
 * @author gflaig
 *
 */
package FileDates;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.regex.*;



   //  @SuppressWarnings("unused")
public class FileDatesSet {
	/**
	 * FileDatesSet contains all methods needed to set file dates as 
	 * 			created date
	 * 			last modified date
	 * 			last accessed date
	 * depending on 
	 * 		command line paramters
	 * 		values from options file
	 * 		existing file/folder related dates
	 * 		current date
	 * 		
	 */
	
		
	String  create_base = "today",    create_random = "0",  create_offset = "0",  date_creation = "",
    		modify_base = "create",   modify_random = "0",  modify_offset = "0",  date_modify   = "",
    		access_base = "modify",   access_random = "0",  access_offset = "0",  date_access   = "",
    		allowit = "no" , logging = "no",
    		arg, optFile = "",  myPath = "", line1 = "",
	        laststring;

	int i = 0;
	
	
	int rndm,
    randval,
    fixval; 
	
    Long randm, randmc, randmm, randma,
	rndOffset = 0L, 
	rndModiOffset = 0L, 
	rndAccsOffset = 0L,

	milis_crea = 0L,
	milis_crea_val = 0l,
	milis_modi = 0L,
	milis_modi_val = 0l,
	milis_accs = 0L,
	milis_accs_val = 0l,
	
	createtime = 0L,
	modifytime = 0L,
	accesstime = 0L,

	todaytime = 0L,
    
	fixlong =0L,
	
	lastlong;
	
    public static final long miliPerDay = 86400000;
	
	Pattern ppath  = Pattern.compile("path:"),
			comment =  Pattern.compile("//"),
		    allow   =  Pattern.compile("allow:"),
			logs    =  Pattern.compile("log:"),
			// 
	  		crBase = Pattern.compile("create-base:"),  crOffs = Pattern.compile("create-offset:"),  crRand = Pattern.compile("create-random:"),
			moBase = Pattern.compile("modify-base:"),  moOffs = Pattern.compile("modify-offset:"),  moRand = Pattern.compile("modify-random:"), 
			acBase = Pattern.compile("access-base:"),  acOffs = Pattern.compile("access-offset:"),  acRand = Pattern.compile("access-random:"),

	    	lastpattern;
	
	Matcher matchVal;
	
	DateFormat dateforlog = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	Calendar calforlog = Calendar.getInstance();
	
	void readparm(String[] iargs) throws IOException, ParseException {
    /** ================================================================================================================== 
         *<br /> 
    	 *<br /> readparm first analyzes the argumants.
    	 *<br />>> -o ????? options points to the option file
    	 *<br />>> -p ????? points to the file system path of the file or of the tree of folders and files, that need dates changed 
    	 *<br />>> -c ????? defines, how creation dates have to be maintained
    	 *<br /> 
    	 *<br /> if -o exists 
    	 *<br /> 		readparm reads the options file and analyzes the values.
    	 *<br />
    	 *<br />>> path:?????              points to the file system path, and maybe overrides the -p argument
    	 *<br />
    	 *<br />>> create-base:?????       defines, how creation dates have to be maintained, and will overrides an existing -c argument
    	 *<br />   create-base:today       use current date  to calculate new creation dates
    	 *<br />   cretae-base:file        use creation date as it is existing for the file/folder to calculate new creation dates
    	 *<br />   create-base:yyyy-MM-dd  use date literal in the form: year dash MONTH dash day to calculate new creation dates (eg. 2011-10-23 )
    	 *<br /> 
    	 *<br />>> create-offset:???       add days to the creation date base, default is 0, can be negative
    	 *<br />   create-random:???       add a positive number of days randomly calculated to creation date base, default value is 0, for 0, no random value is calculated
    	 *<br />   create-offset:??? and create-random:??? can both be used concurrently
    	 *<br /> 
    	 *<br />>> modify-base:?????       defines, how last modification dates have to be maintained
		 *<br />   modify-base:today       use current date  to calculate new last modification dates
         *<br />   modify-base:file		   use last modification date as it is existing for the file/folder to calculate new last modification dates 
         *<br />   modify-base:create      use calculated creation date to calculate new last modification dates
         *<br />   modify-base:yyyy-MM-dd  use date literal in the form year dash MONTH dash day to calculate new last modification dates (eg. 2011-10-23 )
         *<br />
         *<br />>> modify-offset:???       add days to last modification date base, default value is 0, can be negative
         *<br />   modify-random:???       add a positive number of days randomly calculated to last modification date base, default value is 0, for 0, no random value is calculated
         *<br />   modify-offset:??? and modify-random:??? can both be used concurrently  
    	 *<br /> 
    	 *<br />>> access-base:?????       defines, how last accessed dates have to be maintained
		 *<br />   access-base:today       use current date  to calculate new accessed dates
         *<br />   access-base:file		   use last accessed date as it is existing for the file/folder to calculate new last accessed dates 
         *<br />   access-base:modify      use calculated last accessed date to calculate new last accessed dates
         *<br />   access-base:yyyy-MM-dd  use date literal in the form year dash MONTH dash day to calculate new last accessed dates (eg. 2011-10-23 )
         *<br />
         *<br />>> access-offset:???       add days to last accessed date base, default value is 0, can be negative
         *<br />   access-random:???       add a positive number of days randomly calculated to last accessed date base, default value is 0, for 0, no random value is calculated
         *<br />   access-offset:??? and access-random:??? can both be used concurrently  
         *<br />
         *<br />>> allow:??               allow, disallow inconsistent dates at files/folders, eg. future dates, modification dates smaller than creation dates, last accessed before last modified
         *<br />   allow:yes			  allow inconsistent dates	
         *<br />   allow:no               disallow inconsistent dates 
    	 *<br />
    	 *<br />>> log:?? 				  defines, how logging is performed 
    	 *<br />   log:no                 no logging at all (default)
    	 *<br />   log:parm               logging of input args and options is done
    	 *<br />   log:some				  some logging is done
    	 *<br />   log:all                all actions are logged
    	 *<br />
    	 *<br /> After interpreting arguments and options, readparm 
    	 *<br /> calls "decide" using the file system path 	
	 */

        if ( "all".equals(logging) || "ALL".equals(logging))
        {	
        	 System.out.println(dateforlog.format(calforlog.getTime()) + " FG0i0401: method readparm executes -");
        }
		/* ==================================================================================================================  
		 * Read and interprete arguments
		 */
	  	
        while (i < iargs.length && iargs[i].startsWith("-")) 
        	{
            		arg = iargs[i++]; 
            		// ----------------- argument and value
            		if 		(arg.equals("-options") || arg.equals("-o") || arg.equals("-O")) 
              				{ if (i < iargs.length) optFile = iargs[i++];       else System.err.println("FG0e1001: -o requires a filename"); }
            		else if (arg.equals("-path") || arg.equals("-p") || arg.equals("-P")) 
            				{ if (i < iargs.length) myPath = iargs[i++];        else System.err.println("FG0e1002: -p requires a file/folder name"); }
            		else if (arg.equals("-create") || arg.equals("-c") || arg.equals("-C")) 
            				{ if (i < iargs.length) create_base = iargs[i++];   else System.err.println("FG0e1003: -c requires a date value of the form yyyy-MM-dd");  }   	
            		else if (arg.equals("-log") || arg.equals("-l") || arg.equals("-L")) 
    				        { if (i < iargs.length) logging = iargs[i++];   else System.err.println("FG0e1004: -l requires a value");  }
        	}            
        if (i > iargs.length) System.err.println("FG0w1000: Usage: FileDates [-o|-option afile] [-p|-path apath] [-c|-create yyyy-MM-dd] ");
        
		/* ==================================================================================================================  
		 * Read and interprete options file if it exists
		 */
        if (!"".equals(optFile)) 
        {        	
        		try (BufferedReader in = new BufferedReader(new FileReader(optFile)))
        			{	while ((line1 = in.readLine()) != null) 
        			      { matchVal = comment.matcher(line1);
        					 if (!matchVal.find()) 
        					      {  matchVal = ppath.matcher(line1);	if (matchVal.find()) { myPath        = line1.substring(line1.indexOf(':') + 1); }
            					     matchVal = allow.matcher(line1);	if (matchVal.find()) { allowit       = line1.substring(line1.indexOf(':') + 1); }
            					     matchVal = logs.matcher(line1);	if (matchVal.find()) { logging       = line1.substring(line1.indexOf(':') + 1); }
            					     matchVal = crBase.matcher(line1);	if (matchVal.find()) { create_base   = line1.substring(line1.indexOf(':') + 1); }
        						 	 matchVal = crOffs.matcher(line1);	if (matchVal.find()) { create_offset = line1.substring(line1.indexOf(':') + 1); }
        							 matchVal = crRand.matcher(line1);	if (matchVal.find()) { create_random = line1.substring(line1.indexOf(':') + 1); }
        							 matchVal = moBase.matcher(line1);	if (matchVal.find()) { modify_base   = line1.substring(line1.indexOf(':') + 1); } 
        							 matchVal = moOffs.matcher(line1);	if (matchVal.find()) { modify_offset = line1.substring(line1.indexOf(':') + 1); } 
        							 matchVal = moRand.matcher(line1);	if (matchVal.find()) { modify_random = line1.substring(line1.indexOf(':') + 1); } 
        							 matchVal = acBase.matcher(line1);	if (matchVal.find()) { access_base   = line1.substring(line1.indexOf(':') + 1); }
        							 matchVal = acOffs.matcher(line1);	if (matchVal.find()) { access_offset = line1.substring(line1.indexOf(':') + 1); }
        							 matchVal = acRand.matcher(line1);	if (matchVal.find()) { access_random = line1.substring(line1.indexOf(':') + 1); }
        					      } 
        			     }
		           } catch (IOException e) { e.printStackTrace(); } 
        }
        if ( !"no".equals(logging) && !"NO".equals(logging))
        {
        	System.out.println(dateforlog.format(calforlog.getTime()) + " FG0i0005: value of log : " + logging + " -");
        	System.out.println(dateforlog.format(calforlog.getTime()) + " FG0i0010: options-file  : " + optFile + " -");
        	System.out.println(dateforlog.format(calforlog.getTime()) + " FG0i0011: value of path : " + myPath + " -");
        	System.out.println(dateforlog.format(calforlog.getTime()) + " FG0i0015: value of allowit : " + allowit + " -");
        	System.out.println(dateforlog.format(calforlog.getTime()) + " FG0i0020: creation date       - base : " + create_base + " - offset : " + create_offset + " - random : " + create_random + " -");
        	System.out.println(dateforlog.format(calforlog.getTime()) + " FG0i0030: laste modified date - base : " + modify_base + " - offset : " + modify_offset + " - random : " + modify_random + " -");
        	System.out.println(dateforlog.format(calforlog.getTime()) + " FG0i0040: last access date    - base : " + access_base + " - offset : " + access_offset + " - random : " + access_random + " -");
        }     
        /* ==================================================================================================================  
		 * call "decide" to differentiate between folder path and file path
		 */
        
    	decide(myPath);  
        if ( "some".equals(logging) || "SOME".equals(logging) || "all".equals(logging) || "ALL".equals(logging)) 
        {	
        	 System.out.println(dateforlog.format(calforlog.getTime()) + " FG0i0999: finished -");
        }
    }  // end method readparm
	
	
	
    /** ================================================================================================================== */
	void decide(String myPath) throws IOException, ParseException {
		/**
		 * decide is called 
		 * 		by the top level method "readparm" for the first path
		 *      by the "runfolder method" for children of folders
		 * decide calls the "runfolder" method when it detects a folder
		 * decide calls the "setdates" method, when it detects a file, to set 
		 * 		creation date, last modification date and last access date 
		 * for a file.      	
		 */
        if ( "all".equals(logging) || "ALL".equals(logging))
        {	
        	 System.out.println(dateforlog.format(calforlog.getTime()) + " FG0i0401: method decide executes -");
        }
		Path path1 = Paths.get(myPath);
		BasicFileAttributes basicAttributes = null;
		basicAttributes = Files.readAttributes(path1, BasicFileAttributes.class);
		if (basicAttributes.isDirectory()==true){
			runfolder(myPath);
         } else {
        	 setdates(myPath); 
         }
	}  // end method decide
	
	
	
	/** ================================================================================================================== */
	void runfolder(String inPath) throws IOException, ParseException {
		/**
		 * "runfolder" is called by method "decide"
		 * 		"runfolder" acts on a folder
		 *      "runfolder" calls "setdates" to set dates of the current folder in work
		 * 		"runfolder" then loops though all children paths, 
		 * 			calling the "decide" method, verifying if the child is a folder or a file
		 */
        if ( "all".equals(logging) || "ALL".equals(logging))
        {	
        	 System.out.println(dateforlog.format(calforlog.getTime()) + " FG0i0411: method runfolder executes -");
        }

		setdates(inPath);
		File fil = new File(inPath);

    	String allFiles[];
        allFiles=fil.list();

        for (int i = 0; i <allFiles.length; i++)            
          {
        	/* Attributes of Basic View */
        	Path filPath = Paths.get(inPath + "\\" + allFiles[i]);
        	String pathfound = inPath + "\\" + filPath.getFileName().toString();
        	decide(pathfound); 
          } 
        	
	}  // end method runfolder


	/** ================================================================================================================== */
	 void setdates(String datePath) throws IOException, ParseException { 
	    if ( "all".equals(logging) || "ALL".equals(logging))
	        {	
	        	 System.out.println(dateforlog.format(calforlog.getTime()) + " FG0i0421: method setdates executes -");
	        } 
		Path datePathPath = Paths.get(datePath);
	 
    	BasicFileAttributes basicAttributes = null;
		Random randam = new Random();


    	basicAttributes = Files.readAttributes(datePathPath, BasicFileAttributes.class);
    	DateFormat datfrm = new SimpleDateFormat("yyyy-MM-dd'T'");	
    	DateFormat datfix = new SimpleDateFormat("yyyy-MM-dd");	
    	
    	todaytime = System.currentTimeMillis();
    	/** ==================================================================================================================
    	 *  calculate creation time
    	 *    dependend on arguments and options file values   
    	 * ===================================================================================================================  
    	 */

    	// ================================================================================================================== 
    	// find creation time base value to be used, deliver in milliseconds as Long 	
    	if ( "today".equals(create_base)) {
			createtime = todaytime;	                       // use current date/time
		} else if ("file".equals(create_base)) {
			date_creation = basicAttributes.creationTime().toString();	// pickup "created date" from file/folder itself
			Calendar cal_crea  = Calendar.getInstance();  // only initialisation calender type variable	
			cal_crea.setTime(datfrm.parse(date_creation));  
			milis_crea	 = cal_crea.getTime().getTime();
			createtime = milis_crea;
		} else if (create_base != null  ){
		      try{  
				Calendar cal_crea  = Calendar.getInstance();  // only initialisation calender type variable	  
				cal_crea.setTime(datfix.parse(create_base));
				milis_crea	 = cal_crea.getTime().getTime();
				createtime = milis_crea;
		          } catch(ParseException e){
		        	  createtime = todaytime;  // when it is set invalid make it today
		           }
		} else createtime = todaytime;   // when it is NOT set make it today	
    	
    	// ================================================================================================================== 
    	// find creation time offset value to be used, deliver as Long 	
    	fixlong  = Long.valueOf(create_offset) * miliPerDay;
 
    	// ==================================================================================================================  	
    	// find creation time additional random offset value to be used, deliver as Long 	
    	randval = Integer.valueOf(create_random);  
    	rndm = 0;
    	if (randval > 0)  rndm = randam.nextInt(randval);	
    	randmc = (long) (rndm);
		rndOffset = randmc *  miliPerDay;	
		
	   	// ==================================================================================================================  	
    	// calculate complete creation time in milliseconds as Long	
		createtime = createtime + rndOffset + fixlong;	
		
	   	// ==================================================================================================================  	
		// only if allow is not YES
		// ensure createtime <= today 
		if ( !"yes".equals(allowit) && !"YES".equals(allowit)) 
			{ if (createtime > todaytime) createtime = todaytime;
		}	
		
		// ==================================================================================================================  	
    	// finally add creation time to file 
		FileTime fileTime_crea = FileTime.fromMillis(createtime);
				
    	/** ==================================================================================================================
    	 *  calculate last modified time
    	 *    dependend on arguments and options file values   
    	 * ===================================================================================================================  
    	 */

    	// ================================================================================================================== 
    	// find last modified time base value to be used, deliver in milliseconds as Long 	
    	if ( "today".equals(modify_base)) {
			modifytime = todaytime;	                       // use current date/time
		} else if ("file".equals(modify_base)) {
			date_modify = basicAttributes.lastModifiedTime().toString();	// pickup "last modified date" from file/folder itself
			Calendar cal_modi  = Calendar.getInstance();  // only initialisation calender type variable	
			cal_modi.setTime(datfrm.parse(date_modify));  
			milis_modi	 = cal_modi.getTime().getTime();
			modifytime = milis_modi;
		} else if ( "create".equals(modify_base)) {
			modifytime = createtime;
		} else if (modify_base != null  ){
		      try{  
				Calendar cal_modi  = Calendar.getInstance();  // only initialisation calender type variable	  
				cal_modi.setTime(datfix.parse(modify_base));
				milis_modi	 = cal_modi.getTime().getTime();
				modifytime = milis_modi;
		          } catch(ParseException e){
		        	  modifytime = createtime;  // when it is set invalid make it equals the creation time
		           }
		} else modifytime = createtime;   // when it is NOT set make it equals the creation time	
    	
    	
    	// ==================================================================================================================  	
    	// find last modified time offset value to be used, deliver as Long 	
    	fixlong  = Long.valueOf(modify_offset) * miliPerDay;
 
    	// ==================================================================================================================  	 	
    	// find last modified time additional random offset value to be used, deliver as Long 	
    	randval = Integer.valueOf(modify_random);  
    	rndm = 0;
    	if (randval > 0)  rndm = randam.nextInt(randval);	
    	randmc = (long) (rndm);
		rndOffset = randmc *  miliPerDay;	

	   	// ==================================================================================================================  	
    	// calculate complete last modified time in milliseconds as Long		
		modifytime = modifytime + rndOffset + fixlong;	
				
	   	// ==================================================================================================================  	
		// only if allow is not YES
		// ensure last modify time <= today AND last modify time >= create time
		if ( !"yes".equals(allowit) && !"YES".equals(allowit)) 
			{ 
			if (modifytime > todaytime) modifytime = todaytime;
			else if (modifytime < createtime) modifytime = createtime;
		}	
		
		// ==================================================================================================================  	
    	// finally add last modified time to file 
		FileTime fileTime_modi = FileTime.fromMillis(modifytime);

    	/** ==================================================================================================================
    	 *  calculate last accessed time
    	 *    dependend on arguments and options file values   
    	 * ===================================================================================================================  
    	 */

    	// ================================================================================================================== 
    	// find last accessed time base value to be used, deliver in milliseconds as Long 	
    	if ( "today".equals(access_base)) {
			accesstime = todaytime;	                       // use current date/time
		} else if ("file".equals(access_base)) {
			date_access = basicAttributes.lastAccessTime().toString();	// pickup "last access date" from file/folder itself
			Calendar cal_accs  = Calendar.getInstance();  // only initialisation calender type variable	
			cal_accs.setTime(datfrm.parse(date_access));  
			milis_accs = cal_accs.getTime().getTime();
			accesstime = milis_accs;
		} else if ( "modify".equals(access_base)) {
			accesstime = createtime;
		} else if (access_base != null  ){
		      try{  
				Calendar cal_accs  = Calendar.getInstance();  // only initialisation calender type variable	  
				cal_accs.setTime(datfix.parse(access_base));
				milis_accs	 = cal_accs.getTime().getTime();
				accesstime = milis_accs;
		          } catch(ParseException e){
		        	  accesstime = modifytime;  // when it is set invalid make it equals the last modified time
		           }
		} else accesstime = modifytime;   // when it is NOT set make it equals the last modified time	
    	
    	// ==================================================================================================================  	
    	// find last accessed time offset value to be used, deliver as Long 	
    	fixlong  = Long.valueOf(access_offset) * miliPerDay;
 
    	// ==================================================================================================================  	   	
    	// find last accessed time additional random offset value to be used, deliver as Long 	
    	randval = Integer.valueOf(access_random);  
    	rndm = 0;
    	if (randval > 0)  rndm = randam.nextInt(randval);	
    	randmc = (long) (rndm);
		rndOffset = randmc *  miliPerDay;	

	   	// ==================================================================================================================  	
    	// calculate complete last access time in milliseconds as Long	
		accesstime = accesstime + rndOffset + fixlong;	
		
	   	// ==================================================================================================================  	
		// only if allow is not YES
		// ensure last access time <= today AND last access time >= last modified time
		if ( !"yes".equals(allowit) && !"YES".equals(allowit)) 
			{ 
			if (accesstime > todaytime) accesstime = todaytime;
			else if (accesstime < modifytime) accesstime = modifytime;
		}	
				
		// ==================================================================================================================  	
    	// finally add last access time to file time
		FileTime fileTime_accs = FileTime.fromMillis(accesstime);
		
		/** ==================================================================================================================
    	 *  physically perform changes of file dates to file/folder worked on  
    	 * ===================================================================================================================  
    	 */
   		Files.getFileAttributeView(datePathPath, BasicFileAttributeView.class).setTimes(fileTime_modi, fileTime_accs, fileTime_crea);
		
        if ( "all".equals(logging) || "ALL".equals(logging))
        {	
        	 System.out.println(dateforlog.format(calforlog.getTime()) + " FG0i0601: file/folder handled : " + datePath + " -");
        }

        
	}  // end method setdates
	 	 
	 
	 
	 /** ================================================================================================================== */     
}   // end class FileDatesSet


