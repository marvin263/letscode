
### SL: July 2007: If content ID is NOT defined and a title IS defined in CMS > STOREFRONT > MANAGE STORE DESIGN > MANAGE STORE PAGES	
### use it to populate meta tag title as well as modules that display page title in a page heading.
### The individual content module will define the page title so we will not overwrite it.
### The spg_title value is pulled in from StoreLayoutModel.inc
#if("$!ct" == "" && "$!spg_title" != "")
	#set($pageTitle = "$!spg_title")
#end

### SL: Nov 21, 2006	
### Check that pageTitle is set so that we do not show a blank or incorrect page title
#if("$!pageTitle" == "")
	#set($pageTitle = "$storeName")
#end

#if("$!pageID" == "homePage")
		###Home Page Title
		<meta http-equiv="IMAGETOOLBAR" content="NO">
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		## Commenting out these as these are just samples. Clients should not be using these
		#**
		<meta name="description" content="$compNam. For fitness and health, leisure and pleasure we strive to provide the very best deals in sports supplements, home fitness equipment, outdoor living, personal care and garden accessories. Checkout our great offers for fitness and fun.">
		<meta name="keywords" content="$compNam, Kitchen, Home, Computing, Low prices, Digital, Xbox 360, HD Ready, Ipod, TV, Plasma, LCD, Playstation, PS2, PSP, GPS, Washing Machine, Fridge, Freezer, MP3, DAB, DVD, DVD Recorder, Dyson, PDA, Cooker, Oven, Dryer, Dishwasher, Television, Camera, Camcorder, Sat Nav, Gaming, Console, PC, Computer, Laptop, Digital Switchover, Printer, Mobile, Telephone, Phone, Satellite Navigation, Gifts, Refrigeration">
		*#
		<meta name="ROBOTS" content="index, follow">
		<meta http-equiv="Expires" content="Mon, 01 Jan 1996 01:01:01 GMT">	
		<meta name="title" content="$compNam">
		<title>$pageTitle</title>
#elseif("$!pageID" == "giftRegistryManage")
		#*
		DHU: 09/13/2011
		Adding these content when pageID is 'giftRegistryManage', in order to share content on Facebook
		<link rel="image_src" href="$img" />
		<meta name="description" content="$text2" />
		<meta name="title" content="$!title" />
		*#
		#set($shareURL = "$vmUtil.getRequestSiteHostURL().siteHostURL/mygiftlist/$!{giftregistry.id}")
		#set($coName = $vmUtil.trim("$!{giftregistry.coRegistrantFirstName} $!{giftregistry.coRegistrantLastName}"))
		#set($shareContentForTwitter = "$!{giftregistry.Name}#if($!coName != '') & $coName#end\'s Registry")
		#set($title = "$!{giftregistry.Name}#if($!coName != '') & $coName#end's Registry #if($!storeName != '')at $!storeName #end")
		#set($twitter_msg = "$!{shareContentForTwitter} $!{shareURL}")
		#set($description = "$!{giftregistry.Name}#if($!coName != '') & $coName are#else is#end excited to announce their gift registry. Check out their registry list to gift a gift they will love for their special occasion.")
		<meta http-equiv="IMAGETOOLBAR" content="NO"/>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
		<meta name="description" content="$!description"/>
		<meta name="keywords" content="$compNam, Kitchen, Home, Computing, Low prices, Digital, Xbox 360, HD Ready, Ipod, TV, Plasma, LCD, Playstation, PS2, PSP, GPS, Washing Machine, Fridge, Freezer, MP3, DAB, DVD, DVD Recorder, Dyson, PDA, Cooker, Oven, Dryer, Dishwasher, Television, Camera, Camcorder, Sat Nav, Gaming, Console, PC, Computer, Laptop, Digital Switchover, Printer, Mobile, Telephone, Phone, Satellite Navigation, Gifts, Refrigeration">
		<meta name="ROBOTS" content="index, follow"/>
		<meta http-equiv="Expires" content="Mon, 01 Jan 1996 01:01:01 GMT"/>	
		<meta name="title" content="$!title"/>
		<link rel="image_src" href="$!compLogo" />
		<title>$!title</title>
###Karen Wang 11/10/2005: generate all other pages' title dynamically from the database
#elseif( $vmUtil.isNotBlank($catParentID) || $vmUtil.isNotBlank($scId) ) ##$catParentID is the same as $scId
		## #parse( $vmUtil.getCustomizablePage("titleGenerator.inc") ) ## Commented out as $storeCategoryDefaultName is used below instead
		<meta http-equiv="IMAGETOOLBAR" content="NO">
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		
		## Use the database configured meta keywords and description below
		#***
		<meta name="description" content="$compNam. For fitness and health, leisure and pleasure we strive to provide the very best deals in sports supplements, home fitness equipment, outdoor living, personal care and garden accessories. Checkout our great offers for fitness and fun.">
		<meta name="keywords" content="$compNam, Kitchen, Home, Computing, Low prices, Digital, Xbox 360, HD Ready, Ipod, TV, Plasma, LCD, Playstation, PS2, PSP, GPS, Washing Machine, Fridge, Freezer, MP3, DAB, DVD, DVD Recorder, Dyson, PDA, Cooker, Oven, Dryer, Dishwasher, Television, Camera, Camcorder, Sat Nav, Gaming, Console, PC, Computer, Laptop, Digital Switchover, Printer, Mobile, Telephone, Phone, Satellite Navigation, Gifts, Refrigeration">
		*#
		
		## $scMetaTagKeywords and $!scMetaTagDescription are loaded in storeCategoryDetailsModel.inc
		#if("$!scMetaTagKeywords"!="")
			<meta name="keywords" content="$scMetaTagKeywords">
		#end
			
		#if("$!scMetaTagDescription"!="")
			<meta name="description" content="$scMetaTagDescription">
		#end			
		
		<meta name="ROBOTS" content="index, follow">
		<meta http-equiv="Expires" content="Mon, 01 Jan 1996 01:01:01 GMT">	
	
		## $storeCategoryDefaultName loaded in storeCategoryModel.inc
		## storeCategoryModel.inc included in StoreDirectory.vm & CategoryLanding.vm
		<meta name="title" content="$pageTitle">
		<title>$pageTitle</title>		
	
#else
		<meta http-equiv="IMAGETOOLBAR" content="NO">
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<meta name="description" content="$compNam. For fitness and health, leisure and pleasure we strive to provide the very best deals in sports supplements, home fitness equipment, outdoor living, personal care and garden accessories. Checkout our great offers for fitness and fun.">
		<meta name="keywords" content="$compNam, Kitchen, Home, Computing, Low prices, Digital, Xbox 360, HD Ready, Ipod, TV, Plasma, LCD, Playstation, PS2, PSP, GPS, Washing Machine, Fridge, Freezer, MP3, DAB, DVD, DVD Recorder, Dyson, PDA, Cooker, Oven, Dryer, Dishwasher, Television, Camera, Camcorder, Sat Nav, Gaming, Console, PC, Computer, Laptop, Digital Switchover, Printer, Mobile, Telephone, Phone, Satellite Navigation, Gifts, Refrigeration">
		<meta name="ROBOTS" content="index, follow">
		<meta http-equiv="Expires" content="Mon, 01 Jan 1996 01:01:01 GMT">	
		<meta name="title" content="$compNam $pageTitle ">
		<title>$storeName $pageTitle</title>
		
#end


### this variable sets the sort type, default to price
#set($item_orderby = "si_price")

### Before we do the include, we will allow different sorting of the items.
#parse ("model/showitemsModel.inc")

### this variable sets the row limit, before we start a new row
#set($rowLimit = 1)

### this is the counter of the number of items we've displayed
#set($itemCnt = 0)

#if($!wishlistEnabled == 'Y') <input type="hidden" name="wishlistAction" value="addStoreItem"> #end
#if($!giftregistryEnabled == 'Y') <input type="hidden" name="giftregistryAction" value="addStoreItem"> #end

<table name="sfitemlist" align="center" width="100%" border="1" cellspacing="0" cellpadding="1">
<tr>
<th>#plabel($label_preview "Preview")</th>

<th>#plabel($label_Title "Title") (SKU #)</th>
<th>#plabel($label_Price "Price")</th>
<th>#plabel($label_Price "Category")</th>
<th>#plabel($label_BuythisItem "Buy this Item")</th>
</tr>

<!-- START REPEAT -->
#foreach($showitemrow in $showitemresult)
	### Divide the count by the row limit
	#set($startmod = $itemCnt % $rowLimit)

	### set the variables for the result set
	#set($storeitemID = "$!V_si_id")
	#set($title = "$!V_si_title")
	#set($retailprice = "$!V_csku_retail_price")
	#set($itemprice = "$!V_si_price")
	#set($thumbImgPath = "$!V_si_thmburl")
	#set($storecatID = "$V_sc_id")
	#set($storecatName = "$V_sc_name")
	#set($isMasterSKU = "$!V_csku_master")
	#set($csku_id = "$!V_csku_id")
	
	#set($cskuName = "$!V_csku_name")
	#if("$!V_csku_qunlimited"=='Y' || $vmUtil.parseInt($V_csku_quantity)>0 )
		#set($isAvailable = true)
	#else 
		#set($isAvailable = false)
	#end		
	
	### Start a new row.
	#if($startmod == 0)
	<!-- Start Product line 1-->
	<TR align=center>
	#end	    

	### increment the counter
	#set($itemCnt = $itemCnt + 1)	
	
	<TD HEIGHT=100>
	#if("$!thumbImgPath"=="")
		<font class="text">No Image Available</font>
	#else
		<A HREF="/edealinv/servlet/ExecMacro?nurl=control/StoreItem.vm&ctl_nbr=$ctl_nbr&siId=$!storeitemID&catParentID=$storecatID&scId=$storecatID&catLevel=$!catLevel&parentCatName=$!parentCatName"><IMG SRC="$imagePath$!thumbImgPath" BORDER=0 ALT="$!title"></A>
	#end					
	</TD>
	<TD>
		<A HREF="/edealinv/servlet/ExecMacro?nurl=control/StoreItem.vm&ctl_nbr=$ctl_nbr&siId=$!storeitemID&catParentID=$storecatID&scId=$storecatID&catLevel=$!catLevel&parentCatName=$!parentCatName">$!title</A><br>
		(#plabel($label_storeitem "store item") # $storeitemID, sku # $!cskuName)	
	</TD>
	<TD>#if("$!itemprice"!="0.00") <B>$!curr_sign$!itemprice</B> #else N/A #end</TD>
	<TD>$storecatName</TD>
	
	<TD align="center">
		<TABLE>
			<TR>
				<TD>
					#plabel($label_Qty "Qty") 
							
				</TD> 
				<TD>
					<input type="text" value="1" class="textinput" name="qty_$itemCnt" size="2" maxlength="4" align="absmiddle">
					<br>&nbsp; #parse("cskuonbackorder.inc")
				</TD>
			</TR>
			
			<TR>
				<TD colspan="2">
				### TKO: 04/12/2006
				### Master SKUs should go to the product page to add an item.
				#if("$!V_csku_master"=="Y")
					<input #if(!$isAvailable)disabled#end type="button" value="#plabel($label_addtocart "Select Options")" onClick="javascript:window.location='/edealinv/servlet/ExecMacro?nurl=control/StoreItem.vm&ctl_nbr=$ctl_nbr&siId=$!storeitemID&catParentID=$storecatID&scId=$storecatID&catLevel=$!catLevel&parentCatName=$!parentCatName'">
				#else
					#if("$!addShCartLogo" != "")
						<button type="button" style="background-color: transparent; height: auto; margin: 0; overflow: visible; padding: 0; border: 0; width: auto;" onclick="javascript:$!{masterAttrJS}buyItem(this.form,$!storeitemID, this.form.qty_${itemCnt}.value);" #if(!$isAvailable)disabled#end><img src="$!addShCartLogo" border="0"></button>
					#else
						<input #if(!$isAvailable)disabled#end type="button" value="#plabel($label_addtocart "Add to Shopping Cart")" onClick="javascript:$!{masterAttrJS}buyItem(this.form,$!storeitemID, this.form.qty_${itemCnt}.value)">
					#end
					## Add To Wishlist
					#if($!wishlistEnabled == 'Y') <input #if(!$isAvailable)disabled#end type="button" value="#plabel($label_addwishlist "Add to Wish List")" onClick="javascript:$!{masterAttrJS}addItemToWishlist(this.form, $!storeitemID, this.form.qty_${itemCnt}.value)"> #end
					## Add To Giftregistry
					#if($!giftregistryEnabled == 'Y') <input #if(!$isAvailable)disabled#end type="button" value="#plabel($label_addgiftregistry "Add to Gift Registry")" onClick="javascript:$!{masterAttrJS}addItemToGiftregistry(this.form, $!storeitemID, this.form.qty_${itemCnt}.value)"> #end
				#end
				</TD>
			</TR>
		</TABLE>
	</TD>
		
	### Divide the count by the row limit
	#set($endmod = $itemCnt % $rowLimit)
	
	### End the row.
	#if($endmod == 0)
	</tr>
	<!-- End Product line 1-->
	#end
#end

#if($showitemresult.size() == 0)
<tr>
	<td colspan="5" align="center">#plabel($label_notitemsincat "There were no results found for you search query.").</td>
</tr>
#end
</table>
<!-- END REPEAT -->	
