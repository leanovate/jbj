<?php
	// Spanish Language Translation(s)
	// (c) 2004 Javier Gutiérrez Chamorro (Guti), guti <at> ya <dot> com (0.4.7)
	//  Retocado por Cristian Olate, cristian <dot> olate <at> gmail <dot> com (0.4.8)
    //  Retocado por Montse Treviño mtrevim <at> gmail <dot> com  (0.5.1) [2008]
	
	function sb_language( $page ="") {
		global $lang_string;
		
		// Language: Spanish
		$lang_string['language'] = 'spanish';
		$lang_string[ 'locale' ] = array('es_ES', 'esp', 'spanish');
		$lang_string['rss_locale'] = 'es-ES'; // New 0.4.8
		
		// ISO Charset: ISO-8859-1
		$lang_string['html_charset'] = 'ISO-8859-1';
		$lang_string['php_charset'] = 'ISO-8859-1';
		setlocale( LC_TIME, $lang_string['locale'] );
		
	}
?>
