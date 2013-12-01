Repository: setFileDates
============
flexibly set creation date, last modified date and last accessed date of files and folders

-------------------------------------------------------------------------------------------------------------
package: FileDatesSet
-------------------------------------------------------------------------------------------------------------
currently consists of 2 classes
<br>GUI class / Dojo based gui as creator of options file <b>planned</b>


-------------------------------------------------------------------------------------------------------------
class: FileDates
-------------------------------------------------------------------------------------------------------------
"FileDates" class carries the "main" method of the package "FileDatesSet" 
<br>the "main" method of "FileDates" class reads in arguments and forwards them to 
<br>	method "readparm" of class "FileDates" of the package "FileDatesSet"

-------------------------------------------------------------------------------------------------------------
class: FileDatesSet
-------------------------------------------------------------------------------------------------------------
"FileDatesSet" helds all code containg logic of the package "FileDatesSet"
<br>"FileDatesSet" is referred to by "main" method within "FileDates" class

-------------------------------------------------------------------------------------------------------------
method: readparm  
-------------------------------------------------------------------------------------------------------------
"readparm" first analyzes the arguments:
>> -o ????? options points to the option file
>> -p ????? points to the file system path of the file or of the tree of folders and files, that need dates changed 
>> -c ????? defines, how creation dates have to be maintained
>> -l ????? defines, how logging is performed 
 if -o exists 
 		readparm reads the options file and analyzes the values.

>> lines with // are treated as comment

>> lines in the form [sometext] are labels and are not processed

>> path:?????              points to the file system path, and maybe overrides the -p argument

>> create-base:?????       defines, how creation dates have to be maintained, and will overrides an existing -c argument
   create-base:today       use current date  to calculate new creation dates
   cretae-base:file        use creation date as it is existing for the file/folder to calculate new creation dates
   create-base:yyyy-MM-dd  use date literal in the form: year dash MONTH dash day to calculate new creation dates (eg. 2011-10-23 )
 
>> create-offset:???       add days to the creation date base, default is 0, can be negative
   create-random:???       add a positive number of days randomly calculated to creation date base, default value is 0, for 0, no random value is calculated
   create-offset:??? and create-random:??? can both be used concurrently
 
>> modify-base:?????       defines, how last modification dates have to be maintained
   modify-base:today       use current date  to calculate new last modification dates
   modify-base:file		   use last modification date as it is existing for the file/folder to calculate new last modification dates 
   modify-base:create      use calculated creation date to calculate new last modification dates
   modify-base:yyyy-MM-dd  use date literal in the form year dash MONTH dash day to calculate new last modification dates (eg. 2011-10-23 )

>> modify-offset:???       add days to last modification date base, default value is 0, can be negative
   modify-random:???       add a positive number of days randomly calculated to last modification date base, default value is 0, for 0, no random value is calculated
   modify-offset:??? and modify-random:??? can both be used concurrently  
 
>> access-base:?????       defines, how last accessed dates have to be maintained
   access-base:today       use current date  to calculate new accessed dates
   access-base:file		   use last accessed date as it is existing for the file/folder to calculate new last accessed dates 
   access-base:modify      use calculated last accessed date to calculate new last accessed dates
   access-base:yyyy-MM-dd  use date literal in the form year dash MONTH dash day to calculate new last accessed dates (eg. 2011-10-23 )

>> access-offset:???       add days to last accessed date base, default value is 0, can be negative
   access-random:???       add a positive number of days randomly calculated to last accessed date base, default value is 0, for 0, no random value is calculated
   access-offset:??? and access-random:??? can both be used concurrently  

>> allow:??               allow, disallow inconsistent dates at files/folders, eg. future dates, modification dates smaller than creation dates, last accessed before last modified
   allow:yes			  allow inconsistent dates	
   allow:no               disallow inconsistent dates (default)

>> log:?? 				  defines, how logging is performed 
   log:no                 no logging at all (default)
   log:parm               logging of input args and options is done
   log:some				  some logging is done
   log:all                all actions are logged

<br> After interpreting arguments and options, 
<br> readparm calls "decide" using the file system path 	
 
-------------------------------------------------------------------------------------------------------------
method: decide
-------------------------------------------------------------------------------------------------------------
"decide" is called 
<br> . .    by the top level method "readparm" for the first path
<br> . .    by the "runfolder method" for children of folders
<br>"decide" calls the "runfolder" method when it detects a folder
<br>"decide" calls the "setdates" method, when it detects a file, to set 
<br> . .  .  creation date, last modification date and last access date for a file.   

-------------------------------------------------------------------------------------------------------------
method: runfolder
-------------------------------------------------------------------------------------------------------------
<br>"runfolder" is called by method "decide"
<br>"runfolder" acts on a folder
<br>"runfolder" calls "setdates" method to set dates of the current folder in work
<br>"runfolder" then loops though all children paths, 
<br> . . .	calling the "decide" method, verifying if the child is a folder or a file		

-------------------------------------------------------------------------------------------------------------
method: setdates
-------------------------------------------------------------------------------------------------------------
<br>"setdates" is called by method "runfolder" to set dates of the current folder
<br>"setdates" is called by method "decide  to set dates of a file
<br>"setdates" detrmines the values for
<br> . .	to be set creation time
<br> . .	to be set last modified time
<br> . .	to be set last accessed time
<br> . 	by interpreting arguments and values of the option file
<br>"setdates" method ensures consistency of dates if requested.

-------------------------------------------------------------------------------------------------------------
		
		
