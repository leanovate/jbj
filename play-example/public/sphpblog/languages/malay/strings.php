<?php
	// Malay Language Translation(s)
	// (c) 2004 Mohammad Fahmi Adam, farisi <at> hotmail <dot> com (0.3.7)
	
	function sb_language( $page ="") {
		global $lang_string;
			
		// Language: Malay
		$lang_string['language'] = 'malay';
		
		$lang_string[ 'locale' ] = array('ms_MY','malay');
		$lang_string['rss_locale'] = 'ms-MY'; // New 0.4.8
		
		// ISO Charset: ISO-8859-1
		$lang_string['html_charset'] = 'ISO-8859-1';
		$lang_string['php_charset'] = 'ISO-8859-1';
		
		setlocale(LC_TIME, $lang_string['locale'] ); // <-- New 0.3.7
		
	}
		
?>
