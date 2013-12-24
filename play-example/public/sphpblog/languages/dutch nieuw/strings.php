<?php
	// Dutch Language File
	// (c) 2004 S. Klippert, klippy@users.sourceforge.net
	//
	// Simple PHP Version: 0.3.7
	// Language Version:   0.3.7.1
	
	
	function sb_language( $page ="") {
		global $language, $html_charset, $php_charset, $lang_string;
			
		// Language: Dutch
		$lang_string['language'] = 'Nederlands';
		
		$lang_string[ 'locale' ] = array('nl_NL');
		$lang_string['rss_locale'] = 'nl-NL'; // New 0.4.8
		
		// ISO Charset: ISO-8859-1
		$lang_string['html_charset'] = 'ISO-8859-1';
		$lang_string['php_charset'] = 'ISO-8859-1';
		
		setlocale(LC_TIME, $lang_string['locale'] ); // <-- New 0.3.7
		
	}
		
?>
