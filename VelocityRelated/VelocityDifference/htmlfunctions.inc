
##
## added 06-23-2005 by Marco Georgi
## Macro for replacing language specific labels
#macro( plabel $locallabel $default )
#if($locallabel)${locallabel}#else${default}#end
#end
##


##
## added 11-03-2006 by Marco Georgi
## Macro for loading language specific labels to variables
#macro( llabel $targetvar $locallabel $default )
	In llable: targetvar=$targetvar, locallabel=$locallabel, default=$default
#if ($locallabel) #set($targetvar = $locallabel) #else #set($targetvar = $default) #end
	Quitting llable: targetvar=$targetvar, locallabel=$locallabel, default=$default
#end
##
##



