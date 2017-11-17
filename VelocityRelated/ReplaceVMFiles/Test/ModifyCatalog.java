#set($actionCode = 0)

#if($siQty = 'Unlimited')

#parse ("acc_ctrl_head.inc")
#if($isLoggedOn && $accessAllowed)

#if (("$USAll" == ($abc)))
	#set ($zoneNames = 	"$zoneNames$delim$longName")
#end
#if (("$!longName" != (0)))
	#set ($zoneNames = 	"$zoneNames$delim$longName")
#end

#if (("$USAll" == 0))
	#set ($zoneNames = 	"$zoneNames$delim$longName")
#end

#if (($!longName != "Hawaii/Alaska") && ($longName != "Continental US") )
	#set ($zoneNames = 	"$zoneNames$delim$longName")
#end

#if("$!itemCnt" == "!$itemsToShow") abc 
#end

					
##if (			$isAdminSearch == "Y") #end

#if(${msc_id != ""})
  Page = $!{msc_id}&nbsp;
#end

#if("$!selectVal" == "%$!{value}%")selected #end

#if($myObject.prop == "$!itemsToShow") abc #end

#if($myObject.getProp() == "$!itemsToShow") abc #end

#if($myObject.prop == "$!itemsToShow") abc #end
<input #if("$!siteSize"==0)disabled#end type="button" value="#plabel($label_assign_site_button "Assign Site")" onClick="javascript:assignSite()"></td><td>&nbsp;</td></tr>
#end
